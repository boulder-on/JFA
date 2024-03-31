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

import com.sun.jna.Native;
import jpassport.MemoryBlock;
import jpassport.Pointer;
import jpassport.annotations.RefArg;
import jpassport.test.performance.PerfTest;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class TestLinkJNADirect
{
    public static native double sumD(double d, double d2);
    public static native double sumArrD(double[] d, int len);
    public static native double sumArrDD(double[] d, double[] dd, int len);
    public static native void readD(double[] d, int set);

    public static native float sumArrF(float[] d, int len);
    public static native void readF(float[] d, float set);

    public static native long sumArrL(long[] i, long len);
    public static native void readL(long[] d, long set);

    public static native int sumArrI(int[] i, int len);
    public static native void readI(int[] d, int set);

    public static native short sumArrS(short[] i, short len);
    public static native void readS(short[] d, short set);

    public static native byte sumArrB(byte[] i, byte len);
    public static native void readB(byte[] d, byte set);
//    public static native int[] mallocInts(int count);

    static
    {
        Native.register("passport_test");
    }

    public static class JNADirect implements TestLink, PerfTest {
        @Override
        public void functionDoesNotExist(double v) {

        }



        @Override
        public double sumD(double d, double d2) {
            return TestLinkJNADirect.sumD(d, d2);
        }

        @Override
        public double sumArrD(double[] d, int len) {
            return TestLinkJNADirect.sumArrD(d, len);
        }

        @Override
        public double sumArrDD(double[] d, double[] d2, int len) {
            return TestLinkJNADirect.sumArrDD(d, d2, len);
        }

        @Override
        public void readD(double[] d, int set) {
            TestLinkJNADirect.readD(d, set);
        }

        @Override
        public float sumArrF(float[] i, int len) {
            return TestLinkJNADirect.sumArrF(i, len);
        }

        @Override
        public void readF(float[] d, float set) {
            TestLinkJNADirect.readF(d, set);
        }

        @Override
        public long sumArrL(long[] i, long len) {
            return TestLinkJNADirect.sumArrL(i, len);
        }

        @Override
        public void readL(long[] d, long set) {
            TestLinkJNADirect.readL(d, set);
        }

        @Override
        public int sumArrI(int[] i, int len) {
            return TestLinkJNADirect.sumArrI(i, len);
        }

        @Override
        public void readI(int[] d, int set) {
            TestLinkJNADirect.readI(d, set);
        }

        @Override
        public short sumArrS(short[] i, short len) {
            return TestLinkJNADirect.sumArrS(i, len);
        }

        @Override
        public void readS(short[] d, short set) {
            TestLinkJNADirect.readS(d, set);
        }

        @Override
        public byte sumArrB(byte[] i, byte len) {
            return TestLinkJNADirect.sumArrB(i, len);
        }

        @Override
        public void readB(byte[] d, byte set) {
            TestLinkJNADirect.readB(d, set);
        }

        @Override
        public double sumMatD(int rows, int cols, double[][] mat) {
            return 0;
        }

        @Override
        public double sumMatDPtrPtr(int rows, int cols, double[][] mat) {
            return 0;
        }

        @Override
        public float sumMatF(int rows, int cols, float[][] mat) {
            return 0;
        }

        @Override
        public float sumMatFPtrPtr(int rows, int cols, float[][] mat) {
            return 0;
        }

        @Override
        public long sumMatL(int rows, int cols, long[][] mat) {
            return 0;
        }

        @Override
        public long sumMatLPtrPtr(int rows, int cols, long[][] mat) {
            return 0;
        }

        @Override
        public int sumMatI(int rows, int cols, int[][] mat) {
            return 0;
        }

        @Override
        public int sumMatIPtrPtr(int rows, int cols, int[][] mat) {
            return 0;
        }

        @Override
        public int sumMatS(int rows, int cols, short[][] mat) {
            return 0;
        }

        @Override
        public int sumMatSPtrPtr(int rows, int cols, short[][] mat) {
            return 0;
        }

        @Override
        public int sumMatB(int rows, int cols, byte[][] mat) {
            return 0;
        }

        @Override
        public int sumMatBPtrPtr(int rows, int cols, byte[][] mat) {
            return 0;
        }

        @Override
        public int cstringLength(String s) {
            return 0;
        }

        @Override
        public String mallocString(String orig) {
            return new String(orig);
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

        @Override
        public Object readStruct(MemorySegment segment, Object rec) {
            return null;
        }

        public void readPointer(Pointer[] val, long set)
        {}

        public Pointer getPointer(Pointer[] val, long set)
        {
            return null;
        }

        public int swapStrings(@RefArg String[] vals, int i, int j)
        {
            return 0;
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
}
