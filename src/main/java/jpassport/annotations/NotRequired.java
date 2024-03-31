package jpassport.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is for methods that may or may not be available at run-time.
 * For instance, if you want to use multiple library versions that do not contain
 * the same foreign functions you can use this annotation. All Passport interfaces contain
 * the method hasMethod(String) where you can ask if the native method was found.
 * This can be used like old school C #IFDEF's to use or avoid certain calls.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface NotRequired {
}
