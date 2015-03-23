package com.xw.http;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;

/**
 * Author: junjie
 * Date: 3/13/15.
 */
public abstract class PipelineBuilder {
    protected PipelineBuilder() {

    }

    public final ChannelPipeline build(final ChannelPipeline pipeline) {
        // setup ur customized http response/contents processing
        pipeline.addLast(new HttpClientCodec());

        // auto decompression
        pipeline.addLast(new HttpContentDecompressor());

        return (setup(pipeline));
    }

    public abstract ChannelPipeline setup(final ChannelPipeline pipeline);
}
