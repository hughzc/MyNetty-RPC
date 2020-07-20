/**
 * @(#)HelloRPC.java, 2020/7/8.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.service;
// 使用RPC注解，spring启动时被扫描
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(String name) {
        return "HelloService "+name;
    }

    @Override
    public String hello(Person person) {
        return "HelloService "+person.getName();
    }
}