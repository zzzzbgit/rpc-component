package com.shudun.client.builder.protocol;

import com.shudun.base.constants.ConfigConstants;
import com.shudun.base.dto.Container;
import com.shudun.base.dto.RpcRequest;
import com.shudun.base.dto.RpcResponse;
import com.shudun.base.exception.ConnectionRefusedException;
import com.shudun.client.builder.balance.LoadBalance;
import com.shudun.client.builder.balance.Polling;
import com.shudun.client.config.Config;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Client {

    protected LoadBalance balance = new Polling();

    protected final Config config;

    protected Client(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    public abstract RpcResponse Send(RpcRequest rpcRequest, InetSocketAddress isa);

    public InetSocketAddress next() {
        return balance.next(this.config.getAliveHost());
    }

    public void resetAliveHost() {
        ArrayList<InetSocketAddress> tempAliveHost = new ArrayList<>();
        for (InetSocketAddress socket : this.config.getSocketInfos()) {
            RpcRequest rpcRequest = new RpcRequest(ConfigConstants.LOGIN_INTERFACE, ConfigConstants.HEARTBEAT_METHOD,
                    new Class<?>[]{Boolean.class}, Collections.singletonList(new Container(true)));
            RpcResponse rpcResponse = Send(rpcRequest, socket);
            if (ConnectionRefusedException.class != rpcResponse.getReturnType()) {
                tempAliveHost.add(socket);
            }
        }
        if (!(tempAliveHost.containsAll(this.config.getAliveHost()) &&
                (tempAliveHost.size() == this.config.getAliveHost().size()))) {
            /*如不一致*/
            CopyOnWriteArrayList<InetSocketAddress> cloneAliveHost =
                    (CopyOnWriteArrayList<InetSocketAddress>) this.config.getAliveHost().clone();
            cloneAliveHost.removeAll(tempAliveHost);
            tempAliveHost.removeAll(this.config.getAliveHost());

            this.config.getAliveHost().removeAll(cloneAliveHost);
            this.config.getAliveHost().addAll(tempAliveHost);
        }
    }

}
