package com.xw.http.web;

import com.xw.http.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Author: junjie
 * Date: 4/28/15.
 * Target: <>
 */
//@WebServlet(urlPatterns = {"/async"}, asyncSupported = true)
public class AsyncNioServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        final AsyncContext async = req.startAsync(req, resp);
        async.setTimeout(C.http_nio_timeout());

        final String uri = C.http_url();
        if (H.is_null_or_empty(uri)) {
            _l.info("#%s:<ENV:http.url> is null/empty", AsyncNioServlet.class.getSimpleName());
            return;
        }


        NClient.request(new PostRequestBuilder(uri, C.get_post_data(req)) {
            @Override
            public void setup(PostRequestBuilder builder) {
                headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;utf-8");
            }
        }, new PipelineBuilder() {
            @Override
            protected void setup(ChannelPipeline pipeline) {
                pipeline.addLast(new DefaultContentHandler<AsyncContext>(async) {
                    @Override
                    protected void process(String s) {
//                                _l.debug(String.format("#RECV:%d", s.length()));
                        final ServletResponse r = _t.getResponse();
                        if (r != null) {
                            try {
                                final PrintWriter w = r.getWriter();
                                w.write(s);
//                                w.write("\n" + C.host_name()+"\n");
                                w.flush();
                            } catch (IOException ioe) {
                                _l.error(ioe.getMessage(), ioe);
                            } finally {
                                _t.complete();
                            }
                        }
                    }

//                    @Override
//                    protected void complete() {
//                        _t.complete();
//                    }

                });
            }
        });

//        async.start(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
    }

    private static final Logger _l = LoggerFactory.getLogger(AsyncNioServlet.class);
}
