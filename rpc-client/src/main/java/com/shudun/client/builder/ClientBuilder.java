package com.shudun.client.builder;

import com.shudun.base.constants.ConfigConstants;
import com.shudun.base.dto.Container;
import com.shudun.base.dto.RpcRequest;
import com.shudun.base.dto.RpcResponse;
import com.shudun.base.exception.ConnectionRefusedException;
import com.shudun.base.exception.InitException;
import com.shudun.client.builder.protocol.Client;
import com.shudun.client.config.Config;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ClientBuilder {

    /*netty*/
    public static final String NETTY = "com.shudun.client.builder.protocol.netty.NettyBuilder";
    public static final String SOCKET = "com.shudun.client.builder.protocol.socket.SocketBuilder";

    public static ClientBuilder getInstance(String clz, Config config) {
        ClientBuilder scb = null;
        try {
            scb = (ClientBuilder) Class.forName(clz).getConstructor(Config.class).newInstance(config);
        } catch (Exception e) {
            throw new InitException("getInstance input class notFound!", e);
        }
        return scb;
    }

    protected final Config config;

    private final Client client;

    protected ClientBuilder(Config config) {
        this.config = config;
        this.client = doInitialize();
    }

    /**
     * 初始化
     *
     * @return
     */
    protected abstract Client doInitialize();

    public <T> T build(Class<T> interfaceClass, EnhancedProcessor processor) {
        /*登录*/
        RpcRequest loginDto = new RpcRequest(ConfigConstants.LOGIN_INTERFACE, ConfigConstants.LOGIN_METHOD,
                new Class<?>[]{Boolean.class, String.class, String.class}, Arrays.asList(new Container(config.isAuthEnable()),
                new Container(config.getClientId()), new Container(config.getAuthKey())));
        InetSocketAddress isa = client.next();
        RpcResponse loginRr = client.Send(loginDto, isa);
        Object loginResData = loginRr.getReturnData();
        /*如res为异常类*/
        if (loginResData instanceof Exception) {
            throw new InitException("init error");
        }
        /*生成代理对象*/
        ClassLoader loader = interfaceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{interfaceClass};
        Object interfaceProxy =  Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            if (null != processor) {
                Object beginRes = processor.beginProcess();
                if (beginRes != null) {
                    return beginRes;
                }
            }
            Object res = null;
            /*获取ip:host*/
            InetSocketAddress next = client.next();
            List<Container> containers = Arrays.stream(args).map(Container::new).collect(Collectors.toList());
            RpcRequest rpcRequest = new RpcRequest(interfaceClass.getName(), method.getName(), method.getParameterTypes(), containers);
            /*获取Netty返回结果*/
            RpcResponse rpcResponse;
            if (null != processor) {
                processor.beforeSendProcess();
            }
            int retry = config.getDefaultRetries();
            do {
                rpcResponse = client.Send(rpcRequest, next);
                if (rpcResponse.getReturnData() instanceof ConnectionRefusedException) {
                    next = client.next();
                } else {
                    break;
                }
            } while (retry-- > 0);

            res = rpcResponse.getReturnData();
            if (null != processor) {
                processor.afterSendProcess();
            }
            /*如res为异常类*/
            if (res instanceof Exception) {
                throw (Exception) res;
            }
            return res;
        });
        return (T) interfaceProxy;
    }
}
