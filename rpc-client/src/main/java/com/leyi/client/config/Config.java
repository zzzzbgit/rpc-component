package com.leyi.client.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Accessors(chain = true)
public class Config {

    /*定义socketInfo信息*/
    private CopyOnWriteArrayList<InetSocketAddress> socketInfos = new CopyOnWriteArrayList<>();

    /*可用socketInfo集合*/
    private CopyOnWriteArrayList<InetSocketAddress> aliveHost = new CopyOnWriteArrayList<>();

    /*Netty最大连接数*/
    private int maxConnections;

    /*连接超时时间*/
    private int connectTimeout = 10;

    /*默认重试次数*/
    private int defaultRetries = 3;

    /*动态加载时间间隔*/
    private int dynamicInterval = 30;

    /*客户端 id*/
    private String clientId;

    /*身份认证开关*/
    private boolean authEnable = false;

    /*auth Key*/
    private String authKey;

    /*secret Key*/
    private byte[] secretKey;

}
