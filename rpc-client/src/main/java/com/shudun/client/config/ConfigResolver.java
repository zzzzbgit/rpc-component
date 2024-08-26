package com.shudun.client.config;

import com.shudun.base.constants.ConfigConstants;
import com.shudun.base.exception.ConfigurationException;
import com.shudun.base.util.Tools;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * 配置文件解析器
 */
public class ConfigResolver {

    public static Config ResolverProperties(Properties props) throws ConfigurationException {
        /*
        获取配置文件相关信息
         */
        Config config = new Config();

        /*client.socket-servicesAddr 配置 连接服务端ip:port*/
        String serverAddress = props.getProperty(ConfigConstants.CLENT_SERVERADDRESS);
        if (Tools.isEmpty(serverAddress))
            throw new ConfigurationException("serverAddress is null!");
        List<InetSocketAddress> socketInfos = ServicesAddrResolver(serverAddress);
        if (socketInfos.isEmpty())
            throw new ConfigurationException("serverAddress is null!");

        config.getSocketInfos().addAll(socketInfos);
        /*client.max-connections 配置 最大连接数*/
        String maxConnections = props.getProperty(ConfigConstants.CLIENT_MAXCONNECTIONS);
        if (Tools.isEmpty(maxConnections))
            throw new ConfigurationException("maxConnections is null!");
        config.setMaxConnections(Integer.parseInt(maxConnections));

        /*client.max-retries 配置 最大重试次数*/
        String retries = props.getProperty(ConfigConstants.CLIENT_MAXRETRIES);
        if (Tools.NotNull(retries))
            config.setDefaultRetries(Integer.parseInt(retries));

        /*client.connect-timeout 配置 连接超时时间*/
        String timeout = props.getProperty(ConfigConstants.CLIENT_CONNECTTIMEOUT);
        if (Tools.NotNull(timeout))
            config.setConnectTimeout(Integer.parseInt(timeout));

        /*动态加载时间间隔*/
        String dynamicInterval = props.getProperty(ConfigConstants.CLIENT_DYNAMICINTERVAL);
        if (Tools.NotNull(dynamicInterval))
            config.setDynamicInterval(Integer.parseInt(dynamicInterval));

        /*客户端 id*/
        String clientId = props.getProperty(ConfigConstants.CLIENT_ID);
        if (Tools.isEmpty(clientId)) {
            config.setClientId(UUID.randomUUID().toString());
        }

        /*auth*/
        String authEnable = props.getProperty(ConfigConstants.CLIENT_AUTHENABLE);
        if (Tools.NotNull(authEnable)) {
            config.setAuthEnable(Boolean.parseBoolean(authEnable));
            String authKey = props.getProperty(ConfigConstants.CLIENT_AUTHKEY);
            String secretKey = props.getProperty(ConfigConstants.CLIENT_SECRETKEY);
            if (Tools.anyNull(authKey, secretKey)) {
                throw new ConfigurationException("authKey or secretKey is null!");
            }
            config.setAuthKey(authKey);
            config.setSecretKey(Base64.getDecoder().decode(secretKey));
        }

        return config;
    }

    public static List<InetSocketAddress> ServicesAddrResolver(String servicesAddressStr) {
        List<InetSocketAddress> serviceList = new ArrayList<>();
        /*如不为空按新配置解析*/
        String[] servicesArray = servicesAddressStr.split(",");
        for (String serviceAddr : servicesArray) {
            String[] IP_HOST = serviceAddr.split(":");
            serviceList.add(new InetSocketAddress(IP_HOST[0], Integer.parseInt(IP_HOST[1])));
        }
        return serviceList;
    }
}
