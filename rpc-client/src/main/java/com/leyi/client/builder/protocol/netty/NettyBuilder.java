package com.leyi.client.builder.protocol.netty;

import com.leyi.client.builder.RefreshAliveHostThread;
import com.leyi.client.builder.protocol.Client;
import com.leyi.client.builder.ClientBuilder;
import com.leyi.client.config.Config;

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
        Client client = new NettyClient(config);
        RefreshAliveHostThread.Launch(client);
        return client;
    }
}
