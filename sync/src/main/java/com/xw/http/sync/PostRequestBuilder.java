package com.xw.http.sync;

import org.apache.http.Consts;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

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

        return post;
    }
}

