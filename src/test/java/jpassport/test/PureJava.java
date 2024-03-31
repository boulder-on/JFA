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
package jpassport.test;


import jpassport.MemoryBlock;
import jpassport.Pointer;
import jpassport.annotations.RefArg;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class PureJava implements TestLink
{

    @Override
    public void functionDoesNotExist(double v) {

    }

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
    public double sumArrDD(double[] d, double[] d2, int len) {
        double ret = 0;
        for (int n = 0; n < len; ++n)
        {
            ret += d[n];
            ret += d2[n];
        }
        return ret;
    }

    @Override
    public void readD(double[] d, int set) {
        d[0] = set;
    }

    @Override
    public float sumArrF(float[] d, int len)
    {
        float ret = 0;
        for (int n = 0; n < len; ++n)
            ret += d[n];
        return ret;
    }

    @Override
    public void readF(float[] d, float set) {
        d[0] = set;
    }

    @Override
    public long sumArrL(long[] d, long len)
    {
        long ret = 0;
        for (int n = 0; n < len; ++n)
            ret += d[n];
        return ret;
    }

    @Override
    public void readL(long[] d, long set) {
        d[0] = set;
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
    public void readI(int[] d, int set) {
        d[0] = set;
    }

    @Override
    public short sumArrS(short[] d, short len)
    {
        short ret = 0;
        for (int n = 0; n < len; ++n)
            ret += d[n];
        return ret;
    }

    @Override
    public void readS(short[] d, short set) {
        d[0] = set;
    }

    @Override
    public byte sumArrB(byte[] d, byte len)
    {
        byte ret = 0;
        for (int n = 0; n < len; ++n)
            ret += d[n];
        return ret;
    }

    @Override
    public void readB(byte[] d, byte set) {
        d[0] = set;
    }

    @Override
    public double sumMatD(int rows, int cols, double[][] mat)
    {
        double total = 0;
        for (int y = 0; y < rows; ++y)
        {
            for (double i : mat[y])
                total += i;
        }
        return total;
    }

    @Override
    public double sumMatDPtrPtr(int rows, int cols, double[][] mat) {
        return sumMatD(rows, cols, mat);
    }

    @Override
    public float sumMatF(int rows, int cols, float[][] mat)
    {
        float total = 0;
        for (int y = 0; y < rows; ++y)
        {
            for (float i : mat[y])
                total += i;
        }
        return total;
    }

    @Override
    public float sumMatFPtrPtr(int rows, int cols, float[][] mat) {
        return sumMatF(rows, cols, mat);
    }

    @Override
    public long sumMatL(int rows, int cols, long[][] mat)
    {
        long total = 0;
        for (int y = 0; y < rows; ++y)
        {
            for (long i : mat[y])
                total += i;
        }
        return total;
    }

    @Override
    public long sumMatLPtrPtr(int rows, int cols, long[][] mat) {
        return sumMatL(rows, cols, mat);
    }

    @Override
    public int sumMatI(int rows, int cols, int[][] mat)
    {
        int total = 0;
        for (int y = 0; y < rows; ++y)
        {
            for (int i : mat[y])
                total += i;
        }
        return total;
    }

    @Override
    public int sumMatIPtrPtr(int rows, int cols, int[][] mat) {
        return sumMatI(rows, cols, mat);
    }

    @Override
    public int sumMatS(int rows, int cols, short[][] mat)
    {
        int total = 0;
        for (int y = 0; y < rows; ++y)
        {
            for (int i : mat[y])
                total += i;
        }
        return total;
    }

    @Override
    public int sumMatSPtrPtr(int rows, int cols, short[][] mat) {
        return sumMatS(rows, cols, mat);
    }

    @Override
    public int sumMatB(int rows, int cols, byte[][] mat)
    {
        int total = 0;
        for (int y = 0; y < rows; ++y)
        {
            for (int i : mat[y])
                total += i;
        }
        return total;
    }

    @Override
    public int sumMatBPtrPtr(int rows, int cols, byte[][] mat) {
        return sumMatB(rows, cols, mat);
    }

    @Override
    public int cstringLength(String s)
    {
        return s.length();
    }

    @Override
    public String mallocString(String origString) {
        return new String(origString);
    }

    @Override
    public MemorySegment mallocDoubles(int count) {
        return null;
    }

    @Override
    public void freeMemory(MemorySegment address) {
    }

    @Override
    public boolean hasMethod(String name) {
        return true;
    }

    public void readPointer(Pointer[] val, long set)
    {
        val[0] = new Pointer(MemorySegment.ofAddress(set));
    }
    public Pointer getPointer(Pointer[] val, long set)
    {
        readPointer(val, set);
        return val[0];
    }

    public int swapStrings(@RefArg String[] vals, int i, int j)
    {
        var s = vals[i];
        vals[i] = vals[j];
        vals[j] = s;
        return vals[i].length() + vals[j].length();
    }

    public int fillChars(Arena a, MemoryBlock fillThis, int sizemax)
    {
        String s= "hello world";
        fillThis.setString(s);
        return s.length();
    }
    public int passChars(char[] fillThis, int sizemax)
    {
        int s = 0;
        for (char c : fillThis)
            s += c;
        return s;
    }
}
