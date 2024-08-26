package com.shudun.client.builder;

public interface EnhancedProcessor {

    Object beginProcess();

    void beforeSendProcess();

    void afterSendProcess();

}
