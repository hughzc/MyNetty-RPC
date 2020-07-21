/**
 * @(#)RpcServer.java, 2020/7/8.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.server;

import com.xx.rpc.protocol.RpcDecoder;
import com.xx.rpc.protocol.RpcEncoder;
import com.xx.rpc.protocol.RpcRequest;
import com.xx.rpc.protocol.RpcResponse;
import com.xx.rpc.registry.ServiceRegistry;
import com.xx.rpc.service.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcServer implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private Map<String, Object> handlerMap = new HashMap<>();// 存放接口名与服务对象之间的映射关系

    private String serverAddress;

    private ServiceRegistry serviceRegistry;// IOC注入

    // TODO 添加线程池去处理，不用每次来个客户端请求就进行一次新的连接
    private static ThreadPoolExecutor threadPoolExecutor;

    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry){
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    // 加载被RpcService注解的类
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 将注解修饰的类用Bean托管
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (null != serviceBeanMap){
            for (Object serviceBean : serviceBeanMap.values()){
                // 获取类上注解的value值，即类的Class
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                logger.info("load service interface impl {}",interfaceName);
                handlerMap.put(interfaceName,serviceBean);
            }
        }

    }

    // 进行网络连接
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();// selector
        EventLoopGroup workerGroup = new NioEventLoopGroup();// 线程组
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 初始化通道
                            // 增加处理的责任链
                            channel.pipeline()
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RpcHandler(handlerMap));// 处理rpc请求
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            // 获取服务地址与端口号
            String[] arr = serverAddress.split(":");
            String host = arr[0];
            int port = Integer.parseInt(arr[1]);

            ChannelFuture future = bootstrap.bind(host,port).sync();// 阻塞住
            logger.info("server started on host {} and port {}",host,port);

            // 服务地址注册
            if (serviceRegistry != null){
                serviceRegistry.register(serverAddress);
            }

            // 关闭服务器
            future.channel().closeFuture().sync();
        }finally {
            // 关闭资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    // 使用线程池来执行任务
    // lazy load加载
    public static void submit(Runnable task){
        if (threadPoolExecutor == null){
            synchronized (RpcServer.class){
                if (threadPoolExecutor == null){
                    threadPoolExecutor = new ThreadPoolExecutor(16,16,600L, TimeUnit.SECONDS,
                            new ArrayBlockingQueue<Runnable>(65536));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }
}