package com.shudun.client.builder.protocol.socket;

import com.shudun.client.builder.ClientBuilder;
import com.shudun.client.builder.RefreshAliveHostThread;
import com.shudun.client.builder.protocol.Client;
import com.shudun.client.config.Config;

public class SocketBuilder extends ClientBuilder {

    public SocketBuilder(Config config) {
        super(config);
    }

    /**
     * 初始化
     *
     * @return
     */
    @Override
    public Client doInitialize() {
        Client client = new SocketClient(config);
        RefreshAliveHostThread.Launch(client);
        return client;
    }
}
