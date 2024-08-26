package com.shudun.client.builder.protocol.socket;

import com.shudun.client.builder.protocol.Initializer;
import com.shudun.client.pool.FixedPool;
import com.shudun.client.pool.PoolMap;
import com.shudun.client.pool.element.impl.SocketClient;
import com.shudun.client.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class SocketInitializer extends Initializer {


    private PoolMap<InetSocketAddress, SocketClient> poolMap;


    public SocketInitializer(Config config) {
        super(config);
    }

    /**
     * 执行初始化
     */
    @Override
    public void doInitProtocol() {
        poolMap = new PoolMap<>(config.getMaxConnections(),
                (key) -> new SocketClient(key, config.getConnectTimeout()));
    }

    /**
     * 获取remote地址
     *
     * @param isa
     * @return
     */
    public FixedPool<InetSocketAddress, SocketClient> AcquirePool(InetSocketAddress isa) {
        return this.poolMap.get(isa);
    }

}
