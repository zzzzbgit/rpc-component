package com.shudun.client.coder;

import com.shudun.base.dto.RpcRequest;
import com.shudun.base.util.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Rpc消息编码器
 */
public class MessageEncoder extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest request, ByteBuf out) throws Exception {
        if(request == null){
            throw new Exception("msg is null");
        }
        byte[] data = SerializingUtil.serializeToByte(request);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
