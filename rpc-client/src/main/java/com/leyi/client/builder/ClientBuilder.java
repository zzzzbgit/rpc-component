package com.leyi.client.builder;

import com.leyi.base.constants.ConfigConstants;
import com.leyi.base.dto.Container;
import com.leyi.base.dto.RpcRequest;
import com.leyi.base.dto.RpcResponse;
import com.leyi.base.exception.ConnectionRefusedException;
import com.leyi.base.exception.InitException;
import com.leyi.base.util.Tools;
import com.leyi.client.builder.processor.EnhancedProcessor;
import com.leyi.client.builder.processor.ProcessRet;
import com.leyi.client.builder.protocol.Client;
import com.leyi.client.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户端构建器
 */
@Slf4j
public abstract class ClientBuilder {

    /*netty*/
    public static final String NETTY = "com.shudun.client.builder.protocol.netty.NettyBuilder";
    /*socket*/
    public static final String SOCKET = "com.shudun.client.builder.protocol.socket.SocketBuilder";

    /**
     * 获取实例
     *
     * @param protocolClz 协议类
     * @param config      配器配置
     * @return
     */
    public static ClientBuilder getInstance(String protocolClz, Config config) {
        ClientBuilder cb = null;
        try {
            cb = (ClientBuilder) Class.forName(protocolClz).getConstructor(Config.class).newInstance(config);
        } catch (Exception e) {
            log.error("getInstance input class notFound!", e);
            throw new InitException("getInstance input class notFound!", e);
        }
        return cb;
    }

    /**
     * 配置信息
     */
    protected final Config config;

    /**
     * 客户端
     */
    private final Client client;

    /**
     * 是否增强
     */
    private boolean isEnhanced = false;

    /**
     * 构造器
     *
     * @param config
     */
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

    /**
     * 构建代理对象
     *
     * @param interfaceClass 接口类
     * @param processor      增强处理器
     * @param <T>
     * @return
     */
    public <T> T build(Class<T> interfaceClass, EnhancedProcessor processor) {
        if (!Tools.isEmpty(processor)) {
            this.isEnhanced = true;
        }
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
        Object interfaceProxy = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            if (this.isEnhanced) {
                ProcessRet ret = processor.executeBeginProcess();
                if (ret.isInterrupt()) {
                    return ret.getResult();
                }
            }
            Object res = null;
            /*获取ip:host*/
            InetSocketAddress next = client.next();
            List<Container> containers = Arrays.stream(args).map(Container::new).collect(Collectors.toList());
            RpcRequest rpcRequest = new RpcRequest(interfaceClass.getName(), method.getName(), method.getParameterTypes(), containers);
            /*获取Netty返回结果*/
            RpcResponse rpcResponse;
            if (this.isEnhanced) {
                ProcessRet ret = processor.executeBeforeSendProcess();
                if (ret.isInterrupt()) {
                    return ret.getResult();
                }
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
            if (this.isEnhanced) {
                ProcessRet ret = processor.executeAfterSendProcess();
                if (ret.isInterrupt()) {
                    return ret.getResult();
                }
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
