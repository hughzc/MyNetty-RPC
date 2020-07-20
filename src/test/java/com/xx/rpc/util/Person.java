/**
 * @(#)Person.java, 2020/7/13.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.util;

import lombok.Data;

import java.io.Serializable;
@Data
public class Person implements Serializable {
    private String name;
    private int age;
    public Person(int age, String name){
        this.age = age;
        this.name = name;
    }
}