package com.xw.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;

/**
 * Author: junjie
 * Date: 3/9/15.
 */
public final class NClientInitializer extends ChannelInitializer<SocketChannel> {
    public NClientInitializer(final PipelineBuilder builder) {
        _builder = builder;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        if (_builder != null) {
            _builder.setup(ch.pipeline());
        }
    }

    private final PipelineBuilder _builder;
}
