package com.xw.http;


import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String _data;
    private int _header; // 0:all; 1:header-only 2:content-only
    private int _timeout;
    private int _concurrent;
    private int _cpu;

    public Options(final String url, final HttpMethod method, final String data, final int header,
                   final int timeout, final int concurrent, final int cpu) {
        _url = url;
        _method = method;
        _header = header;
        _data = _rebuild_data(data);
        _timeout = timeout;
        _concurrent = concurrent;
        _cpu = cpu;
    }

    public final String url() {
        return (_url);
    }

    public final HttpMethod method() {
        return (_method);
    }

    public final String data() {
        return (_data);
    }

    public final boolean header() {
        return (_header == 0 || 1 == (_header & 1));
    }

    public final boolean body() {
        return (_header == 0 || 2 == (_header & 2));
    }

    public final int timeout() {
        return (_timeout);
    }

    public final int concurrent() {
        return (_concurrent);
    }

    public final int cpu() { return (_cpu); }

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

    private static String _rebuild_data(final String data) {
        if (H.is_null_or_empty(data)) {
            return (data);
        }

        final Matcher m = _data_pattern.matcher(data);
        if (m.find()) {
            _l.info(m.group(1));
            final String f = H.read_file(m.group(1));
            if (!H.is_null_or_empty(f)) {
                return (f);
            }
        }
        return (data);
    }

    private static final Logger _l = LoggerFactory.getLogger(Options.class);
    private static final Pattern _data_pattern = Pattern.compile("@([\\.\\w\\S]+)");
}
