package com.leyi.base.dto;

import com.leyi.base.util.SerializingUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@Accessors(chain = true)
public class Request implements Serializable {

    /*请求id*/
    private long requestId;
    /*客户端编码*/
    private String clientId;
    /*调用类名*/
    private String interfaceName;
    /*调用方法名*/
    private String methodName;
    /*方法参数类型*/
    private String[] parameterTypes;
    /*方法参数*/
    private Object[] parameters;
    /*时间戳*/
    private long time;
    /*原hashCode*/
    private int hasCode;

    private String authKey;

    private String signValue;

    public Request(String interfaceName, String methodName, String[] parameterTypes, Object[] parameters) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.time = System.currentTimeMillis();
        this.hasCode = parameters.hashCode();
    }

    public static void main(String[] args) {
        ArrayList<Object> os = new ArrayList<>();
        os.add(12);
        os.add(null);
        os.add("dd");
        byte[] bytes = SerializingUtil.serializeToByte(os);
        ArrayList<Object> objects = SerializingUtil.deserializeFromByte(bytes, ArrayList.class);
        System.out.println(objects);

    }
}
