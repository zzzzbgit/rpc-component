package com.leyi.client.builder.protocol.socket;

import com.leyi.client.builder.pool.GeneralFixedPool;
import com.leyi.client.builder.protocol.Initializer;
import com.leyi.client.config.Config;
import com.leyi.client.builder.pool.PoolMap;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class SocketInitializer extends Initializer {


    private PoolMap<InetSocketAddress, GeneralFixedPool<InetSocketAddress, SocketElement>> poolMap;


    public SocketInitializer(Config config) {
        super(config);
    }

    /**
     * 执行初始化
     */
    @Override
    public void doInitProtocol() {
        poolMap = new PoolMap<InetSocketAddress, GeneralFixedPool<InetSocketAddress, SocketElement>>() {
            @Override
            protected GeneralFixedPool<InetSocketAddress, SocketElement> newPool(InetSocketAddress key) {
                return new GeneralFixedPool<>(config.getMaxConnections(),
                        (isa)-> new SocketElement(isa, config.getConnectTimeout()),
                        key);
            }
        };
    }

    /**
     * 获取remote地址
     *
     * @param isa
     * @return
     */
    public GeneralFixedPool<InetSocketAddress, SocketElement> AcquirePool(InetSocketAddress isa) {
        return this.poolMap.get(isa);
    }

}
