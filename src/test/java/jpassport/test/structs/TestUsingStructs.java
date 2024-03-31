package jpassport.test.structs;

import jpassport.PassportFactory;
import jpassport.Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static java.lang.foreign.ValueLayout.*;
import static jpassport.test.TestLinkHelp.getLibName;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUsingStructs {

    static TestStructCalls PassingStructs;

    @BeforeAll
    public static void startup() throws Throwable
    {
        System.setProperty("jpassport.build.home", "out/testing");
        PassingStructs = PassportFactory.link(getLibName(), TestStructCalls.class);
//        PassingStructs = new TestStructCalls_impl(PassportFactory.loadMethodHandles("libpassport_test.dll", TestStructCalls.class));
    }

    @Test
    public void testSimpleStruct()
    {
        assertEquals(4 * JAVA_LONG.byteSize(), Utils.size_of(TestStruct.class));
        assertEquals(JAVA_INT.byteSize() + Utils.size_of(TestStruct.class) +
                ADDRESS.byteSize() * 2, Utils.size_of(ComplexStruct.class));


        assertEquals(2+3+4+5, PassingStructs.passStruct(new TestStruct(2, 3, 4, 5)));
//        long[] times = new long[1000];
//
//        for (int n = 0; n < times.length; n++)
//        {
//            long t = System.currentTimeMillis();
//
//            for (int m = 0; m < 10000; ++m)
//            {
//                PassingStructs.passStruct(new TestStruct(m, m+1, m+2, m+3));
//            }
//
//            times[n] = System.currentTimeMillis() - t;
//            System.out.printf("%d = %d ms\n", n, times[n]);
//        }
//
//        times = Arrays.copyOfRange(times, 5, times.length);
//        System.out.println("Mean = " + Arrays.stream(times).average().getAsDouble());
    }

    @Test
    public void testComplexStruct()
    {
        TestStruct ts = new TestStruct(1, 2, 3, 4);
        TestStruct tsPtr = new TestStruct(5, 6, 7, 8);
        ComplexStruct[] complex = new ComplexStruct[] {new ComplexStruct(55, ts, tsPtr, "hello")};

        double d = PassingStructs.passComplex(complex);
        assertEquals(IntStream.range(1, 9).sum(), d);
        assertEquals(65, complex[0].ID());
        assertEquals(11, complex[0].ts().s_int());
        assertEquals(25, complex[0].tsPtr().s_int());
        assertEquals("HELLO", complex[0].string());
//
//        long[] times = new long[500];
//
//        for (int n = 0; n < times.length; n++)
//        {
//            long t = System.currentTimeMillis();
//
//            for (int m = 0; m < 10000; ++m)
//            {
//                complex = new ComplexStruct[] {new ComplexStruct(55, ts, tsPtr, "hello")};
//                PassingStructs.passComplex(complex);
//            }
//
//            times[n] = System.currentTimeMillis() - t;
//            System.out.printf("%d = %d ms\n", n, times[n]);
//        }
//
//        times = Arrays.copyOfRange(times, 5, times.length);
//        System.out.println("Mean = " + Arrays.stream(times).average().getAsDouble());
    }

    @Test
    public void testStructsWithArrays()
    {
        int expected = IntStream.range(1, 21).sum();

        var doublesArr = new double[] {1, 2, 3, 4, 5};
        var longArr = new long[] {6, 7, 8, 9, 10, 11, 12, 13};
        var doublePtr =  new double[] {14, 15, 16};
        var longPtr = new long[] {17,18,19,20};

        PassingArrays pa = new PassingArrays(doublesArr, longArr, doublePtr.length, doublePtr, longPtr.length, longPtr);
        PassingArrays[] regArg = new PassingArrays[] {pa};
        assertEquals(expected, PassingStructs.passStructWithArrays(regArg));

        assertArrayEquals(new double[] {14,15,16,4,5}, regArg[0].s_double());
        assertArrayEquals(new long[] {6,7,8,9}, regArg[0].s_longPtr());
        assertArrayEquals(longArr, regArg[0].s_long());
        assertArrayEquals(doublePtr, regArg[0].s_doublePtr());
    }
}
