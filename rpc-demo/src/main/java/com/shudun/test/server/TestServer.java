package com.shudun.test.server;

import com.shudun.server.Server;
import com.shudun.server.config.ServerConfig;
import com.shudun.test.server.impl.TestServiceImpl;

public class TestServer {

    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(9991).setMaxConnections(32);
        new Server(serverConfig).start(new TestServiceImpl());
    }
}
