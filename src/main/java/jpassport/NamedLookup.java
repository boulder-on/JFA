package jpassport;

import java.lang.foreign.MemorySegment;

/**
 * Variables of this type declared in your interface will be loaded by the SymbolLookup.
 *
 * <pre>
 *     public interface LoadNames extends Passport
 *     {
 *        NamedLookup loadedName = new NamedLookup("LoadName");
 *     }
 * </pre>
 *
 * The above code would load LoadName from the native library as a pointer.
 */
public class NamedLookup {
    final private String name;
    private MemorySegment addr;

    public NamedLookup(String name)
    {
        this.name = name;
    }

    protected void setAddress(MemorySegment addr)
    {
        this.addr = addr;
    }

    public MemorySegment addr()
    {
        return addr;
    }

    public String name()
    {
        return name;
    }

    public boolean equals(GenericPointer ptr)
    {
        return addr.equals(ptr.getPtr());
    }
}
