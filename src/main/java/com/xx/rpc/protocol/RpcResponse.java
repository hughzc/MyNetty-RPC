/**
 * @(#)RpcResponse.java, 2020/7/11.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.protocol;

import lombok.Data;

@Data
public class RpcResponse {
    private String requestId;

    private String error;

    private Object result;

    public boolean isError(){
        return error != null;
    }
}