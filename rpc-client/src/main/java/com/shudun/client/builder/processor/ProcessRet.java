package com.shudun.client.builder.processor;

import lombok.Data;

/**
 * 处理结果
 */
@Data
public class ProcessRet {

    /**
     * 是否中断
     */
    private boolean isInterrupt = false;

    /**
     * 结果
     */
    private Object result;
}
