/**
 * @(#)ServiceRegistryTest.java, 2020/7/8.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.registry;

import org.junit.Test;

import java.util.List;

public class ServiceDiscoveryTest {
//    @Test
//    public void test(){
//        String address = "39.99.157.11:2181";
//        ServiceRegistry registry = new ServiceRegistry(address);
//        ZooKeeper zk = registry.connectServer();
//    }

    @Test
    public void testRegister(){
        String data = "hello";
        String address = "39.99.157.11:2181";
        ServiceRegistry registry = new ServiceRegistry(address);
        registry.register(data);
        ServiceDiscovery discovery = new ServiceDiscovery(address);
        List<String> dataList = discovery.getDataList();
        for (String s : dataList) {
            System.out.print(s+" ");
        }

    }
}