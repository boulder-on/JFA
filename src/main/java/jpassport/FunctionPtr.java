package jpassport;

import java.lang.foreign.MemorySegment;

/**
 * If you make a callback function, this will be the class returned. Since this
 * derives from GenericPointer, you can pass it to a method.
 */
public class FunctionPtr extends GenericPointer {
    FunctionPtr(MemorySegment ptr)
    {
        super(ptr);
    }
}
