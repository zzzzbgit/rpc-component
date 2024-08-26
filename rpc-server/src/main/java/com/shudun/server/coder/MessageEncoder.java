package com.shudun.server.coder;

import com.shudun.base.dto.RpcRequest;
import com.shudun.base.dto.RpcResponse;
import com.shudun.base.util.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Rpc消息编码器
 */
public class MessageEncoder extends MessageToByteEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse response, ByteBuf out) throws Exception {
        if(response == null){
            throw new Exception("msg is null");
        }
        byte[] data = SerializingUtil.serializeToByte(response);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
