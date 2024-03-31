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
package jpassport;


import jpassport.annotations.Array;
import jpassport.annotations.Ptr;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;


public class Utils {

    public static MemorySegment toAddr(MemorySegment seg) {
        if (seg == null)
            return MemorySegment.NULL;

        return seg;
    }

    /* Double ///////////////////////////////////////////////////////////////// */
    public static MemorySegment toMS(SegmentAllocator scope, double[] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;
        return isReadBackOnly ? scope.allocate((long)arr.length * Double.BYTES) :
                scope.allocateFrom(ValueLayout.JAVA_DOUBLE, arr);
    }

    public static MemorySegment toMS(SegmentAllocator scope, double[][] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * arr[0].length * Double.BYTES);
        int n = 0;
        for (double[] row : arr) {
            segment.asSlice(n, (long) row.length * Double.BYTES).copyFrom(MemorySegment.ofArray(row));
            n += row.length * Double.BYTES;
        }

        return segment;
    }

    public static MemorySegment toPtrPTrMS(SegmentAllocator scope, double[][] arr) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * Long.BYTES);
        int n = 0;
        for (double[] a : arr) {
            MemorySegment subSeg = scope.allocateFrom(ValueLayout.JAVA_DOUBLE, a);
            segment.setAtIndex(ValueLayout.ADDRESS, n++, subSeg);
        }
        return segment;
    }

    public static void toArr(double[] arr, MemorySegment segment) {
        if (arr == null)
            return;

        MemorySegment.copy(segment, ValueLayout.JAVA_DOUBLE, 0, arr, 0, arr.length);
    }

    public static double[] toArr(ValueLayout.OfDouble layout, MemorySegment seg, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        return slice(seg, addr, count * layout.byteSize()).toArray(layout);
    }

    public static double[] toArr(ValueLayout.OfDouble layout, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        if (addr.byteSize() == 0)
        {
            var seg = MemorySegment.ofAddress(addr.address()).reinterpret((long)Double.BYTES * count);
            return seg.toArray(ValueLayout.JAVA_DOUBLE);
        }

        return addr.asSlice(0, (long) count * Long.BYTES).toArray(layout);
    }

    /* Float ///////////////////////////////////////////////////////////////// */

    public static MemorySegment toMS(SegmentAllocator scope, float[] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        return isReadBackOnly ? scope.allocate(arr.length * Float.BYTES) :
                scope.allocateFrom(ValueLayout.JAVA_FLOAT, arr);
    }

    public static MemorySegment toMS(SegmentAllocator scope, float[][] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * arr[0].length * Float.BYTES);
        int n = 0;
        for (float[] row : arr) {
            segment.asSlice(n, (long) row.length * Float.BYTES).copyFrom(MemorySegment.ofArray(row));
            n += row.length * Float.BYTES;
        }

        return segment;
    }

    public static MemorySegment toPtrPTrMS(SegmentAllocator scope, float[][] arr) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * Long.BYTES);
        int n = 0;
        for (float[] a : arr) {
            MemorySegment subSeg = scope.allocateFrom(ValueLayout.JAVA_FLOAT, a);
            segment.setAtIndex(ValueLayout.ADDRESS, n++, subSeg);
        }
        return segment;
    }

    public static void toArr(float[] arr, MemorySegment segment) {
        if (arr == null)
            return;

        MemorySegment.copy(segment, ValueLayout.JAVA_FLOAT, 0, arr, 0, arr.length);
    }

    public static float[] toArr(ValueLayout.OfFloat layout, MemorySegment seg, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        return slice(seg, addr, count * layout.byteSize()).toArray(layout);
    }

    public static float[] toArr(ValueLayout.OfFloat layout, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        if (addr.byteSize() == 0)
        {
            var seg = MemorySegment.ofAddress(addr.address()).reinterpret((long)Float.BYTES * count);
            return seg.toArray(ValueLayout.JAVA_FLOAT);
        }

        return addr.asSlice(0, (long) count * Long.BYTES).toArray(layout);
    }

    /* Pointers ///////////////////////////////////////////////////////////////// */

    public static MemorySegment toMS(SegmentAllocator scope, GenericPointer[] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        var pass = Arrays.stream(arr).mapToLong(gp -> gp == null ? MemorySegment.NULL.address() : gp.getPtr().address()).toArray();
        return isReadBackOnly ? scope.allocate((long)arr.length * Long.BYTES) :
                scope.allocateFrom(ValueLayout.JAVA_LONG, pass);
    }
    public static void toArr(GenericPointer[] arr, MemorySegment segment) {
        if (arr == null)
            return;

        var asLong = new long[arr.length];
        toArr(asLong, segment);

        for (int n = 0; n < asLong.length; ++n)
        {
            var addr = MemorySegment.ofAddress(asLong[n]);
            if (arr[n] == null)
            {
                try {
                    var cons = arr.getClass().getComponentType().getConstructor(MemorySegment.class);
                    arr[n] = (GenericPointer) cons.newInstance(addr);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            else
                arr[n].ptr = addr;
        }
    }

    /* Long ///////////////////////////////////////////////////////////////// */

    public static MemorySegment toMS(SegmentAllocator scope, long[] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;
        return isReadBackOnly ? scope.allocate((long)arr.length * Long.BYTES) :
                scope.allocateFrom(ValueLayout.JAVA_LONG, arr);
    }

    public static MemorySegment toPtrPTrMS(SegmentAllocator scope, long[][] arr) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * Long.BYTES);
        int n = 0;
        for (long[] a : arr) {
            MemorySegment subSeg = scope.allocateFrom(ValueLayout.JAVA_LONG, a);
            segment.setAtIndex(ValueLayout.ADDRESS, n++, subSeg);
        }
        return segment;
    }

    public static MemorySegment toMS(SegmentAllocator scope, long[][] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * arr[0].length * Long.BYTES);
        int n = 0;
        for (long[] row : arr) {
            segment.asSlice(n, (long) row.length * Long.BYTES).copyFrom(MemorySegment.ofArray(row));
            n += row.length * Long.BYTES;
        }

        return segment;
    }

    public static void toArr(long[] arr, MemorySegment segment) {
        if (arr == null)
            return;

        MemorySegment.copy(segment, ValueLayout.JAVA_LONG, 0, arr, 0, arr.length);
    }

    public static long[] toArr(ValueLayout.OfLong layout, MemorySegment seg, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        return slice(seg, addr, count * layout.byteSize()).toArray(layout);
    }

    public static long[] toArr(ValueLayout.OfLong layout, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        if (addr.byteSize() == 0)
        {
            var seg = MemorySegment.ofAddress(addr.address()).reinterpret((long)Long.BYTES * count);
            return seg.toArray(ValueLayout.JAVA_LONG);
        }

        return addr.asSlice(0, (long) count * Long.BYTES).toArray(layout);
    }

    /* Int ///////////////////////////////////////////////////////////////// */

    public static MemorySegment toMS(SegmentAllocator scope, int[] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        return isReadBackOnly ? scope.allocate(arr.length * Integer.BYTES) :
                scope.allocateFrom(ValueLayout.JAVA_INT, arr);
    }

    public static MemorySegment toPtrPTrMS(SegmentAllocator scope, int[][] arr) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * Long.BYTES);
        int n = 0;
        for (int[] a : arr) {
            segment.setAtIndex(ValueLayout.ADDRESS, n++, scope.allocateFrom(ValueLayout.JAVA_INT, a));
        }
        return segment;
    }

    public static MemorySegment toMS(SegmentAllocator scope, int[][] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * arr[0].length * Integer.BYTES);
        int n = 0;
        for (int[] row : arr) {
            segment.asSlice(n, (long) row.length * Integer.BYTES).copyFrom(MemorySegment.ofArray(row));
            n += row.length * Integer.BYTES;
        }

        return segment;
    }

    public static void toArr(int[] arr, MemorySegment segment) {
        if (arr == null)
            return;

        MemorySegment.copy(segment, ValueLayout.JAVA_INT, 0, arr, 0, arr.length);
    }

    public static int[] toArr(ValueLayout.OfInt layout, MemorySegment seg, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        return slice(seg, addr, count * layout.byteSize()).toArray(layout);
    }

    public static int[] toArr(ValueLayout.OfInt layout, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        if (addr.byteSize() == 0)
        {
            return  MemorySegment.ofAddress(addr.address()).
                    reinterpret((long)Integer.BYTES * count).toArray(ValueLayout.JAVA_INT);
        }

        return addr.asSlice(0, (long) count * Integer.BYTES).toArray(layout);
    }


    /* Short ///////////////////////////////////////////////////////////////// */

    public static MemorySegment toMS(SegmentAllocator scope, short[] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        return isReadBackOnly ? scope.allocate((int) (arr.length * Short.BYTES)) :
                scope.allocateFrom(ValueLayout.JAVA_SHORT, arr);
    }

    public static MemorySegment toPtrPTrMS(SegmentAllocator scope, short[][] arr) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * Long.BYTES);
        int n = 0;
        for (short[] a : arr) {
            segment.setAtIndex(ValueLayout.ADDRESS, n++, scope.allocateFrom(ValueLayout.JAVA_SHORT, a));
        }
        return segment;
    }

    public static MemorySegment toMS(SegmentAllocator scope, short[][] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * arr[0].length * Short.BYTES);
        int n = 0;
        for (short[] row : arr) {
            segment.asSlice(n, (long) row.length * Short.BYTES).copyFrom(MemorySegment.ofArray(row));
            n += row.length * Short.BYTES;
        }

        return segment;
    }

    public static void toArr(short[] arr, MemorySegment segment) {
        if (arr == null)
            return;
        MemorySegment.copy(segment, ValueLayout.JAVA_SHORT, 0, arr, 0, arr.length);
    }

    public static short[] toArr(ValueLayout.OfShort layout, MemorySegment seg, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        return slice(seg, addr, count * layout.byteSize()).toArray(layout);
    }

    public static short[] toArr(ValueLayout.OfShort layout, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        if (addr.byteSize() == 0)
        {
            var seg = MemorySegment.ofAddress(addr.address()).reinterpret((long)Short.BYTES * count);
            return seg.toArray(ValueLayout.JAVA_SHORT);
        }

        return addr.asSlice(0, (long) count * Long.BYTES).toArray(layout);
    }

    /* Byte ///////////////////////////////////////////////////////////////// */

    public static MemorySegment toMS(SegmentAllocator scope, byte[] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        return isReadBackOnly ? scope.allocate(arr.length) : scope.allocateFrom(ValueLayout.JAVA_BYTE, arr);
    }

    public static MemorySegment toPtrPTrMS(SegmentAllocator scope, byte[][] arr) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * Long.BYTES);
        int n = 0;
        for (byte[] a : arr)
            segment.setAtIndex(ValueLayout.ADDRESS, n++, scope.allocateFrom(ValueLayout.JAVA_BYTE, a));
        return segment;
    }

    public static MemorySegment toMS(SegmentAllocator scope, byte[][] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * arr[0].length * Byte.BYTES);
        int n = 0;
        for (byte[] row : arr) {
            segment.asSlice(n, row.length * Byte.BYTES).copyFrom(MemorySegment.ofArray(row));
            n += row.length * Byte.BYTES;
        }

        return segment;
    }

    public static void toArr(byte[] arr, MemorySegment segment) {
        if (arr == null)
            return;
        MemorySegment.copy(segment, ValueLayout.JAVA_BYTE, 0, arr, 0, arr.length);
    }

    public static byte[] toArr(ValueLayout.OfByte layout, MemorySegment seg, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        return slice(seg, addr, count * layout.byteSize()).toArray(layout);
    }

    public static byte[] toArr(ValueLayout.OfByte layout, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        if (addr.byteSize() == 0)
        {
            var seg = MemorySegment.ofAddress(addr.address()).reinterpret(count);
            return seg.toArray(ValueLayout.JAVA_BYTE);
        }

        return addr.asSlice(0, count).toArray(layout);
    }

    /* Char ///////////////////////////////////////////////////////////////// */

    public static MemorySegment toMS(SegmentAllocator scope, char[] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        return isReadBackOnly ? scope.allocate(Character.BYTES * arr.length) : scope.allocateFrom(ValueLayout.JAVA_CHAR, arr);
    }

    public static MemorySegment toPtrPTrMS(SegmentAllocator scope, char[][] arr) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * Long.BYTES);
        int n = 0;
        for (char[] a : arr)
            segment.setAtIndex(ValueLayout.ADDRESS, n++, scope.allocateFrom(ValueLayout.JAVA_CHAR, a));
        return segment;
    }

    public static MemorySegment toMS(SegmentAllocator scope, char[][] arr, boolean isReadBackOnly) {
        if (arr == null)
            return null;

        MemorySegment segment = scope.allocate((long) arr.length * arr[0].length * Byte.BYTES);
        int n = 0;
        for (char[] row : arr) {
            segment.asSlice(n, (int)(row.length * Character.BYTES)).copyFrom(MemorySegment.ofArray(row));
            n += row.length * Byte.BYTES;
        }

        return segment;
    }

    public static void toArr(char[] arr, MemorySegment segment) {
        if (arr == null)
            return;
        MemorySegment.copy(segment, ValueLayout.JAVA_CHAR, 0, arr, 0, arr.length);
    }

    public static char[] toArr(ValueLayout.OfChar layout, MemorySegment seg, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        return slice(seg, addr, count * layout.byteSize()).toArray(layout);
    }

    public static char[] toArr(ValueLayout.OfChar layout, MemorySegment addr, int count) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        if (addr.byteSize() == 0)
        {
            var seg = MemorySegment.ofAddress(addr.address()).reinterpret(count);
            return seg.toArray(ValueLayout.JAVA_CHAR);
        }

        return addr.asSlice(0, count).toArray(layout);
    }

    /*///////////////////////////////////////////////////////////////// */

    public static MemorySegment slice(MemorySegment scope, MemorySegment addr, long bytes) {
        if (addr.byteSize() == 0)
            return MemorySegment.ofAddress(addr.address()).reinterpret(bytes).asSlice(0, bytes);

        return MemorySegment.ofAddress(addr.address()).asSlice(0, bytes);
    }

    public static String readString(MemorySegment addr) {
        if (MemorySegment.NULL.equals(addr))
            return null;

        if (addr.byteSize() == 0)
        {
            // This is slightly horrible. I can't find a better way. Use C's strlen to figure out
            //how big the memory segment really is.
            return addr.reinterpret(strLen(addr)+1).getString(0);
        }

        return addr.getString(0);
    }

    public static MemorySegment resize(MemorySegment addr, long bytes)
    {
        if (addr.byteSize() == 0)
            addr = addr.reinterpret(bytes);
        return addr;
    }

    static MethodHandle strlen = null;

    private static long strLen(MemorySegment seg)
    {
        if (strlen == null)
        {
            Linker linker = Linker.nativeLinker();
            SymbolLookup stdlib = linker.defaultLookup();
            strlen = linker.downcallHandle(
                    stdlib.find("strlen").get(),
                    FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
            );

        }
        try {
            return (long)strlen.invokeExact(seg);
        } catch (Throwable e) {
            return 0;
        }
    }

    public static MemorySegment toCString(String[] s, Arena scope) {
        var segment = scope.allocate(ValueLayout.JAVA_LONG.byteSize() * s.length);
        for (int i = 0; i < s.length; ++i)
        {
            segment.setAtIndex(ValueLayout.ADDRESS, i, toCString(s[i], scope));
        }
        return segment;
    }

    public static void fromCString(MemorySegment mem, String[] s)
    {
        for (int n = 0; n < s.length; ++n)
            s[n] = readString(mem.getAtIndex(ValueLayout.ADDRESS, n));
    }

    public static MemorySegment toCString(String s, Arena scope) {
        return scope.allocateFrom(s);
    }

    public static MemorySegment toCString(String s, SegmentAllocator scope) {
        return scope.allocateFrom(s);
    }

    /**
     * Given a folder or file this will recursively delete it.
     *
     * @param folder The root folder to recursively delete everything under.
     * @return True if everything was deleted.
     */
    public static boolean deleteFolder(Path folder) {
        if (!Files.exists(folder))
            return true;

        try {
            Files.walkFileTree(folder, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null)
                        throw exc;

                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Path getBuildFolder() {
        if (System.getProperty("jpassport.build.home") != null)
            return Path.of(System.getProperty("jpassport.build.home"));
        return Path.of(System.getProperty("java.io.tmpdir"), "jpassport");
    }

    public enum Platform {Windows, Mac, Linux, Unknown}

    public static Platform getPlatform() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win"))
            return Platform.Windows;
        if (os.contains("mac"))
            return Platform.Mac;
        if ((os.contains("nix") || os.contains("nux") || os.contains("aix")))
            return Platform.Linux;

        return Platform.Unknown;
    }

    private static long typeToSize(Class<?> type)
    {

        if (type.equals(byte.class)) return ValueLayout.JAVA_CHAR.byteSize();
        if (type.equals(short.class)) return ValueLayout.JAVA_SHORT.byteSize();
        if (type.equals(int.class)) return ValueLayout.JAVA_INT.byteSize();
        if (type.equals(long.class)) return ValueLayout.JAVA_LONG.byteSize();
        if (type.equals(float.class)) return ValueLayout.JAVA_FLOAT.byteSize();
        if (type.equals(double.class)) return ValueLayout.JAVA_DOUBLE.byteSize();
        if (type.isRecord()) return size_of(type);
        throw new IllegalArgumentException("Cannot get size for non-primative");
    };
    public static long size_of(Class<?> c)
    {
        if (!c.isRecord())
            throw new IllegalArgumentException("Can only get size of records, not " + c.getName());

        int size = 0;
        for (Field f : c.getDeclaredFields())
        {
            size += PassportWriter.getPaddingBytes(f);

            Class<?> type = f.getType();
            if (type.isPrimitive())
                size += typeToSize(type);
            else if (type.isRecord())
            {
                boolean isPtr = f.getAnnotationsByType(Ptr.class).length > 0;
                if (isPtr)
                    size += ValueLayout.ADDRESS.byteSize();
                else
                    size += size_of(type);
            }
            else if (String.class.equals(type))
                size += ValueLayout.ADDRESS.byteSize();
            else if (type.isArray())
            {
                Annotation[] arrays = f.getAnnotationsByType(Array.class);
                boolean isPointer = f.getAnnotationsByType(Ptr.class).length > 0;

                if (arrays.length > 0)
                {
                    int length = ((Array) arrays[0]).length();
                    size += length * typeToSize(type.getComponentType());
                }
                else if (isPointer)
                    size += ValueLayout.ADDRESS.byteSize();
            }
        }
        return size;
    }

    /**
     * Called by generated code to build the memory layout for a struct. This automatically
     * tries to figure out where padding is needed in the struct in order to get proper byte
     * alignment.
     * @param layout The members of the struct
     * @return The full GroupLayout of the struct.
     */
    public static GroupLayout makeStruct(MemoryLayout ... layout)
    {
        int byteBarrier = System.getProperty("sun.arch.data.model").contains("64") ? 8 : 4;
        ArrayList<MemoryLayout> memLayout = new ArrayList<>();
        memLayout.add(layout[0]);
        long nextBarrier = byteBarrier;

        var curSize = memLayout.stream().mapToLong(MemoryLayout::byteSize).sum();
        while (nextBarrier <= curSize) nextBarrier += byteBarrier;

        for (int n = 1; n < layout.length; ++n)
        {
            curSize = memLayout.stream().mapToLong(MemoryLayout::byteSize).sum();
            long nextItemSize = layout[n].byteSize();

            //if an array is next, then we only need to byte align the first element of the array
            if (layout[n] instanceof SequenceLayout seq)
                nextItemSize = seq.byteSize() / seq.elementCount();

            // If the next piece of memory we are adding crosses the byte alignement barrier
            // then we need to pad the struct to alow byte alignment
            if (curSize + nextItemSize > nextBarrier)
                memLayout.add(MemoryLayout.paddingLayout(nextBarrier - curSize));
            memLayout.add(layout[n]);

            curSize = memLayout.stream().mapToLong(MemoryLayout::byteSize).sum();
            while (nextBarrier <= curSize) nextBarrier += byteBarrier;
        }


        return MemoryLayout.structLayout(memLayout.toArray(new MemoryLayout[0]));
    }
}
