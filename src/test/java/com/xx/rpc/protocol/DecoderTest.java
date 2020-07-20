/**
 * @(#)DecoderTest.java, 2020/7/14.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.protocol;

import com.xx.rpc.util.Person;
import com.xx.rpc.util.ProtostuffSerialization;
import com.xx.rpc.util.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

public class DecoderTest {
    @Test
    public void testEncoder(){
        EmbeddedChannel channel = new EmbeddedChannel(new RpcEncoder(Person.class));
        Person p = new Person(100,"test");
        channel.writeOutbound(p);
        // 用byte读
        ByteBuf buf = channel.readOutbound();
        // 读数据
        int len = buf.readInt();
        Serialization serialization = new ProtostuffSerialization();
        byte[] data = new byte[len];
        buf.readBytes(data);
        Person p2 = serialization.deserialize(data,Person.class);
        Assert.assertEquals(p.getAge(),p2.getAge());
        Assert.assertEquals(p.getName(),p2.getName());
    }


    @Test
    public void testDecoder(){
        EmbeddedChannel channel = new EmbeddedChannel(new RpcEncoder(Person.class));
        channel.pipeline().addLast(new RpcEncoder(Person.class))
                .addLast(new RpcDecoder(Person.class));
        Person p = new Person(100,"test");
        ByteBuf buf = Unpooled.buffer();
        byte[] data = new ProtostuffSerialization().serialize(p);
        buf.writeInt(data.length);
        buf.writeBytes(data);
        channel.writeInbound(buf.duplicate());
        // 读信息
        Person p2 = (Person)channel.readInbound();
        Assert.assertEquals(p.getAge(),p2.getAge());
        Assert.assertEquals(p.getName(),p2.getName());
    }
}