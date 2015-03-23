package com.xw.http;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: junjie
 * Date: 3/12/15.
 */
public final class Core {
    private static final Logger _l = LogManager.getLogger(Core.class);

    private Core() {
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            _help();
            return;
        }

        final LongOpt[] opts = new LongOpt[]{
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("conf", LongOpt.OPTIONAL_ARGUMENT, null, 'c'),
                new LongOpt("save", LongOpt.OPTIONAL_ARGUMENT, null, 's'),
                new LongOpt("url", LongOpt.OPTIONAL_ARGUMENT, null, 'u'),
                new LongOpt("get", LongOpt.NO_ARGUMENT, null, 'g'),
                new LongOpt("post", LongOpt.NO_ARGUMENT, null, 'p'),
                new LongOpt("data", LongOpt.OPTIONAL_ARGUMENT, null, 'd'),
                new LongOpt("header", LongOpt.OPTIONAL_ARGUMENT, null, 'H')
        };

        final Getopt g = new Getopt(A.NAME, args, "hc:s:u:gpd:H:;", opts);
        g.setOpterr(true);
        int c;

        String conf = null;
        String save = null;
        String url = null;
        HttpMethod method = null;
        String data = null;
        int header = 0;

        while ((c = g.getopt()) != -1)
            switch (c) {
                case 'c':
                    conf = g.getOptarg();
                    break;
                case 's':
                    save = g.getOptarg();
                    break;
                case 'u':
                    url = g.getOptarg();
                    break;
                case 'g':
                    method = HttpMethod.GET;
                    break;
                case 'p':
                    method = HttpMethod.POST;
                    break;
                case 'd':
                    data = g.getOptarg();
                    break;
                case 'H':
                    header = H.str_to_int(g.getOptarg(), 0);
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

        final Options options = (H.is_null_or_empty(conf) ?
                new Options(url, method, data, header) : Options.read(conf)
        );
        if (null == options) {
            _l.error(String.format("<var:options> can't create Options%s",
                    H.is_null_or_empty(conf) ? null : String.format(" from %s", conf)));
            return;
        }
        if (!H.is_null_or_empty(save)) {
            Options.save(options, save);
        }

        if (HttpMethod.GET.equals(options.method())) {
            _http_get(options);
        } else if (HttpMethod.POST.equals(options.method())) {
            _http_post(options);
        }
    }

    private static void _http_get(final Options options) {
        _info(options);

        final RequestBuilder requested = new RequestBuilder(options.url()) {
            @Override
            public FullHttpRequest setup(FullHttpRequest request) {
                // setup ur customized http headers/contents processing
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

                request.headers().set(
                        HttpHeaderNames.COOKIE,
                        ClientCookieEncoder.encode(
                                new DefaultCookie("my-cookie", "foo"),
                                new DefaultCookie("another-cookie", "bar")));
                return (request);
            }
        };
        final PipelineBuilder pipelined = new PipelineBuilder() {
            @Override
            public ChannelPipeline setup(ChannelPipeline pipeline) {
                // default http header processing
                if (options.header()) {
                    pipeline.addLast(new DefaultHeaderHandler());
                }

                // default http content processing
                if (options.body()) {
                    pipeline.addLast(new DefaultContentHandler<Integer>() {
                        @Override
                        protected Integer process(String s) {
                            _l.info(String.format("<BEGIN OF CONTENT:%s>", s.length()));
                            _l.info(s);
                            _l.info("<END OF CONTENT>");
                            return (s.length());
                        }
                    });
                }
                return (pipeline);
            }
        };

        NClient.get(requested, pipelined);
    }

    private static void _http_post(final Options options) {
        _info(options);

        final RequestBuilder requested = new RequestBuilder(options.url(), options.data()) {
            @Override
            public FullHttpRequest setup(FullHttpRequest request) {
                // setup ur customized http headers/contents processing
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.DEFLATE);

                request.headers().set(
                        HttpHeaderNames.COOKIE,
                        ClientCookieEncoder.encode(
                                new DefaultCookie("my-cookie", "foo"),
                                new DefaultCookie("another-cookie", "bar")));

                return (request);
            }
        };
        final PipelineBuilder pipelined = new PipelineBuilder() {
            @Override
            public ChannelPipeline setup(ChannelPipeline pipeline) {
                // default http header processing
                if (options.header()) {
                    pipeline.addLast(new DefaultHeaderHandler());
                }

                // default http content processing
                if (options.body()) {
                    pipeline.addLast(new DefaultContentHandler<Integer>(A.OPTION_BLOCK_SIZE) {

                        @Override
                        protected Integer process(String s) {
                            _l.info(String.format("<BEGIN OF CONTENT:%s>", s.length()));
                            _l.info(s);
                            _l.info("<END OF CONTENT>");
                            return (s.length());
                        }
                    });
                }
                return (pipeline);
            }
        };

        NClient.post(requested, pipelined);
    }


    private static void _info(Options options) {
        _l.info(H.pad_right("OPTIONS:", A.OPTION_PROMPT_LEN, "="));
        _l.info(options);
        _l.info(H.pad_right(String.format("%s:", options.method()), A.OPTION_PROMPT_LEN, "="));
        _l.info(H.pad_right("RESPONSE:", A.OPTION_PROMPT_LEN, "="));
        _l.info(String.format("<Tid:%s>", H.tid()));
    }

    private static void _help() {
        _help(null);
    }

    private static void _help(final String m) {
        if (null != m) {
            _l.info(m);
        }
        _l.info(String.format("usage: %s %s", A.NAME,
                "[-h|--help] [-u|--url] [-g|--get] [-p|--post] [-c|--conf] [-s|--save] [-H|--header]"));
        _l.info("\t--url: specify the http url");
        _l.info("\t--get: http get method");
        _l.info("\t--post: http post method");
        _l.info("\t--conf: specify the configuration file");
        _l.info("\t--save: save configuration to the file");
        _l.info("\t--header: 0:header-content; 1:header-only; 2:content-only");
        System.exit(0);
    }
}
