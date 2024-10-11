package com.leyi.client.builder.protocol.socket;

import com.leyi.client.builder.RefreshAliveHostThread;
import com.leyi.client.builder.protocol.Client;
import com.leyi.client.config.Config;
import com.leyi.client.builder.ClientBuilder;

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
