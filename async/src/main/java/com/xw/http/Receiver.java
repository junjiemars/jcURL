package com.xw.http;

import java.nio.ByteBuffer;

/**
 * Created by junjie on 8/19/15.
 */
public abstract class Receiver {
    public Receiver() {
    }

    public abstract void onReceive(final String s);
}
