/**
 * @(#)Serialization.java, 2020/7/12.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.util;

public interface Serialization {
    <T> byte[] serialize(T obj);
    <T> T deserialize(byte[] data, Class<T> cls);

}