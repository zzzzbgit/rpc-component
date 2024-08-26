package com.shudun.client.builder.protocol.netty;

import com.shudun.client.builder.ClientBuilder;
import com.shudun.client.builder.RefreshAliveHostThread;
import com.shudun.client.builder.protocol.Client;
import com.shudun.client.config.Config;

public class NettyBuilder extends ClientBuilder {

    public NettyBuilder(Config config) {
        super(config);
    }

    /**
     * 初始化
     *
     * @return
     */
    @Override
    public Client doInitialize() {
        Client client = new NettyCli(config);
        RefreshAliveHostThread.Launch(client);
        return client;
    }
}
