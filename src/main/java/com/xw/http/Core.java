package com.xw.http;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.html.Option;

/**
 * Author: junjie
 * Date: 3/12/15.
 */
public final class Core {
    private Core() {
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            _help();
            return;
        }

        final LongOpt[] opts = new LongOpt[]{
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("url", LongOpt.OPTIONAL_ARGUMENT, null, 'u')
        };

        final Getopt g = new Getopt(A.NAME, args, "hu:;", opts);
        g.setOpterr(true);
        int c;
        final Options options = new Options();

        while ((c = g.getopt()) != -1)
            switch (c) {
                case 'u':
                    options.url(g.getOptarg());
                    break;
                case 'h':
                    _help();
                    break;
                case ':':
                    _help(String.format("u need specify an argument for option:%s",
                            g.getOptopt()));
                    break;
                case '?':
                    _help(String.format("the option:%s is invalid",
                            g.getOptopt()));
                default:
                    _help(String.format("cli-parser return:%s", c));
                    break;
            }

        _request(options);
    }

    private static final void _request(final Options options) {
        _l.info("OPTIONS:\n=========================");
        _l.info(options);
        _l.info("RESPONSE:\n=========================");
        final HttpRequestBuilder builder = new HttpRequestBuilder() {
            @Override
            public HttpRequest setup(HttpRequest request) {
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

                request.headers().set(
                        HttpHeaderNames.COOKIE,
                        ClientCookieEncoder.encode(
                                new DefaultCookie("my-cookie", "foo"),
                                new DefaultCookie("another-cookie", "bar")));
                return (request);
            }
        };
        NClient.get(options.url(), builder);
    }

    private static final void _help() {
        _help(null);
    }

    private static final void _help(final String m) {
        if (null != m) {
            _l.info(m);
        }
        _l.info(String.format("usage: %s [-h|--help] [-u|--url]", A.NAME));
        _l.info("\t--url: specify the http url");
        System.exit(0);
    }

    private static final Logger _l = LogManager.getLogger(Core.class);
}
