package com.leyi.client.builder.protocol.netty;

import com.leyi.base.coder.MessageEncoder;
import com.leyi.base.coder.ProtocolDecoder;
import com.leyi.base.constants.ConfigConstants;
import com.leyi.base.dto.RpcRequest;
import com.leyi.base.dto.RpcResponse;
import com.leyi.client.builder.pool.GeneralFixedPool;
import com.leyi.client.builder.protocol.Initializer;
import com.leyi.client.config.Config;
import com.leyi.client.builder.pool.PoolMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyInitializer extends Initializer {


    /*创建Bootstrap*/
    private Bootstrap BOOTSTRAP;

    /*Netty连接池*/
    private PoolMap<InetSocketAddress, GeneralFixedPool<InetSocketAddress, NettyElement>> poolMap;

    public NettyInitializer(Config config) {
        super(config);
    }

    /**
     * 执行初始化
     */
    @Override
    public void doInitProtocol() {
        this.BOOTSTRAP = new Bootstrap();
        /*
        bootstrap配置
         */
        this.BOOTSTRAP.group(new NioEventLoopGroup(8))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, false)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new MessageEncoder<>(RpcRequest.class)); //加入编码器
                        pipeline.addLast(new ProtocolDecoder<>(RpcResponse.class, ConfigConstants.MAX_FRAME_LENGTH,
                                ConfigConstants.LENGTH_FIELD_OFFSET,
                                ConfigConstants.LENGTH_FIELD_LENGTH,
                                ConfigConstants.LENGTH_ADJUSTMENT,
                                ConfigConstants.INITIAL_BYTES_TO_STRIP,
                                false)); //加入解码器
                        pipeline.addLast(new NettyHandler());
                    }
                });

        poolMap = new PoolMap<InetSocketAddress, GeneralFixedPool<InetSocketAddress, NettyElement>>() {
            @Override
            protected GeneralFixedPool<InetSocketAddress, NettyElement> newPool(InetSocketAddress key) {
                return new GeneralFixedPool<>(config.getMaxConnections(),
                        (isa) -> new NettyElement(BOOTSTRAP, isa, config.getConnectTimeout()),
                        key);
            }
        };

    }

    /**
     * 获取remote地址
     *
     * @param isa
     * @return
     */
    public GeneralFixedPool<InetSocketAddress, NettyElement> AcquirePool(InetSocketAddress isa) {
        return this.poolMap.get(isa);
    }

}
