package jpassport.test.callback;

import jpassport.Passport;

import java.lang.foreign.MemorySegment;

public interface CallbackNative extends Passport {
    int call_CB(MemorySegment fn, int v, double v2);
    void call_CBArr(MemorySegment fn, int[] vals, int count);
}
