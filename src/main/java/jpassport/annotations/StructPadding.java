package jpassport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This should be used to annotate the members of a Record class that are converted to a C Struct.
 * The idea is that the values in a struct are often padded. At least chars, and shorts are often
 * padded to 32 bits. The trouble is that the exact amount of padding is dependent on the compiler
 * and the platform. As such, it's nearly impossible to know ahead of time what the padding should
 * be on a given platform with a given compiler. As the developer, you need to tell JPassport how
 * much padding there should be. You can specify different padding for different platforms.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
public @interface StructPadding {

    int NO_VALUE = Integer.MIN_VALUE;

    /** The number of bytes of padding to add. */
    int bytes() default 0;
    int windowsBytes() default NO_VALUE;
    int macBytes() default NO_VALUE;
    int linuxBytes() default NO_VALUE;
}
