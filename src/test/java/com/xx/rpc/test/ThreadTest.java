/**
 * @(#)ThreadTest.java, 2020/7/21.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.test;

import com.xx.rpc.client.RpcProxy;
import com.xx.rpc.registry.ServiceDiscovery;
import com.xx.rpc.service.HelloService;

public class ThreadTest {
    // 当线程执行任务过多，存在OOM
    public static void main(String[] args) throws InterruptedException {
        ServiceDiscovery discovery = new ServiceDiscovery("39.99.157.11:2181");
        RpcProxy rpcProxy = new RpcProxy(discovery);

        int threadNum = 10;
        int requestNum = 50;
        Thread[] threads = new Thread[threadNum];

        long startTime = System.currentTimeMillis();
        // sync call
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();
                    for (int j = 0; j < requestNum; j++) {
                        HelloService syncClient = rpcProxy.create(HelloService.class);
                        String result = syncClient.hello(Integer.toString(j));
                        if (!result.equals("HelloService "+j)){
                            System.out.println("error= "+result);
                        }
                    }
                }
            });
            threads[i].start();
        }
        // 等待线程完成
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        long timeCost = System.currentTimeMillis() - startTime;
        String msg = String.format("sync call total-time-cost:%sms, req/s=%s",timeCost,((double)(requestNum * threadNum)) / timeCost * 1000);
        System.out.println(msg);
    }
}