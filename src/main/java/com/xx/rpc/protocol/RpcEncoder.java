/**
 * @(#)RpcEncoder.java, 2020/7/12.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.protocol;

import com.xx.rpc.util.ProtostuffSerialization;
import com.xx.rpc.util.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

// 编码器
public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;


    public RpcEncoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)){
            Serialization serialization = new ProtostuffSerialization();
            byte[] data = serialization.serialize(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}