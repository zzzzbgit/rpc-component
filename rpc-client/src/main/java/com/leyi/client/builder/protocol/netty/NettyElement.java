package com.leyi.client.builder.protocol.netty;

import com.leyi.base.dto.RpcRequest;
import com.leyi.base.dto.RpcResponse;
import com.leyi.client.builder.pool.element.PoolElement;
import com.leyi.client.builder.pool.exception.PoolException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Netty element
 */
@Slf4j
public class NettyElement extends PoolElement<RpcRequest, RpcResponse> {

    /*Bootstrap*/
    private final Bootstrap bootstrap;

    /*Channel*/
    private Channel channel;

    /*remote address*/
    private final InetSocketAddress isa;

    /**
     * constructor
     *
     * @param bootstrap Bootstrap
     * @param isa       remote address
     * @param timeout   timeout
     */
    public NettyElement(Bootstrap bootstrap, InetSocketAddress isa, int timeout) {
        this(bootstrap, isa, timeout, 3);
    }

    /**
     * constructor
     *
     * @param bootstrap  Bootstrap
     * @param isa        remote address
     * @param timeout    timeout
     * @param retryTimes retry times
     */
    public NettyElement(Bootstrap bootstrap, InetSocketAddress isa, int timeout, int retryTimes) {
        this.isa = isa;
        this.timeout = timeout;
        this.retryTimes = retryTimes;
        this.bootstrap = bootstrap;
        this.init();
    }

    /**
     * get
     *
     * @return
     */
    public Channel get() {
        return this.channel;
    }

    /**
     * init
     */
    @Override
    public void init() {
        try {
            ChannelFuture future = this.bootstrap.connect(isa);
            future.await();
            this.channel = future.channel();
            this.closed = false;
        } catch (InterruptedException e) {
            log.error("NettyElement:{}.init Error!", this, e);
            throw new PoolException("NettyElement.init Error!", e);
        }
    }

    /**
     * execute
     *
     * @param inData input param
     * @return
     */
    @Override
    public RpcResponse execute(RpcRequest inData) {
        long requestId = inData.getRequestId();
        /*发送消息*/
        try {
            send(requestId, inData);
            return recv(requestId);
        } catch (Exception e) {
            log.error("NettyElement:{}.execute Error! 待定", this, e);
            throw new PoolException("NettyElement.execute Error!", e);
        }
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
}
