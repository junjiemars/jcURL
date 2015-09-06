package com.xws.bio.client;

import org.apache.http.client.methods.HttpGet;

/**
 * Author: junjie
 * Date: 4/2/15.
 * Target: <>
 */
public abstract class GetRequestBuilder
        implements RequestBuilder<HttpGet> {

    public GetRequestBuilder(final String url) {
        this.url = url;
    }

    @Override
    public HttpGet build() {
        final HttpGet get = new HttpGet(url);
        return get;
    }

    protected String url;
}