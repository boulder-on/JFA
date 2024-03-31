package jpassport;

import java.lang.foreign.MemorySegment;

public class Pointer extends GenericPointer{
    public Pointer(MemorySegment addr) {
        super(addr);
    }

    public Pointer() {
        super(MemorySegment.NULL);
    }
}
