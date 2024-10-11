package com.leyi.client.builder.protocol.netty;

import com.leyi.base.dto.RpcRequest;
import com.leyi.base.dto.RpcResponse;
import com.leyi.base.enums.ErrorEnum;
import com.leyi.base.exception.ConnectionRefusedException;
import com.leyi.base.util.SecretKeyUtil;
import com.leyi.base.util.Tools;
import com.leyi.client.builder.protocol.Client;
import com.leyi.client.builder.pool.GeneralFixedPool;
import com.leyi.client.config.Config;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyClient extends Client {

    private final NettyInitializer initializer;

    public NettyClient(Config config) {
        super(config);
        this.initializer = new NettyInitializer(config);
        this.initializer.doInitProtocol();
        resetAliveHost();
    }

    @Override
    public RpcResponse Send(RpcRequest rpcRequest, InetSocketAddress isa) {
        /*获取唯一标识*/
        long requestId = Tools.GenSerialNumber();
        rpcRequest.setRequestId(requestId)
                .setClientId(config.getClientId())
                .setAuthKey(config.getAuthKey());
        if (config.isAuthEnable()) {
            rpcRequest.setSignValue(SecretKeyUtil.sign(config.getSecretKey(), rpcRequest));
        }
        GeneralFixedPool<InetSocketAddress, NettyElement> pool = initializer.AcquirePool(isa);
        NettyElement element = pool.acquire();
        try {
            /*获取服务端返回的数据*/
            RpcResponse response = element.execute(rpcRequest);
            if (response != null) {
                return response;
            }
            /*添加服务端中断重试机制*/
            Channel linkChannel = element.get();
            if (null != linkChannel) {
                /*当server连接之后超时，宕机后返回false，当不宕机时返回true*/
                if (!linkChannel.isOpen()) {
                    ConnectionRefusedException refusedException = new ConnectionRefusedException(ErrorEnum.CONNECTION_REFUSED.getMessage() + ":" + isa);
                    return new RpcResponse(requestId, refusedException.getClass(), refusedException);
                }
            }
            throw new ConnectionRefusedException(ErrorEnum.CONNECTION_TIMEOUT.getMessage() + ":" + isa);
        } finally {
            pool.release(element);
        }
    }
}
