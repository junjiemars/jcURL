package com.xws.nio.client;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.nio.channels.Pipe;
import java.util.concurrent.TimeUnit;

/**
 * Author: junjie
 * Date: 3/13/15.
 */
public abstract class PipelineBuilder {
    protected PipelineBuilder() {
        this(0);
    }

    protected PipelineBuilder(final int timeout) {
        _timeout = new Integer(timeout);
    }

    public final void build(final ChannelPipeline pipeline) {
        if (_timeout > 0 ) {
            pipeline.addLast(new ReadTimeoutHandler(_timeout, TimeUnit.MILLISECONDS));
        }

        pipeline.addLast(new HttpClientCodec());

        pipeline.addLast(new HttpContentDecompressor());

        setup(pipeline);
    }

    protected abstract void setup(final ChannelPipeline pipeline);

    private final int _timeout;
}

