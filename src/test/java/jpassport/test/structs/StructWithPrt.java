package jpassport.test.structs;

import jpassport.annotations.StructPadding;

import java.lang.foreign.MemorySegment;

public record StructWithPrt(@StructPadding(bytes = 4)int n, MemorySegment addr, float f) {
}
