package com.shudun.client.builder.protocol;

import com.shudun.base.constants.ConfigConstants;
import com.shudun.base.dto.Container;
import com.shudun.base.dto.RpcRequest;
import com.shudun.base.dto.RpcResponse;
import com.shudun.base.enums.ErrorEnum;
import com.shudun.base.exception.ConnectionRefusedException;
import com.shudun.base.exception.NoHostsException;
import com.shudun.client.config.Config;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Client {

    protected final AtomicLong INCR = new AtomicLong(0);

    protected static final long MAX =8000000000000000000L;

    protected final Config config;

    protected Client(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    public abstract RpcResponse Send(RpcRequest rpcRequest, InetSocketAddress isa);

    public InetSocketAddress next() {
        if (this.config.getAliveHost().isEmpty()) {
            throw new NoHostsException(ErrorEnum.ALIVE_HOST_IS_NULL.getMessage());
        } else if (this.config.getAliveHost().size() == 1) {
            return this.config.getAliveHost().get(0);
        } else {
            long andIncrement = INCR.getAndIncrement();
            if (andIncrement > MAX) INCR.set(0);
            return this.config.getAliveHost().get((int) (andIncrement % this.config.getAliveHost().size()));
        }
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
