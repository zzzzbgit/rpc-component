package com.shudun.client.builder.protocol.socket;

import com.shudun.base.dto.RpcRequest;
import com.shudun.base.dto.RpcResponse;
import com.shudun.base.exception.ConnectionRefusedException;
import com.shudun.base.util.SecretKeyUtil;
import com.shudun.base.util.SerializingUtil;
import com.shudun.base.util.Tools;
import com.shudun.client.builder.protocol.Client;
import com.shudun.client.pool.FixedPool;
import com.shudun.client.pool.element.impl.SocketClient;
import com.shudun.client.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class SocketCli extends Client {

    private final SocketInitializer initializer;

    public SocketCli(Config config) {
        super(config);
        this.initializer = new SocketInitializer(config);
        this.initializer.doInitProtocol();
        resetAliveHost();
    }

    @Override
    public RpcResponse Send(RpcRequest rpcRequest, InetSocketAddress isa) {
        /*获取唯一标识*/
        long requestId = Tools.GenSerialNumber();
        rpcRequest.setRequestId(requestId)
                .setClientId(config.getClientId())
                .setAuthKey(config.getAuthKey());
        if (config.isAuthEnable()) {
            rpcRequest.setSignValue(SecretKeyUtil.sign(config.getSecretKey(), rpcRequest));
        }
        FixedPool<InetSocketAddress, SocketClient> pool = null;
        SocketClient socket = null;
        try {
            pool = initializer.AcquirePool(isa);
        } catch (Exception e) {
            return new RpcResponse(requestId, ConnectionRefusedException.class, new ConnectionRefusedException("获取连接失败:"+isa.getHostString()));
        }
        try {
            socket = pool.acquire();
            if (null == socket) {
                return new RpcResponse(requestId, ConnectionRefusedException.class, new ConnectionRefusedException("获取连接失败:"+isa.getHostString()));
            }
            byte[] sendData = SerializingUtil.serializeToByte(rpcRequest);
            /*发送消息*/
            byte[] outData = socket.execute(sendData);
            if (null == outData) {
                return new RpcResponse(requestId, ConnectionRefusedException.class, new ConnectionRefusedException("获取连接失败:"+isa.getHostString()));
            }
            /*反序列化 */
            return SerializingUtil.deserializeFromByte(outData, RpcResponse.class);
        } catch (Exception e) {
            log.error("数据发送、数据返回异常!"+ isa, e);
            initializer.removeAddr(isa);
            return new RpcResponse(requestId, ConnectionRefusedException.class, new ConnectionRefusedException("连接失败:"+isa.getHostString()));
        }finally {
            pool.release(socket);
        }
    }
}
