package jpassport;


import java.lang.foreign.MemorySegment;

/**
 * This class should be extended if you have a method that works with an opaque pointer
 * class. You could just declare that your interface returns a MemoryAddress, but this
 * allows you to give a type to the address, which aids in readability.
 * ex.
 * <pre>
 * C
 * GATEWAY* openGateway();
 * void closeGateway(GATEWAY* g);
 *
 * Java
 * class Gateway extends GenericPointer
 * {}
 *
 * interface MyGateway
 * {
 *     Gateway openGateway();
 *     void closeGateway(Gateway g);
 * }
 * </pre>
 */
public class GenericPointer {
    protected MemorySegment ptr;

    public GenericPointer(MemorySegment addr)
    {
        ptr = addr;
    }

    public MemorySegment getPtr()
    {
        return ptr;
    }

    public boolean isNull()
    {
        return ptr.equals(MemorySegment.NULL);
    }

    /**
     * A convenience method for a NULL value.
     */
    public static GenericPointer NULL() {
        return new GenericPointer(MemorySegment.NULL);
    }
}
