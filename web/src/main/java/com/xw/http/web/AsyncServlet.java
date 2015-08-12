package com.xw.http.web;

import com.xw.http.*;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
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
public class AsyncServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        final AsyncContext async = req.startAsync();
        async.setTimeout(C.http_nio_timeout());

        final String uri = C.http_url();
        if (H.is_null_or_empty(uri)) {
            C.output_str(async.getResponse(), "<R:ENV:http.url> is null/empty");
            return;
        }

        async.start(new Runnable() {
            @Override
            public void run() {
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
                                        w.write("\n" + C.host_name()+"\n");
                                        w.flush();
                                        _t.complete();
                                    } catch (IOException ioe) {
                                        _l.error(ioe.getMessage(), ioe);
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private static final Logger _l = LoggerFactory.getLogger(AsyncServlet.class);
}
