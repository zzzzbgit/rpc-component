package com.leyi.server;

import com.leyi.server.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

    private final ServerConfig config;

    public Server(ServerConfig config) {
        this.config = config;
    }

    public void start(Object... servers) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(config.getMaxConnections());

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .channel(NioServerSocketChannel.class)
                    //自定义一个初始化类
                    .childHandler(new ServerInitializer(servers));
            ChannelFuture channelFuture = serverBootstrap.bind(config.getPort()).sync();
            log.info("Server服务已启动, 监听端口:{}", config.getPort());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Server服务启动中断！",e);
            Thread.currentThread().interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
