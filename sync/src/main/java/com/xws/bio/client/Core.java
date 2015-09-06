package com.xws.bio.client;

import com.xws.nio.base.H;
import com.xws.nio.base.Tuple;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
                new LongOpt("header", LongOpt.OPTIONAL_ARGUMENT, null, 'H'),
                new LongOpt("timeout", LongOpt.OPTIONAL_ARGUMENT, null, 't'),
                new LongOpt("concurrent", LongOpt.OPTIONAL_ARGUMENT, null, 'm'),
                new LongOpt("cpu", LongOpt.NO_ARGUMENT, null, 'P')
        };

        final Getopt g = new Getopt(A.NAME, args, "hc:s:u:gpd:H:t:m:P;", opts);
        g.setOpterr(true);
        int c;

        String conf = null;
        String save = null;
        String url = null;
        HttpMethod method = null;
        String data = null;
        int header = 0;
        int timeout = 0;
        int concurrent = 0;
        int cpu_sensible = 0;

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
                case 't':
                    timeout = H.str_to_int(g.getOptarg(), 0);
                    break;
                case 'm':
                    concurrent = H.str_to_int(g.getOptarg(), 0);
                    break;
                case 'P':
                    cpu_sensible = Runtime.getRuntime().availableProcessors();
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

        final Options options = (H.is_null_or_empty(conf)
                ? new Options(url, method, data, header, timeout, concurrent, cpu_sensible) : Options.read(conf)
        );
        if (null == options) {
            _l.error(String.format("<var:options> can't create Options%s",
                    H.is_null_or_empty(conf) ? null : String.format(" from %s", conf)));
            return;
        }
        if (!H.is_null_or_empty(save)) {
            Options.save(options, save);
        }

        if (options.concurrent() <= 0) {
            (HttpMethod.POST.equals(options.method()) ? _http_post(options) : _http_get(options)).call();
        } else {
            Runtime.getRuntime().availableProcessors();
            final ExecutorService e = Executors.newFixedThreadPool(options.cpu() > 0 ? options.cpu() : options.concurrent());
            final Set<Callable<Boolean>> invokes = new HashSet<Callable<Boolean>>(options.concurrent());

            for (int i = 0; i < options.concurrent(); i++) {
                invokes.add(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ((HttpMethod.POST.equals(options.method())
                                ? _http_post(options) : _http_get(options)).call());
                    }
                });
            }

            try {
                e.invokeAll(invokes);
            } catch (final InterruptedException ex) {
                _l.error(ex);
            } finally {
                e.shutdown();
            }

            while (!e.isTerminated()) {

            }
            _l.info("###$$$");
        }


        _l.info(H.pad_right("*", A.OPTION_PROMPT_LEN, "="));
    }

    private static Tuple<RequestBuilder<HttpGet>, ResponseHandler<String>, Boolean>
    _http_get(final Options options) {
        _info(options);

        final RequestBuilder<HttpGet> requested = new GetRequestBuilder(options.url()) {
            @Override
            public void setup(final HttpGet get) {

            }
        };

        final ResponseHandler<String> pipelined = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                final HttpEntity e = response.getEntity();
                if (null == e) {
                    _l.info("<R:Entity> is null/empty");
                    return null;
                }
                final String s = EntityUtils.toString(e);
                _l.info(String.format("<BEGIN OF CONTENT:%s>", s.length()));
                _l.info(s);
                _l.info("<END OF CONTENT>");
                return (s);

            }
        };


        return (new Tuple<RequestBuilder<HttpGet>, ResponseHandler<String>, Boolean>(requested, pipelined) {
            @Override
            public Boolean call() {
                return (SClient.request(x(), y()));
            }
        });
    }

    private static Tuple<RequestBuilder<HttpPost>, ResponseHandler<String>, Boolean>
    _http_post(final Options options) {
        _info(options);
        final Long begin = System.currentTimeMillis();

        final RequestBuilder<HttpPost> requested = new PostRequestBuilder(options.url(), options.data()) {
            @Override
            public void setup(final HttpPost post) {
            }
        };
        final ResponseHandler<String> pipelined = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                if (!options.body()) return (null);
                final HttpEntity r = response.getEntity();
                if (null == r) {
                    _l.info("<R:Entity> is null/empty");
                    return null;
                }
                final String s = EntityUtils.toString(r);
                _l.info(H.pad_right(String.format("#R-CONTENT-A<Tid:%d|Len:%d|#%d>",
                                H.tid(), s.length(), _sn.getAndIncrement()),
                        A.OPTION_PROMPT_LEN, "="));
                _l.info(s);
                _l.info(H.pad_right(String.format("#R-CONTENT-Z<Tid:%d|Len:%d|#%d>",
                                H.tid(), s.length(), _sn.get()),
                        A.OPTION_PROMPT_LEN, "="));

                _l.info(String.format("elapsed:%d", System.currentTimeMillis() - begin));
                return s;
            }
        };


        return (new Tuple<RequestBuilder<HttpPost>, ResponseHandler<String>, Boolean>(requested, pipelined) {
            @Override
            public Boolean call() {
                return (SClient.request(x(), y()));
            }
        });
    }

    private static void _info(Options options) {
        _l.info(H.pad_right(String.format("PWD<Tid:%s>", H.tid()), A.OPTION_PROMPT_LEN, "="));
        _l.info(System.getProperty("user.dir"));
        _l.info(H.pad_right(String.format("OPTIONS<Tid:%s>", H.tid()), A.OPTION_PROMPT_LEN, "="));
        _l.info(options);
        _l.info(H.pad_right(String.format("%s<Tid:%d>", options.method(), H.tid()),
                A.OPTION_PROMPT_LEN, "="));
    }

    private static void _help() {
        _help(null);
    }

    private static void _help(final String m) {
        if (null != m) {
            _l.info(m);
        }
        _l.info(String.format("usage: %s %s", A.NAME,
                "[-h|--help] [-u|--url] [-g|--get] [-p|--post] [-c|--conf] [-s|--save] [-H|--header] [-t|--timeout] [-m|--concurrent]"));
        _l.info("\t--url: specify the http url");
        _l.info("\t--get: http get method");
        _l.info("\t--post: http post method");
        _l.info("\t--conf: specify the configuration file");
        _l.info("\t--save: save configuration to the file");
        _l.info("\t--header: 0:header-content; 1:header-only; 2:content-only");
        _l.info("\t--timeout: default 0 milliseconds");
        _l.info("\t--concurrent: concurrently run");
        _l.info("\t--cpu: concurrent threads equals processors count");
        System.exit(0);
    }

    private static final AtomicInteger _sn = new AtomicInteger(0);
}
