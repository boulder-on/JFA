package jpassport.test.callback;

import jpassport.PassportFactory;
import jpassport.Utils;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

public class CallbackObj {
    public int calls = 0;

    public int callback(int n, double m) {
        calls++;
        return (int) (n + m);
    }

    public MemorySegment getAsFunctionPtr()
    {
        return PassportFactory.createCallback(this, "callback").getPtr();
    }

    public int sum = 0;

//    public void callbackArr(MemorySegment ptr, int count) {
//        var vals = Utils.toArr(ValueLayout.JAVA_INT, ptr, ptr.address(), count);
//        sum = Arrays.stream(vals).sum();
//    }

    public void callbackArr(MemorySegment ptr, int count) {
        var vals = Utils.toArr(ValueLayout.JAVA_INT, ptr, count);
        sum = Arrays.stream(vals).sum();
    }

    public MemorySegment getAsFunctionArrPtr()
    {
        return PassportFactory.createCallback(this, "callbackArr").getPtr();
    }
}
