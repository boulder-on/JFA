package jpassport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation enables the MethodHandle hint "critical". This reduces some of the
 * overhead that is required when calling a MethodHandle. From the definition of critical:
 *
 * A critical function is a function that has an extremely short running time in all cases (similar to calling an empty
 * function), and does not call back into Java (e.g. using an upcall stub). Using this linker option is a hint which
 * some implementations may use to apply optimizations that are only valid for critical functions. Using this linker
 * option when linking non-critical functions is likely to have adverse effects, such as loss of performance, or JVM crashes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Critical {
}
