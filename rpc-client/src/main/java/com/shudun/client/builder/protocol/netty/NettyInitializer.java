package com.shudun.client.builder.protocol.netty;

import com.shudun.base.constants.ConfigConstants;
import com.shudun.client.builder.protocol.Initializer;
import com.shudun.client.coder.MessageEncoder;
import com.shudun.client.coder.ProtocolDecoder;
import com.shudun.client.config.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyInitializer extends Initializer {


    /*创建Bootstrap*/
    private Bootstrap BOOTSTRAP;

    /*Netty连接池*/
    private ChannelPoolMap<InetSocketAddress, FixedChannelPool> poolMap;

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
        this.BOOTSTRAP.group(new NioEventLoopGroup())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, false)
                .channel(NioSocketChannel.class);


        this.poolMap = new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>() {
            @Override
            protected FixedChannelPool newPool(InetSocketAddress key) {
                ChannelPoolHandler channelPoolHandler = new ChannelPoolHandler() {
                    @Override
                    public void channelReleased(Channel channel) {
                    }

                    @Override
                    public void channelAcquired(Channel channel) {
                    }

                    @Override
                    public void channelCreated(Channel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new MessageEncoder()); //加入编码器
                        pipeline.addLast(new ProtocolDecoder(ConfigConstants.MAX_FRAME_LENGTH,
                                ConfigConstants.LENGTH_FIELD_OFFSET,
                                ConfigConstants.LENGTH_FIELD_LENGTH,
                                ConfigConstants.LENGTH_ADJUSTMENT,
                                ConfigConstants.INITIAL_BYTES_TO_STRIP,
                                false)); //加入解码器
                        pipeline.addLast(new NettyHandler());
                    }
                };
                return new FixedChannelPool(BOOTSTRAP.remoteAddress(key), channelPoolHandler,
                        config.getMaxConnections());
            }
        };
    }

    /**
     * 获取remote地址
     *
     * @param isa
     * @return
     */
    public FixedChannelPool AcquirePool(InetSocketAddress isa) {
        return this.poolMap.get(isa);
    }

}
