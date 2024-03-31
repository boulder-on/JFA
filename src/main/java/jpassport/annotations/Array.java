package jpassport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used with record members to declare the size of an array. For example
 * <pre>
 * public record PassingArrays(
 *         <code>@Array(length = 5)</code> double[] s_double)
 *  </pre>
 *
 *  In the above example, if PassingArrays is annotated with RefArg to indicate that it should
 *  be read back after the native call, then the Array annotation indicates that s_double[]
 *  should always be read as 5 doubles.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
public @interface Array {
    int length() default 1;
}
