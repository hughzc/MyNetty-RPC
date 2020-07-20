/**
 * @(#)HelloServiceTest.java, 2020/7/18.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.test;

import com.xx.rpc.client.RpcProxy;
import com.xx.rpc.service.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class HelloServiceTest {
    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void helloTest(){
        long start = System.currentTimeMillis();
        HelloService service = rpcProxy.create(HelloService.class);
        String  result = service.hello("world");
        long end = System.currentTimeMillis();
        System.out.println(result);
        System.out.println(end-start);
    }
}