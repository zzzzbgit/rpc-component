package com.shudun.client.builder.protocol.netty;

import com.shudun.base.dto.RpcRequest;
import com.shudun.base.dto.RpcResponse;
import com.shudun.base.enums.ErrorEnum;
import com.shudun.base.exception.ConnectionRefusedException;
import com.shudun.base.util.SecretKeyUtil;
import com.shudun.base.util.Tools;
import com.shudun.client.builder.protocol.Client;
import com.shudun.client.config.Config;
import io.netty.channel.Channel;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

@Slf4j
public class NettyCli extends Client {

    private final NettyInitializer initializer;

    public NettyCli(Config config) {
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
        FixedChannelPool pool = initializer.AcquirePool(isa);
        Future<Channel> future = pool.acquire();
        try {
            NettyReceiveTools.initReceiveMsg(requestId);
            Channel channel = future.get();
            channel.writeAndFlush(rpcRequest);
//            future.addListener((FutureListener<Channel>) futureListener -> {
//                if (futureListener.isSuccess()) {
//                    /*给服务端发送数据*/
//                    Channel channel = futureListener.getNow();
//                    channel.writeAndFlush(rpcRequest);
//                } else {//连接失败，立即返回
//                    NettyReceiveTools.setReceiveMsg(requestId, new ConnectionRefusedException(futureListener.cause().getMessage()));
//                    initializer.removeAddr(isa);
//                }
//            });
            /*获取服务端返回的数据*/
            RpcResponse response = NettyReceiveTools.waitReceiveMsg(requestId);
            if (response != null) {
                return response;
            }
            /*添加服务端中断重试机制*/
            try {
                Channel linkChannel = future.get();
                if (null != linkChannel) {
                    /*当server连接之后超时，宕机后返回false，当不宕机时返回true*/
                    if (!linkChannel.isOpen()) {
                        ConnectionRefusedException refusedException = new ConnectionRefusedException(ErrorEnum.CONNECTION_REFUSED.getMessage() + ":" + isa);
                        return new RpcResponse(requestId, refusedException.getClass(), refusedException);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Send check channel Error", e);
                ConnectionRefusedException refusedException = new ConnectionRefusedException(ErrorEnum.CONNECTION_REFUSED.getMessage() + ":" + isa);
                return new RpcResponse(requestId, refusedException.getClass(), refusedException);
            }
            throw new ConnectionRefusedException(ErrorEnum.CONNECTION_TIMEOUT.getMessage() + ":" + isa);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            Channel channel = future.getNow();
            if (null != channel) {
                pool.release(channel);
            }
        }
    }
}
