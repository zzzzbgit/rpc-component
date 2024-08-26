package com.shudun.base.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class RpcRequest implements Serializable {

    /*请求id*/
    private long requestId;
    /*客户端编码*/
    private String clientId;
    /*调用类名*/
    private String interfaceName;
    /*调用方法名*/
    private String methodName;
    /*方法参数类型*/
    private Class<?>[] parameterTypes;
    /*方法参数*/
    private List<Container> parameters;
    /*时间戳*/
    private long time;
    /*原hashCode*/
    private int hasCode;

    private String authKey;

    private String signValue;

    public RpcRequest(String interfaceName, String methodName, Class<?>[] parameterTypes, List<Container> parameters) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.time = System.currentTimeMillis();
        this.hasCode = parameters.hashCode();
    }
}
