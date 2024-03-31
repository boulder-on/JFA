package jpassport.test.structs;

import jpassport.annotations.Ptr;

/**
 * This record is meant to match the ComplexPassing struct in the C code.
 * This is considered complex in that it contains references to other records.
 */
public record ComplexStruct(
        int ID,
        TestStruct ts,
        @Ptr TestStruct tsPtr,
        String string)
{
}
