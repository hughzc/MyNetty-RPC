/**
 * @(#)RpcRequest.java, 2020/7/11.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.protocol;

import lombok.Data;

@Data
public class RpcRequest {
    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parametersTypes;

    private Object[] parameters;
}