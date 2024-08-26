package com.shudun.base.dto;

import lombok.Data;

/**
 * 因反序列化框架数组元素为null会丢弃null元素,使用容器存放参数
 */
@Data
public class Container {

    private Object content;

    public Container(Object content) {
        this.content = content;
    }
}
