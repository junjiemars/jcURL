package com.xw.http;


import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Type;

/**
 * Author: junjie
 * Date: 12/16/14.
 * Target: <>
 */
public final class Options {
    //    private static final Pattern _SPLIT_NODES = Pattern.compile("\\s*:\\s*");
    private static final Type _type = new TypeToken<Options>() {}.getType();

    private String _url;
    private HttpMethod _method;
    private String _content;

    public Options(final String url, final HttpMethod method, final String content) {
        _url = url;
        _method = method;
        _content = content;
    }

    public final String url() {
        return (_url);
    }

    public final HttpMethod method() {
        return (_method);
    }

    public final String content() {
        return (_content);
    }

    public static Options read(final String conf) {
        final String j = H.read_file(conf);
        if (H.is_null_or_empty(j)) {
            return (null);
        }

        return (H.from_json(j, _type));
    }

    public static void save(final Options options, final String conf) {
        final String j = H.to_json(options, _type);
        H.write_file(j, conf);
    }

    @Override
    public String toString() {
        return (H.to_json(this, _type));
    }

//    private static final Logger _l = LogManager.getLogger(Options.class);
}
