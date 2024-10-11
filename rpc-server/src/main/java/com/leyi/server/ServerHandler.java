package com.leyi.server;

import com.leyi.base.dto.Container;
import com.leyi.base.dto.RpcRequest;
import com.leyi.base.dto.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

@ChannelHandler.Sharable
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    public ServerHandler(Object... servers) {
        INTERFACE_MAP.put(HeartbeatHandler.class.getName(), new HeartbeatHandler());
        for (Object server : servers) {
            INTERFACE_MAP.put(server.getClass().getInterfaces()[0].getName(), server);
        }
    }

    private Map<String, Object> INTERFACE_MAP = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
        /*定义res*/
        Object res = null;
        RpcResponse rpcResponse;
        /*从消息中获取rpcRequest对象*/
        try {
            /*从IOC容器中获取接口实现类对象*/
            Object service = INTERFACE_MAP.get(rpcRequest.getInterfaceName());

            /*根据参数获取方法对象*/
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            /*执行方法*/
            List<Container> containers = rpcRequest.getParameters();
            Object[] args = containers.stream().map(Container::getContent).toArray();
            try {
                res = method.invoke(service, args);
            } catch (Exception e) {
                throw e;
            }

            /*构建rpcResponse*/
            rpcResponse = new RpcResponse(rpcRequest.getRequestId(), res.getClass(), res);
        } catch (Exception e) {
            res = e.getCause();
            rpcResponse = new RpcResponse(rpcRequest.getRequestId(), res.getClass(), res);
        }
        /*将结果写回*/
        ctx.writeAndFlush(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause.getMessage().equals("Connection reset by peer") || cause.getMessage().equals("连接被对方重设"))) {
            log.info(cause.getMessage(), cause);
        }
        ctx.close();
    }
}
