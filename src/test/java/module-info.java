module test.passport {
    requires jpassport;
    requires com.sun.jna;
    requires com.sun.jna.platform;

    requires org.junit.jupiter.api;
    requires org.junit.platform.engine;
    requires jmh.core;
    requires jmh.generator.annprocess;

    requires commons.csv;

    exports jpassport.test;
    exports jpassport.test.performance;
    exports jpassport.test.structs;
    exports jpassport.test.callback;
}