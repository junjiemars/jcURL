package com.xw.http;


import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by junjie on 12/16/14.
 */
public final class Options {
    private static final Pattern _SPLIT_NODES = Pattern.compile("\\s*:\\s*");
    private static final Type _type = new TypeToken<Options>() {}.getType();

    private String _url;

    public Options() {

    }

    public final String url() { return (_url); }

    public final String url(final String url) {
        return (_url = url);
    }

    public static final Options read(final String conf) {
        final String j = H.read_file(conf);
        if (H.is_null_or_empty(j)) {
            return (null);
        }

        final Options c = H.from_json(j, _type);
        return (c);
    }

    public static final void save(final Options options, final String conf) {
        final String j = H.to_json(options, _type);
        H.write_file(j, conf);
    }


    public final String logger_config() {
        return (A.LOG4J_CONFIG_PROPERTY);
    }

    @Override
    public String toString() {
        final String s = H.to_json(this, _type);
        save(this, A.OPTIONS_CONFIG_FILE);
        return (s);
    }

    private static final Logger _l = LogManager.getLogger(Options.class);
}
