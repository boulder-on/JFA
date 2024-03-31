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
package jpassport.test.performance;

import com.sun.jna.Library;
import jpassport.Passport;
import jpassport.annotations.Critical;

public interface PerfTest extends Passport, Library {
    @Critical
    double sumD(double d, double d2);
    @Critical
    double sumArrD(double[] d, int len);
    float sumArrF(float[] d, int len);
    int sumArrI(int[] d, int len);
}
