/**
 * @(#)RpcProxy.java, 2020/7/17.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.client;

import com.xx.rpc.protocol.RpcRequest;
import com.xx.rpc.protocol.RpcResponse;
import com.xx.rpc.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

// 封装网络细节，通过动态代理生成代理类
public class RpcProxy implements InvocationHandler{
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    private static final Logger logger = LoggerFactory.getLogger(RpcProxy.class);

    public RpcProxy(String serverAddress){
        this.serverAddress = serverAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    // 动态代理
    public <T> T create(Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 创建并初始化Rpc请求
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParametersTypes(method.getParameterTypes());
        request.setParameters(args);

        if (serviceDiscovery != null){
            serverAddress = serviceDiscovery.discover();
        }

        String[] arr = serverAddress.split(":");
        String host = arr[0];
        int port = Integer.parseInt(arr[1]);

        // 通过客户端发送信息获取Rpc响应
        RpcClient client = new RpcClient(host,port);
        RpcResponse response = client.send(request);

        if (response.isError()){
            logger.error(response.getError());
        }else {
            return response.getResult();
        }
        return null;
    }
}