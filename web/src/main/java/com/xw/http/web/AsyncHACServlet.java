package com.xw.http.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: junjie
 * Date: 8/13/15.
 * Target: <>
 */
public class AsyncHACServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        final AsyncContext ctx = req.startAsync(req, resp);

    }

    private static final Logger _l = LoggerFactory.getLogger(AsyncHACServlet.class);
}
