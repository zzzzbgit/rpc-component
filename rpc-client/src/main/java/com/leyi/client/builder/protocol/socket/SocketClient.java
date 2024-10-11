package com.leyi.client.builder.protocol.socket;

import com.leyi.base.dto.RpcRequest;
import com.leyi.base.dto.RpcResponse;
import com.leyi.base.exception.ConnectionRefusedException;
import com.leyi.base.util.SecretKeyUtil;
import com.leyi.base.util.SerializingUtil;
import com.leyi.base.util.Tools;
import com.leyi.client.builder.pool.GeneralFixedPool;
import com.leyi.client.builder.protocol.Client;
import com.leyi.client.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class SocketClient extends Client {

    private final SocketInitializer initializer;

    public SocketClient(Config config) {
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
        GeneralFixedPool<InetSocketAddress, SocketElement> pool = null;
        SocketElement socket = null;
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
