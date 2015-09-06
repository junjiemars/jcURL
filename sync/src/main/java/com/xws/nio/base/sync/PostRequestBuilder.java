package com.xws.nio.base.sync;

import org.apache.http.client.methods.HttpPost;

/**
 * Author: junjie
 * Date: 4/2/15.
 * Target: <>
 */
public abstract class PostRequestBuilder
        implements RequestBuilder<HttpPost> {

    public PostRequestBuilder(final String uri, final String content) {
        this.uri = uri;
        this.content = content;
    }


    protected final String uri;
    protected final String content;

    @Override
    public HttpPost build() {
        final HttpPost post = new HttpPost(uri);
        setup(post);
        return post;
    }
}

