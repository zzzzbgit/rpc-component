package com.leyi.base.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    /*请求id*/
    private long requestId;
    /*方法参数类型*/
    private Class<?> returnType;
    /*方法参数*/
    private Object returnData;

    public RpcResponse(long requestId, Class<?> returnType, Object returnData) {
        this.requestId = requestId;
        this.returnType = returnType;
        this.returnData = returnData;
    }
}
