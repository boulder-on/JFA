package jpassport.test.callback;

import jpassport.FunctionPtr;
import jpassport.Passport;

import java.lang.foreign.MemorySegment;

public interface CallbackNative extends Passport {
    int call_CB(FunctionPtr fn, int v, double v2);
    void call_CBArr(FunctionPtr fn, int[] vals, int count);
}
