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
package jpassport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is for an array that will be changed in the foreign library and
 * therefore should be read back in after the library call.
 *
 * This annotation is observed for array arguments and the class as a whole. When
 * used on a class it means that ALL arrays in interface methods are RefArgs.
 *
 * If the read_back_only parameter is false then the value in the java array is copied
 * into the native memory that is passed to the function call. If read_back_only is true
 * then blank memory is allocated and passed to the function call. For large
 * blocks of memory where you will only ever receive data back (eg. a read call)
 * read_back_only = true can save quite a bit of time.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE})
public @interface RefArg {
    boolean read_back_only() default false;
}
