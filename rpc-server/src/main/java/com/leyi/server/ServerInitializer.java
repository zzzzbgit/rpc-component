package com.leyi.server;

import com.leyi.base.constants.ConfigConstants;
import com.leyi.base.coder.MessageEncoder;
import com.leyi.base.coder.ProtocolDecoder;
import com.leyi.base.dto.RpcRequest;
import com.leyi.base.dto.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private Object[] servers;

    public ServerInitializer(Object... servers) {
        this.servers = servers;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        //解码器
        pipeline.addLast(new ProtocolDecoder<>(RpcRequest.class, ConfigConstants.MAX_FRAME_LENGTH,
                ConfigConstants.LENGTH_FIELD_OFFSET,
                ConfigConstants.LENGTH_FIELD_LENGTH,
                ConfigConstants.LENGTH_ADJUSTMENT,
                ConfigConstants.INITIAL_BYTES_TO_STRIP,
                false));
        //编码器
        pipeline.addLast(new MessageEncoder<>(RpcResponse.class));
        pipeline.addLast(new IdleStateHandler(7, 7, 10, TimeUnit.SECONDS));
        pipeline.addLast(new ServerHandler(servers));
    }
}
