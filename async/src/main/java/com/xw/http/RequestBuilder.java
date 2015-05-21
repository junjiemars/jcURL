package com.xw.http;

import io.netty.util.AsciiString;

/**
 * Author: junjie
 * Date: 4/2/15.
 * Target: <>
 */
public interface RequestBuilder<T> {
    public T build();
    public void setup(final T t);

    public static final AsciiString TEXT_XML = new AsciiString("text/xml;charset=utf-8");

    public static final AsciiString USER_AGENT = new AsciiString(A.NAME);

    public static final AsciiString ACCEPT_ALL = new AsciiString("*/*");

}
