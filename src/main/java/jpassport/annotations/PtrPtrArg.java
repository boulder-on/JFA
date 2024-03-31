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
 * This annotation is for a 2-D array that should be passed as a pointer to a list of pointers. Without
 * this annotation a 2-D array will be copied as a single large memory block in row major order.
 *
 * double sumMatD(int rows, int cols, double mat[rows][cols]) <- C function
 * double sumMatD(int rows, int cols, double[][] mat);  <- Java interface
 *
 * double sumMatD(const int rows, const int cols, const double** mat) <- C function
 * double sumMatD(int rows, int cols, @PtrPtrArg double[][] mat);  <- Java interface
 *
 * This annotation is only observed for array arguments.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PtrPtrArg {
}
