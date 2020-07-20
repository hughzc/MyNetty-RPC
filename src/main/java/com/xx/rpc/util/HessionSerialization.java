/**
 * @(#)HessionSerialization.java, 2020/7/12.
 * <p/>
 * Copyright 2020 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xx.rpc.util;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessionSerialization implements Serialization{
    private Logger logger = LoggerFactory.getLogger(HessionSerialization.class);

    public <T> byte[] serialize(T obj){
        ByteArrayOutputStream bos = null;
        HessianOutput hessianOutput = null;

        try {
            bos = new ByteArrayOutputStream();
            // hession序列化输出
            hessianOutput = new HessianOutput(bos);
            hessianOutput.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            logger.error(e.toString());
        }finally {
            try {
                bos.close();
                hessianOutput.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
        return null;

    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        ByteArrayInputStream bis = null;
        HessianInput hessianInput = null;
        try {
            bis = new ByteArrayInputStream(data);
            hessianInput = new HessianInput(bis);
            return (T)hessianInput.readObject();

        }catch (IOException e){
            logger.info(e.toString());
        }finally {
            try {
                bis.close();
                hessianInput.close();
            } catch (IOException e) {
                logger.info(e.toString());
            }
        }
        return null;
    }
}