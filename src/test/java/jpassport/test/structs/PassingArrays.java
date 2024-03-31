package jpassport.test.structs;

import jpassport.annotations.Array;
import jpassport.annotations.Ptr;

public record PassingArrays(
        @Array(length = 5) double[] s_double,
        @Array(length = 8) long[] s_long,
        long s_doublePtrCount,
        @Ptr double[] s_doublePtr,
        long s_longPtrCount,
        @Ptr long[] s_longPtr)
{
}
