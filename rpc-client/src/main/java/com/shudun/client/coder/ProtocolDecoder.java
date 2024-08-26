package com.shudun.client.coder;

import com.shudun.base.dto.Message;
import com.shudun.base.dto.RpcResponse;
import com.shudun.base.util.SerializingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Rpc消息解码器
 */
public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {
    /**
     * @param maxFrameLength  帧的最大长度
     * @param lengthFieldOffset length字段偏移的地址
     * @param lengthFieldLength length字段所占的字节长
     * @param lengthAdjustment 修改帧数据长度字段中定义的值，可以为负数 因为有时候我们习惯把头部记入长度,若为负数,则说明要推后多少个字段
     * @param initialBytesToStrip 解析时候跳过多少个长度
     * @param failFast 为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常
     */
    public ProtocolDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        in = (ByteBuf) super.decode(ctx,in);

        if(in==null){
            return null;
        }
        /*读取length字段*/
        int length = in.readInt();

        if(in.readableBytes()!=length){
            throw new Exception("标记的长度不符合实际长度");
        }
        /*读取body*/
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        in.release();
        /*消息反序列化*/
        RpcResponse rpcResponse = SerializingUtil.deserializeFromByte(bytes, RpcResponse.class);
        return new Message(length,rpcResponse);
    }
}
