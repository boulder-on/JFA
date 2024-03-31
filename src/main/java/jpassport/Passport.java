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
package jpassport;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.util.HashMap;

/**
 * An interface needs to extend this interface in order to link to the foreign library.
 */
public interface Passport {
    HashMap<String, MethodHandle> m_methods = new HashMap<>();
    HashMap<String, MemorySegment> m_loadedNames = new HashMap<>();

    /**
     * Lets you know if a specific method was found or not. Generally, all methods must be found
     * when loading the library. However, if some methods use the <code>@NotRequired</code> annotation
     * then those methods may not be present at runtime.
     *
     * @param name The exact name of a method in your interface.
     * @return True if the native link was made, false if it was not. If this returns false
     * then a call to the interface method in question is going to throw a java.lang.Error.
     */
    default boolean hasMethod(String name)
    {
        return m_methods.containsKey(name);
    }

    default boolean hasName(String name)
    {
        return m_loadedNames.containsKey(name);
    }

    default Object readStruct(MemorySegment segment, Object rec)
    {
        throw new RuntimeException("Not implemented");
    }
}
