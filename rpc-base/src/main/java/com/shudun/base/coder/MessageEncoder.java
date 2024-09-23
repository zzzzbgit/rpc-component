package com.shudun.base.coder;

import com.shudun.base.util.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Rpc消息编码器
 */
public class MessageEncoder<T> extends MessageToByteEncoder<T> {


    public MessageEncoder(Class<T> clz) {
        super(clz);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, T t, ByteBuf out) throws Exception {
        if(t == null){
            throw new Exception("msg is null");
        }
        byte[] data = SerializingUtil.serializeToByte(t);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
