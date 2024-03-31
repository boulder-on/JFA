package jpassport.test.performance;

import com.sun.jna.Native;
import jpassport.PassportFactory;
import jpassport.test.PureJava;
import jpassport.test.TestLink;
import jpassport.test.TestLinkJNADirect;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class JPassportMicroBenchmark
{
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(JPassportMicroBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    static TestLink testFL;
    static TestLink testJNA;
    static TestLink testJNADirect;
    static TestLink testJava;

    @Param({"1024", "2048", "16384", "262144"})
    public int array_size;

    public double[] test_arr;

    @Setup(Level.Trial)
    public void updateArray()
    {
        test_arr = IntStream.range(0, array_size).mapToDouble(i -> i).toArray();

    }


    @Setup()
    public void setUp() throws Throwable
    {
        System.setProperty("jna.library.path", System.getProperty("java.library.path"));
        testFL = PassportFactory.link("libforeign_link", TestLink.class);
        testJNA =  Native.load("libforeign_link.dll", TestLink.class);
        testJNADirect =  new TestLinkJNADirect.JNADirect();
        testJava = new PureJava();
    }


    @Benchmark
    @Fork(value = 2, warmups = 1)
    public void sumTestArrDJava()
    {
        testJava.sumArrD(test_arr, test_arr.length);
    }

    @Benchmark
    @Fork(value = 2, warmups = 1)
    public void sumTestArrDJNA()
    {
        testJNA.sumArrD(test_arr, test_arr.length);
    }

    @Benchmark
    @Fork(value = 2, warmups = 1)
    public void sumTestArrDJNADirect()
    {
        testJNADirect.sumArrD(test_arr, test_arr.length);
    }

    @Benchmark
    @Fork(value = 2, warmups = 1)
    public void sumTestArrDJPassport()
    {
        testFL.sumArrD(test_arr, test_arr.length);
    }
}
