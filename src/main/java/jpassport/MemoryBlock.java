package jpassport;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * A very common idiom in C is to pass a pointer to some allocated memory
 * and the function fills the memory. This class is used to handle this
 * for strings specifically. You can pass char[] if you want, but the
 * C function then needs to handle 2 byte chars.
 * NOTE: this class is always assumed to be a RefArg, i.e. it is always
 * read back after the native call.
 */
public class MemoryBlock {
    final private long sizeInBytes;
    private MemorySegment ptr = null;
    private String readBack = null;
    private byte[] buffer = null;

    public MemoryBlock(long bytes)
    {
        sizeInBytes = bytes;
    }

    public long size()
    {
        return sizeInBytes;
    }

    public MemorySegment toPtr(Arena scope)
    {
        if (ptr == null)
            ptr = scope.allocate(sizeInBytes);
        return ptr;
    }

    public void readBack()
    {
        if (ptr != null)
        {
            String[] args = new String[1];
            args[0] = Utils.readString(ptr);
            readBack = args[0];

            var bb = ptr.asByteBuffer();
            buffer = new byte[bb.limit()];
            bb.get(buffer, 0, buffer.length);
        }
    }

    public byte[] getBytes()
    {
        return buffer;
    }

    public void setString(String testing_only)
    {
        readBack = testing_only;
    }

    public String toString()
    {
        if (readBack != null)
            return readBack;
        return "No memory allocated";
    }
}
