package com.shudun.client.pool.element.impl;

import com.shudun.base.dto.RpcRequest;
import com.shudun.base.dto.RpcResponse;
import com.shudun.client.builder.protocol.netty.NettyReceiveTools;
import com.shudun.client.pool.element.PoolElement;
import com.shudun.client.pool.exception.PoolException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Netty客户端
 */
@Slf4j
public class NettyClient implements PoolElement<RpcRequest, RpcResponse> {

    /**
     * bootstrap
     */
    private Bootstrap bootstrap;

    /**
     * Channel
     */
    private Channel channel;

    /**
     * 是否已关闭
     */
    private volatile boolean closed;

    /**
     * 远程地址
     */
    private final InetSocketAddress isa;

    /**
     * 超时时间
     */
    private final int timeout;

    /**
     * 默认重置次数
     */
    private final int DEFAULT_RESET_TIMES = 3;

    /**
     * 构造
     *
     * @param isa     ip:port
     * @param timeout 超时时间
     */
    public NettyClient(Bootstrap bootstrap, InetSocketAddress isa, int timeout) {
        this.isa = isa;
        this.timeout = timeout;
        this.bootstrap = bootstrap;
        this.create();
        log.debug("NettyClient:{}.create Success!", this);
    }

    /**
     * 创建
     */
    private void create() {
        try {
            ChannelFuture future = bootstrap.connect(isa);
            future.await();
            channel = future.channel();
            this.closed = false;
        } catch (InterruptedException e) {
            log.error("NettyClient:{}.create Error!", this, e);
            throw new PoolException("NettyClient.create Error!", e);
        }
    }

    /**
     * 重置
     */
    @Override
    public void reSet() {
        close();
        create();
        log.debug("NettyClient:{}.reSet!", this);
    }

    /**
     * 重试到最大次数
     */
    @Override
    public void retry() {
        int retry = 1;
        while (retry <= DEFAULT_RESET_TIMES) {
            log.debug("NettyClient:{}.retry start! times:{}", this, retry);
            try {
                reSet();
                break;
            } catch (PoolException pe) {
                log.error("NettyClient:{}.retry Error! times:{}", this, retry, pe);
                retry++;
            }
        }
    }

    /**
     * 执行
     *
     * @param inData 输入数据
     * @return
     */
    @Override
    public RpcResponse execute(RpcRequest inData) {
        long requestId = inData.getRequestId();
        /*发送消息*/
        send(requestId, inData);
        return recv(requestId);
    }

    /**
     * 发送
     *
     * @param rpcRequest 发送数据
     * @throws IOException
     */
    private void send(long requestId, RpcRequest rpcRequest) {
        NettyReceiveTools.initReceiveMsg(requestId);
        channel.writeAndFlush(rpcRequest);
    }

    /**
     * 接收
     *
     * @return 接收数据
     * @throws IOException
     */
    private RpcResponse recv(long requestId) {
        return NettyReceiveTools.waitReceiveMsg(requestId);
    }

    @Override
    public boolean isHealth() {
        if (null != channel) {
            return channel.isOpen() && !this.closed;
        }
        return false;
    }

    /**
     * 关闭资源
     */
    @Override
    public void close() {
        try {
            this.closed = true;
            channel.close();
        } finally {
            this.channel = null;
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public InetSocketAddress getIsa() {
        return isa;
    }

    public int getTimeout() {
        return timeout;
    }
}
