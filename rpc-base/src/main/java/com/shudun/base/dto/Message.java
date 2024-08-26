package com.shudun.base.dto;

import lombok.Data;

@Data
public class Message {

    private int length;

    private RpcRequest rpcRequest;

    private RpcResponse rpcResponse;

    public Message() {
    }

    public Message(int length, RpcRequest rpcRequest) {
        this.length = length;
        this.rpcRequest = rpcRequest;
    }

    public Message(int length, RpcResponse rpcResponse) {
        this.length = length;
        this.rpcResponse = rpcResponse;
    }
}
