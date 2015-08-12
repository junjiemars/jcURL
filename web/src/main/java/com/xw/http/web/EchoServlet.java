package com.xw.http.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: junjie
 * Date: 5/22/15.
 * Target: <>
 */
public class EchoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        final int timeout = C.http_bio_timeout()/2;
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            _l.error(e.getMessage(), e);
        }

        final String s = C.get_post_data(req);
        C.output_str(resp, s, C.host_name());
    }

    private static final Logger _l = LoggerFactory.getLogger(EchoServlet.class);
}
