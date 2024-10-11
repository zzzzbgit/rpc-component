package com.leyi.server.config;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServerConfig {

    private int port;

    private int maxConnections;
}
