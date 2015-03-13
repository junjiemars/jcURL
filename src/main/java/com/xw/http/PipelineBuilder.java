package com.xw.http;

import io.netty.channel.ChannelPipeline;

/**
 * Author: junjie
 * Date: 3/13/15.
 */
public abstract class PipelineBuilder {
    public abstract ChannelPipeline setup(final ChannelPipeline pipeline);
}
