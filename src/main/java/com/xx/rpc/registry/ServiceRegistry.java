/**
 * @(#)ServiceRegistry.java, 2020/7/8.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.registry;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

// 服务注册，使用zk
public class ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    public ServiceRegistry(String registryAddress){
        this.registryAddress = registryAddress;
    }

    // 服务注册
    public void register(String data){
        if (data != null){
            ZooKeeper zk = connectServer();
            if (zk != null){
                addRootNode(zk);
                createNode(zk,data);
            }
        }
    }

    // zk连接的方法
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

    // 若父节点没有，就进行创建
    private void addRootNode(ZooKeeper zk){
        try {
            Stat stat = zk.exists(Constant.ZK_REGISTRY_PATH, false);
            if (stat == null){
                zk.create(Constant.ZK_REGISTRY_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            logger.error(e.toString());
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
    }

    // 创建子节点，临时有序
    private void createNode(ZooKeeper zk, String data){
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("create zk node ({} -> {})",path,data);
        } catch (KeeperException e) {
            logger.error(e.toString());
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
    }

    public List<String> getNode(ZooKeeper zk){
        List<String> children = null;
        try {
            children = zk.getChildren(Constant.ZK_REGISTRY_PATH, false);
            return children;
        } catch (KeeperException e) {
            logger.error(e.toString());
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
        return children;
    }
}