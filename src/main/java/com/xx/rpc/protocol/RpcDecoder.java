/**
 * @(#)RpcDecoder.java, 2020/7/14.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.protocol;

import com.xx.rpc.util.ProtostuffSerialization;
import com.xx.rpc.util.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 防止黏包
        if (in.readableBytes() < 4)
            return;
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0)
            ctx.close();
        if (in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Serialization serialization = new ProtostuffSerialization();
        Object obj = serialization.deserialize(data,genericClass);
        out.add(obj);
    }
}