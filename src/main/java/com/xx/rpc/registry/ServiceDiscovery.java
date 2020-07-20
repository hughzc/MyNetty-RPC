/**
 * @(#)ServiceDiscovery.java, 2020/7/11.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.registry;

import lombok.Getter;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceDiscovery {
    Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    private ZooKeeper zooKeeper;

    @Getter
    // volatile修饰
    private volatile List<String> dataList = new ArrayList<>();

    // init
    public ServiceDiscovery(String registryAddress){
        this.registryAddress = registryAddress;
        zooKeeper = connectServer();
        if (zooKeeper != null){
            watchNode(zooKeeper);
        }
    }

    // 如果节点有变化，就加入到list中
    private void watchNode(ZooKeeper zk){
        try {
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged){
                        // 当有变化时，重新调用
                        watchNode(zk);
                    }
                }
            });
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            logger.info("node data: {}",dataList);
            this.dataList = dataList;
        } catch (KeeperException e) {
            logger.error(e.toString());
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
    }

    // 服务发现，可以使用负载均衡算法
    public String discover(){
        String data = null;
        int size = dataList.size();
        if (size > 0){
            // 如果只有一个服务器，选择那个
            if (size == 1){
                data = dataList.get(0);
                logger.info("using only data {}",data);
            }else {
                // 随机选择
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                logger.info("using random data {}",data);
            }
        }
        return data;
    }

    public ZooKeeper connectServer(){
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected){
                        logger.info("connect zk server");
                        latch.countDown();
                    }
                }
            });
            // 阻塞住
            latch.await();
        } catch (IOException e) {
            logger.error("",e);
        } catch (InterruptedException e) {
            logger.error("",e);
        }
        return zk;
    }

    public void stop(){
        if (zooKeeper != null){
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                logger.error(e.toString());
            }
        }
    }
}