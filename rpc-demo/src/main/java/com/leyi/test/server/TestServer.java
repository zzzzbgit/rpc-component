package com.leyi.test.server;

import com.leyi.server.Server;
import com.leyi.server.config.ServerConfig;
import com.leyi.test.server.impl.TestServiceImpl;

public class TestServer {

    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(9992).setMaxConnections(64);
        new Server(serverConfig).start(new TestServiceImpl());
    }
}
