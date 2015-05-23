package com.xw.http.web;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xw.http.H;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;

/**
 * Author:junjie
 * Create:17/5/15.
 * Target:<>
 */
//@SpringBootApplication
public final class C {
    private C() {}

    public static void main(String[] args) {
//        SpringApplication.run(C.class, args);
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

    public static final <T extends ServletResponse> void output_str(final T resp, final String... s) {
        try {
//            final PrintWriter w = resp.getWriter();
//            for (String i : s) {
//                w.write(i + '\n');
//            }
//            w.flush();
//            w.close();
            final ServletOutputStream o = resp.getOutputStream();
            for (String i : s) {
                o.println(i);
            }
            o.flush();
//            o.close();
        } catch (IOException e) {
            _l.error(e);
        }
    }

    public static final String http_url() {
        final String s = System.getProperty("http.url");
        return s;
    }

    public static final int http_timeout() {
        final String s = System.getProperty("http.timeout");
        return (H.str_to_int(s, 3000));
    }

    public static final String host_name() {
        try {
            final String h = String.format("[%s]", java.net.InetAddress.getLocalHost().getHostName());
            return h;
        } catch (UnknownHostException e) {
            _l.error(e);
        }
        return "[host.unknown]";
    }

    private static final Logger _l = LogManager.getLogger(C.class);
}
