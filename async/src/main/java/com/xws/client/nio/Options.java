package com.xws.client.nio;


import com.google.gson.reflect.TypeToken;
import com.sun.javafx.UnmodifiableArrayList;
import com.xws.nio.base.H;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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

    public static final String H_POST = "POST";

    private boolean _has_head;
    private boolean _is_verbose;
    private final List<Integer> _unknown_opts = new ArrayList<Integer>();
    private final List<String> _urls = new ArrayList<String>();
    private String _data;
    private String _request;


    public Options() {
    }

    public final boolean has_head() {
        return _has_head;
    }

    public final boolean is_verbose() {
        return _is_verbose;
    }

    public final boolean is_verbose(boolean verbose) {
        return _is_verbose = verbose;
    }

    public final List<Integer> unknown_opts() {
        return _unknown_opts;
    }
    public final int unknown_opts(int i) {
        _unknown_opts.add(i);
        return i;
    }

    public final boolean has_head(final boolean head) {
        return _has_head = head;
    }
    public final List<String> urls() {
        return _urls;
    }

    public final String url() {
        return _urls.remove(0);
    }

    public final String url(final String url) {
        _urls.add(url);
        return url;
    }

    public final String data() {
        return _data;
    }

    public final String data(final String data) {
        if (H.is_null_or_empty(data)) {
            return (_data = data);
        }

        final Matcher m = _data_pattern.matcher(data);
        if (m.find()) {
            final String f = H.read_file(m.group(1));
            if (!H.is_null_or_empty(f)) {
                return _data = f;
            }
        }
        return data;
    }

    public final String request() {
        return _request;
    }

    public final String request(final String command) {
        return _request = command;
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

    private static final Logger _l = LoggerFactory.getLogger(Options.class);
    private static final Pattern _data_pattern = Pattern.compile("@([\\.\\w\\S]+)");
}
