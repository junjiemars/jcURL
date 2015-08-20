package com.xw.http;

import java.nio.ByteBuffer;

/**
 * Created by junjie on 8/19/15.
 */
public abstract class Receiver {
    private final boolean _progressive;

    public Receiver() {
        this(false);
    }

    public Receiver(boolean progressive) {
        _progressive = progressive;
    }

    public final boolean progressive() {
        return _progressive;
    }

    public abstract void onReceive(final String s);
    public abstract void onDone();
}
