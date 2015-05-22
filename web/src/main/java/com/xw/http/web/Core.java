package com.xw.http.web;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Author:junjie
 * Create:17/5/15.
 * Target:<>
 */
//@SpringBootApplication
public class Core {
    public static void main(String[] args) {
//        SpringApplication.run(Core.class, args);
    }

    public static final String get_post_data(final HttpServletRequest req) {
        final StringBuffer d = new StringBuffer();
        String line = null;
        try {
            final BufferedReader r = req.getReader();
            while ((line = r.readLine()) != null)
                d.append(line);
            return d.toString();
        } catch (Exception e) {
            _l.error(e);
        }

        return null;
    }

    public static <T extends ServletResponse> void output_str(final T resp, final String s) {
        try {
            final ServletOutputStream o = resp.getOutputStream();
            o.println(s);
            o.flush();
            o.close();
        } catch (IOException e) {
            _l.error(e);
        }
    }

    private static final Logger _l = LogManager.getLogger(Core.class);
}
