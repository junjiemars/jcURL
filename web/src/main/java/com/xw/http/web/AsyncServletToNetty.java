package com.xw.http.web;

import com.xw.http.DefaultContentHandler;
import com.xw.http.NClient;
import com.xw.http.PipelineBuilder;
import com.xw.http.PostRequestBuilder;
import io.netty.channel.ChannelPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Author: junjie
 * Date: 4/28/15.
 * Target: <>
 */
@WebServlet(value = "/netty", asyncSupported = true)
public class AsyncServletToNetty extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        // create the async context, otherwise getAsyncContext() will be null
        final AsyncContext ctx = req.startAsync();

        // set the timeout
        ctx.setTimeout(2000);

        NClient.request(new PostRequestBuilder("http://cn.bing.com", "Hello") {
            @Override
            public void setup(PostRequestBuilder builder) {

            }
        }, new PipelineBuilder() {
            @Override
            protected void setup(ChannelPipeline pipeline) {
                pipeline.addLast(new DefaultContentHandler<String>() {

                    @Override
                    protected String process(String s) {
                        try {
                            ctx.getResponse().getWriter().write(s);
                        } catch (IOException e) {
                            _l.error(e);
                        }
                        return s;
                    }
                });
            }
        });
    }

    private static final Logger _l = LogManager.getLogger(AsyncServletToNetty.class);
}
