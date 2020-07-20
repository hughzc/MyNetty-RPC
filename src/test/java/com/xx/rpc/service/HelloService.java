/**
 * @(#)HelloService.java, 2020/7/8.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.service;

public interface HelloService {
    public String hello(String name);
    public String hello(Person person);
}