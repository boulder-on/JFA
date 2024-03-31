package jpassport.test.structs;

import jpassport.annotations.StructPadding;

public record TestStruct(
        @StructPadding(bytes = 4) int s_int,
        long s_long,
        @StructPadding(bytes = 4) float s_float,
        double s_double) {
}
