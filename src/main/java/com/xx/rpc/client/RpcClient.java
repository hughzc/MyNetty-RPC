/**
 * @(#)RpcClient.java, 2020/7/17.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.client;

import com.xx.rpc.protocol.RpcDecoder;
import com.xx.rpc.protocol.RpcEncoder;
import com.xx.rpc.protocol.RpcRequest;
import com.xx.rpc.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private String host;

    private int port;

    private RpcResponse response;

    private CountDownLatch latch = new CountDownLatch(1);

    public RpcClient(String host, int port){
        this.host = host;
        this.port = port;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        this.response = response;
        // 进行计数
        latch.countDown();
    }

    public RpcResponse send(RpcRequest request) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(RpcClient.this);// 使用RpcClient发送请求
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future = bootstrap.connect(host,port).sync();//阻塞住
            future.channel().writeAndFlush(request).sync();

            logger.info("Waiting for response from request "+request.getRequestId());
            // 避免在没有拿到response就进行了返回
            latch.await();
            logger.info("receive response from request "+request.getRequestId());
            if (response != null){
                future.channel().closeFuture().sync();
            }
            return response;
        }finally {
            group.shutdownGracefully();
        }
    }
}