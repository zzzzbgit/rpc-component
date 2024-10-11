package com.leyi.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum ErrorEnum {

    SUCCESS("0", "操作成功"),
    FAILED("-1", "操作失败"),
    APPCODE_IS_NULL("133401", "app-code不能为空"),
    APPCODE_AK_SK_IS_NULL("133402", "app-ak或app-sk不能为空"),
    MAX_CONNECTIONS_IS_NULL("133403", "socket-maxConnections不能为空"),
    SERVICES_ADDR_IS_NULL("133404", "socket-servicesAddr不能为空"),
    ALIVE_HOST_IS_NULL("133405", "无可用的服务"),
    CONNECTION_REFUSED("133406", "ConnectionRefused"),
    LOGIN_ERROR("133407", "登陆失败"),
    FILE_NOT_EXIST("133408", "文件不存在"),
    FILE_UPLOAD_ERROR("133409", "文件上传失败"),
    FILE_DOWNLOAD_ERROR("133410", "文件下载失败"),
    FILE_ENCRYPT_ERROR("133411", "文件加密失败"),
    FILE_DECRYPT_ERROR("133412", "文件解密失败"),
    CONNECTION_TIMEOUT("133413", "连接超时:"),
    SSL_CERT_IS_NULL("133414", "SSL证书不能为空"),
    REQUEST_ID_IS_NULL("133415", "流水号不能为空"),
    SSL_TYPE_ERROR("133416", "SSL类型错误"),
    DIGEST_INIT_NOTFOUND_ERROR("133417", "未查询到摘要初始化"),
    FILE_WRITE_ERROR("133418", "文件写入失败"),
    PARA_IS_ERROR("133205", "参数错误"),

    PARA_IS_NONE("133101", "必填请求参数不能为空"),
    HSM_ALGNOTSUPPORT("16777225", "不支持的算法调用"),
    HSM_INARGERR("16777245", "输入参数错误"),
    DIGEST_ERROR("133218", "摘要计算失败"),
    ;



    //响应状态码
    private String code;
    //响应信息
    private String message;
}
