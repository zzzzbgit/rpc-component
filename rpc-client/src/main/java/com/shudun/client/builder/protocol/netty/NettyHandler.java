package com.shudun.client.builder.protocol.netty;

import com.shudun.base.dto.Message;
import com.shudun.base.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *Rpc客户端处理器
 */
public class NettyHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        /*从消息中获取rpcResponse*/
        RpcResponse rpcResponse = msg.getRpcResponse();
        /*将返回结果放入缓存*/
        NettyReceiveTools.setReceiveMsg(rpcResponse.getRequestId(), rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常消息=" + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

}