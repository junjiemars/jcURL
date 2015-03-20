package com.xw.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;
/* just since JDK1.7 (Java7)
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
*/

/**
 * Author: junjie
 * Date: 12/17/14.
 */
public final class H {
    public static final String[] EMPTY_STR_ARRAY = new String[]{};
    private static final Charset _UTF8 = Charset.forName("UTF-8");

    private static final Logger _out = LogManager.getLogger(H.class);

    private H() {
    }

    public static final String pad_right(final String s, final int length, final String c) {
        if (is_null_or_empty(s) || is_non_negative(s.length() - length) || is_null_or_empty(c)) {
            return (s);
        }

        final int d = length - s.length();
        if (d < c.length()) {
            return (s);
        }

        final String p = repeat(c, _floor_div(d, c.length()));
        final String t = s.concat(p);

        return (t);
    }

    public static final boolean is_null_or_empty(final String s) {
        return (null == s || s.length() == 0);
    }

    public static final long tid() {
        return (Thread.currentThread().getId());
    }
//
//    public static final <T> boolean is_null_or_empty(final T[] array) {
//        if (null == array || array.length == 0) {
//            return (true);
//        }
//
//        return (false);
//    }
//
//    public static final <T> boolean is_null_or_empty(final List<T> list) {
//        if (null == list || list.size() == 0) {
//            return (true);
//        }
//        return (false);
//    }
//
//    public static final String to_lowercase(final String s) {
//        if (is_null_or_empty(s)) {
//            return (s);
//        }
//
//        return (s.toLowerCase());
//    }
//
//    public static final String host_name() {
//        try {
//            InetAddress ip = InetAddress.getLocalHost();
//            final String n = ip.getHostName();
//            return (n);
//        } catch (final UnknownHostException e) {
//            _out.error(e);
//        }
//
//        return (null);
//    }

//    public static final String host_ipv4() {
//        return (null);
//    }

    public static final <T> String format(Class<T> clazz, final String arg) {
        final String s = String.format("%s::%s", clazz.getSimpleName(), arg);
        return (s);
    }

    public static <T> T from_json(final String json, final Type type) {
        if (H.is_null_or_empty(json) || null == type) {
            return (null);
        }

        final Gson gson = new Gson();
        try {
            final T t = gson.fromJson(json, type);
            return (t);
        } catch (JsonSyntaxException e) {
            _out.error(e);
        } catch (JsonParseException e) {
            _out.error(e);
        }

        return (null);
    }

    public static <T> String to_json(final T t, final Type type) {
        if (null == t) {
            return (null);
        }

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String j = gson.toJson(t, type);

        return (j);
    }
// NOTE: just since JDK1.7
//    public static final String read_file(final String file) {
//        byte[] b = null;
//        try {
//            b = Files.readAllBytes(Paths.get(file));
//        } catch (IOException e) {
//            _out.error(e);
//            return (null);
//        }
//
//        final String s = new String(b, _UTF8);
//        return (s);
//    }

    public static final String read_file(final String file) {
        if (is_null_or_empty(file)) {
            _out.error("<arg:file> is invalid");
            return (null);
        }

        final File f = new File(file);
        if (!f.exists()) {
            _out.warn(String.format("<file:%s> does not exists", file));
            return (null);
        }

        final byte[] b = new byte[(int) f.length()];
        try {
            InputStream in = null;
            try {
                int length = 0;
                in = new BufferedInputStream(new FileInputStream(f));
                while (length < b.length) {
                    final int count = b.length - length;
                    final int c = in.read(b, length, count);
                    if (c > 0) {
                        length += c;
                    }
                }
            } finally {
                in.close();
            }
        } catch (final FileNotFoundException e) {
            _out.error(e);
        } catch (final IOException e) {
            _out.error(e);
        }

        return (new String(b, _UTF8));
    }
// NOTE: just since JDK1.7
//    public static final void write_file(final String json, final String file) {
//        try {
//            Files.write(Paths.get(file), json.getBytes(_UTF8),
//                    StandardOpenOption.CREATE,
//                    StandardOpenOption.TRUNCATE_EXISTING);
//        } catch (IOException e) {
//            _out.error(e);
//        }
//    }

    public static final void write_file(final String json, final String file) {
        if (is_null_or_empty(json) || is_null_or_empty(file)) {
            _out.error("<arg:json:file> is invalid");
            return;
        }

        try {
            OutputStream out = null;
            try {
                out = new BufferedOutputStream(new FileOutputStream(file));
                out.write(json.getBytes(_UTF8));
            } finally {
                out.close();
            }
        } catch (final FileNotFoundException e) {
            _out.error(e);
        } catch (final IOException e) {
            _out.error(e);
        }
    }

//
//    public static final Long str_to_long(final String s) {
//        if (null == s || s.length() == 0) {
//            return (null);
//        }
//
//        try {
//            final Long l = Long.parseLong(s);
//            return (l);
//        } catch (final NumberFormatException n) {
//            // do nothing
//        }
//
//        return (null);
//    }

    private static final boolean is_non_negative(final int n) {
        return (n >= 0);
    }

    private static final String repeat(final String s, final int c) {
        if (is_null_or_empty(s) || c < 1) {
            return (s);
        }

        final StringBuilder b = new StringBuilder(s.length() * c);
        for (int i = 0; i < c; i++) {
            b.append(s);
        }

        return (b.toString());
    }

    private static final int _floor_div(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return (r);
    }
}
