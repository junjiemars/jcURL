package com.xws.nio.base;

/**
 * Author: junjie
 * Date: 3/31/15.
 * Target: <>
 */
public abstract class Tuple<X, Y, V> {
    public Tuple(final X x, final Y y) {
        _x = x;
        _y = y;
    }

    public final X x() {
        return (_x);
    }

    public final Y y() {
        return (_y);
    }

    public abstract V call();
    protected final X _x;
    protected final Y _y;
}
