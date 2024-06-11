package jpassport;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * If you make a callback function, this will be the class returned. Since this
 * derives from GenericPointer, you can pass it to a method.
 */
public class FunctionPtr extends GenericPointer {

    private final Arena arena; //The arena that was used to allocate the function pointer

    /**
     *
     * @param arena The arena used to allocate the function pointer
     * @param ptr The function pointer
     */
    FunctionPtr(Arena arena, MemorySegment ptr)
    {
        super(ptr);
        this.arena = arena;
    }
}
