/**
 * @(#)ProtobufSerialization.java, 2020/7/13.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerialization implements Serialization {
    // 存放Schema的map
    Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(cls);
        LinkedBuffer buffer  = null;
        try {
            buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
            return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }finally {
            if (buffer != null){
                buffer.clear();
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        Schema<T> schema = getSchema(cls);
        T message = schema.newMessage();// 可以用objenesis来做
        ProtostuffIOUtil.mergeFrom(data,message,schema);
        return message;
    }

    private <T> Schema<T> getSchema(Class<T> cls){
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null){
            schema = RuntimeSchema.getSchema(cls);
            // 做缓存
            cachedSchema.put(cls,schema);
        }
        return schema;
    }
}