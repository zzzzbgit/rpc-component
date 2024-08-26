package com.shudun.base.constants;

/**
 * 配置常量类
 */
public class ConfigConstants {
    /*
    服务端socket ip：端口 例：127.0.0.1:2334,127.0.0.2:2335
     */
    public final static String CLENT_SERVERADDRESS = "client.server-address";
    /*
    netty socket连接池最大连接数
     */
    public final static String CLIENT_MAXCONNECTIONS = "client.max-connections";
    /*
    动态调整服务端ip时间间隔
     */
    public final static String CLIENT_DYNAMICINTERVAL = "client.dynamic-interval";
    /*
    netty消息连接超时时间
     */
    public final static String CLIENT_CONNECTTIMEOUT = "client.connect-timeout";
    /*
    如服务端连接失败最大重试次数
     */
    public final static String CLIENT_MAXRETRIES = "client.max-retries";
    /*
    netty消息包最大大小 （MB）
     */
    public final static String CLIENT_MESSAGEMAXSIZE = "client.message-maxSize";
    /*
    客户端id
     */
    public final static String CLIENT_ID = "client.id";
    /*
    认证开关
     */
    public final static String CLIENT_AUTHENABLE = "client.auth-enable";
    /*
    认证密钥
     */
    public final static String CLIENT_AUTHKEY = "client.auth-key";
    /*
    密钥
     */
    public final static String CLIENT_SECRETKEY = "client.secret-key";

    //====================接口常量=========================>
    /*
    登陆接口类
     */
    public final static String LOGIN_INTERFACE = "com.shudun.server.HeartbeatHandler";
    /*
    登陆方法
     */
    public final static String LOGIN_METHOD = "login";
    /*
    心跳接口
     */
    public final static String HEARTBEAT_METHOD = "heartbeat";

    /*
    消息解码偏移信息
    */
    public final static int MAX_FRAME_LENGTH = 1024 * 1024 * 1024;
    public final static int LENGTH_FIELD_OFFSET = 0;  //长度偏移
    public final static int LENGTH_FIELD_LENGTH = 4;  //长度字段所占的字节数
    public final static int LENGTH_ADJUSTMENT = 0;
    public final static int INITIAL_BYTES_TO_STRIP = 0;
}
