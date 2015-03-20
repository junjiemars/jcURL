package com.xw.http;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
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
                new LongOpt("content", LongOpt.OPTIONAL_ARGUMENT, null, 'b')
        };

        final Getopt g = new Getopt(A.NAME, args, "hc:s:u:gpb:;", opts);
        g.setOpterr(true);
        int c;

        String conf = null;
        String save = null;
        String url = null;
        HttpMethod method = null;
        String content = null;

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
                case 'b':
                    content = g.getOptarg();
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
                new Options(url, method, content) : Options.read(conf)
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

    private static final void _http_get(final Options options) {
        _info(options);

        final RequestBuilder requested = new RequestBuilder(options.url()) {
            @Override
            public FullHttpRequest setup(FullHttpRequest request) {
                // setup ur customized http headers/contents processing
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
        final PipelineBuilder pipelined = new PipelineBuilder() {
            @Override
            public ChannelPipeline setup(ChannelPipeline pipeline) {
                // setup ur customized http response/contents processing
                pipeline.addLast(new HttpClientCodec());

                // auto decompression
                pipeline.addLast(new HttpContentDecompressor());

                // default http response processing
                pipeline.addLast(new DefaultResponseHandler());

                // default http content processing
                pipeline.addLast(new DefaultContentHandler());

                return (pipeline);
            }
        };

        NClient.get(requested, pipelined);
    }

    private static final void _http_post(final Options options) {
        _info(options);

        final RequestBuilder requested = new RequestBuilder(options.url(), options.content()) {
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
                // setup ur customized http response/contents processing
                pipeline.addLast(new HttpClientCodec());

                // auto decompression
                pipeline.addLast(new HttpContentDecompressor());

                // default http response processing
                pipeline.addLast(new DefaultResponseHandler());

                // default http content processing
                pipeline.addLast(new DefaultContentHandler());

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
}
