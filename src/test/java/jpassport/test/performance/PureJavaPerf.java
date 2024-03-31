/* Copyright (c) 2021 Duncan McLean, All Rights Reserved
 *
 * The contents of this file is dual-licensed under the
 * Apache License 2.0.
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code.
 */
package jpassport.test.performance;

import jpassport.test.structs.TestStruct;

public class PureJavaPerf implements PerfTest{
    @Override
    public double sumD(double d, double d2) {
        return d + d2;
    }

    @Override
    public double sumArrD(double[] d, int len)
    {
        double ret = 0;
        for (int n = 0; n < len; ++n)
            ret += d[n];
        return ret;
    }

    @Override
    public int sumArrI(int[] d, int len)
    {
        int ret = 0;
        for (int n = 0; n < len; ++n)
            ret += d[n];
        return ret;
    }

    @Override
    public float sumArrF(float[] d, int len)
    {
        float ret = 0;
        for (int n = 0; n < len; ++n)
            ret += d[n];
        return ret;
    }

    public double passStruct(TestStruct simpleStruct) {
        return simpleStruct.s_int() + simpleStruct.s_long() + simpleStruct.s_float() + simpleStruct.s_double();
    }

    @Override
    public boolean hasMethod(String name) {
        return true;
    }

}
