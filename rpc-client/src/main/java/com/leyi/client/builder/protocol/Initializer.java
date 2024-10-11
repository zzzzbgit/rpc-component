package com.leyi.client.builder.protocol;

import com.leyi.client.config.Config;

import java.net.InetSocketAddress;

public abstract class Initializer {

    /*配置实体*/
    protected Config config;

    public Initializer(Config config) {
        this.config = config;
    }

    /**
     * 执行初始化
     */
    public abstract void doInitProtocol();

    /**
     * 移除remote地址
     * @param isa
     */
    public void removeAddr(InetSocketAddress isa) {
        if (this.config.getAliveHost().size() > 1) {
            this.config.getAliveHost().remove(isa);
        }
    }

}
