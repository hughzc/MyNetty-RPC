/**
 * @(#)SerializationTest.java, 2020/7/13.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SerializationTest {
    @Autowired
    Serialization serialization;
    @Test
    public void testHession(){
        HessionSerialization serialization = new HessionSerialization();
        Person p = new Person(100,"testqdhksjhfsadfhsfsfijsifdjsfisncisfnasifadsfisj");
        byte[] data = serialization.serialize(p);
        System.out.println("Hession length: "+data.length);
        long t1 = System.currentTimeMillis();
        Person p2 = serialization.deserialize(data, Person.class);
        long t2 = System.currentTimeMillis();
        System.out.println("Hession time: "+(t2-t1));
        Assert.assertEquals(p.getAge(),p2.getAge());
        Assert.assertEquals(p.getName(),p2.getName());
    }
    @Test
    public void testBean(){
        System.out.println(serialization);
        Assert.assertNull(serialization);
    }

    @Test
    public void testProtubuf(){
        Serialization serialization = new ProtostuffSerialization();
        Person p = new Person(100,"testqdhksjhfsadfhsfsfijsifdjsfisncisfnasifadsfisj");
        byte[] data = serialization.serialize(p);
        System.out.println("Protobuf length: "+data.length);
        long t1 = System.currentTimeMillis();
        Person p2 = serialization.deserialize(data,Person.class);
        long t2 = System.currentTimeMillis();
        System.out.println("Protobuf time: "+(t2-t1));
        Assert.assertEquals(p.getAge(),p2.getAge());
        Assert.assertEquals(p.getName(),p2.getName());
    }

    @Test
    public void testProtubuf2(){
        Serialization serialization = new ProtostuffSerialization();
        Person p = new Person(100,"testqdhksjhfsadfhsfsfijsifdjsfisncisfnasifadsfisj");
        byte[] data = serialization.serialize(p);
        System.out.println("Protobuf no concurrentMap length: "+data.length);
        long t1 = System.currentTimeMillis();
        Person p2 = serialization.deserialize(data,Person.class);
        long t2 = System.currentTimeMillis();
        System.out.println("Protobuf no concurrentMap time: "+(t2-t1));
        Assert.assertEquals(p.getAge(),p2.getAge());
        Assert.assertEquals(p.getName(),p2.getName());
    }


    @Test
    public void testProtubuf3(){
        Serialization serialization = new ProtostuffSerialization();
        Person p = new Person(100,"testqdhksjhfsadfhsfsfijsifdjsfisncisfnasifadsfisj");
        byte[] data = serialization.serialize(p);
        System.out.println("Protobuf Objenis length: "+data.length);
        long t1 = System.currentTimeMillis();
        Person p2 = serialization.deserialize(data,Person.class);
        long t2 = System.currentTimeMillis();
        System.out.println("Protobuf Objenis time: "+(t2-t1));
        Assert.assertEquals(p.getAge(),p2.getAge());
        Assert.assertEquals(p.getName(),p2.getName());
    }

    @Test
    public void testJavaSerialization(){
        Serialization serialization = new JavaSeriazation();
        Person p = new Person(100,"testqdhksjhfsadfhsfsfijsifdjsfisncisfnasifadsfisj");
        byte[] data = serialization.serialize(p);
        System.out.println("Java length: "+data.length);
        long t1 = System.currentTimeMillis();
        Person p2 = serialization.deserialize(data,Person.class);
        long t2 = System.currentTimeMillis();
        System.out.println("Java time: "+(t2-t1));
        Assert.assertEquals(p.getAge(),p2.getAge());
        Assert.assertEquals(p.getName(),p2.getName());
    }
}