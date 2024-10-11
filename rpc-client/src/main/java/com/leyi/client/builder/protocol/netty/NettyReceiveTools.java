package com.leyi.client.builder.protocol.netty;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.leyi.base.dto.RpcResponse;
import com.leyi.base.exception.ConnectionRefusedException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 消息接收工具
 */
public class NettyReceiveTools {

    private static int CONNECTTIMEOUT = 10;

    /**
     * 设置响应超时时间
     * @param connectTimeout
     */
    public static void init(int connectTimeout) {
        CONNECTTIMEOUT = connectTimeout;
    }

    /**
     * 响应消息缓存
     */
    private static Cache<Long, CompletableFuture<RpcResponse>> responseMsgCache = CacheBuilder.newBuilder()
            .maximumSize(200000)
            .expireAfterWrite(1000, TimeUnit.SECONDS)
            .build();


    /**
     * 等待响应消息
     * @param key 消息唯一标识
     * @return ReceiveDdcMsgVo
     */
    public static RpcResponse waitReceiveMsg(Long key) {

        try {
            //设置超时时间
            RpcResponse vo = Objects.requireNonNull(responseMsgCache.getIfPresent(key))
                .get(CONNECTTIMEOUT, TimeUnit.SECONDS);
            //删除key
            responseMsgCache.invalidate(key);
            if (null == vo) System.out.println("响应超时,sn={"+key+"}");
            return vo;
        } catch (Exception e) {
            System.out.println("获取数据异常,sn={"+key+"},msg=null");
            return null;
        }

    }

    /**
     * 初始化响应消息的队列
     * @param key 消息唯一标识
     */
    public static void initReceiveMsg(Long key) {
        responseMsgCache.put(key,new CompletableFuture<>());
    }

    /**
     * 设置响应消息
     * @param key 消息唯一标识
     */
    public static void setReceiveMsg(Long key, RpcResponse res) {
        if(responseMsgCache.getIfPresent(key) != null){
            responseMsgCache.getIfPresent(key).complete(res);
            return;
        }
    }

    /**
     * 设置响应消息
     * @param key 消息唯一标识
     */
    public static void setReceiveMsg(Long key, ConnectionRefusedException cre) {
        if(responseMsgCache.getIfPresent(key) != null){
            responseMsgCache.getIfPresent(key).complete(new RpcResponse(key, cre.getClass(), cre));
            return;
        }
    }

}
