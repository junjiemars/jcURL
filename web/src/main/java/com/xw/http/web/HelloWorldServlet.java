package com.xw.http.web;

import com.xw.http.*;
import io.netty.channel.ChannelPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: junjie
 * Date: 4/20/15.
 * Target: <>
 */
public class HelloWorldServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        final ServletOutputStream out = resp.getOutputStream();

        NClient.request(new PostRequestBuilder("http://localhost:8082", "Hello") {

            @Override
            public void setup(PostRequestBuilder builder) {

            }
        }, new PipelineBuilder() {
            @Override
            protected void setup(ChannelPipeline channelPipeline) {
                channelPipeline.addLast(new DefaultContentHandler<Integer>() {
                    @Override
                    protected Integer process(final String s) {
//                        try {

//                        } catch (/*InterruptedException e*/) {
//                            return (-1);
//                        }
                        return (0);
                    }
                });
            }
        });



    }


    private static final Logger _l = LogManager.getLogger(HelloWorldServlet.class);
}
