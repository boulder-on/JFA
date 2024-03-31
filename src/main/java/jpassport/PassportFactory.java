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


import jpassport.annotations.NotRequired;
import jpassport.annotations.Critical;

import java.io.File;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

public class PassportFactory
{
    /**
     * Call this method to generate the library linkage. This version of the method will write the java file and compile
     * it. As a result, the start-up is a bit slower than {@link #proxy(String, Class) proxy()}, but the implementation
     * is a bit quicker.
     *
     * <p>This version also supports Record -> struct conversions.
     *
     * @param libraryName The library name (the file name of the shared library without extension on all platforms,
     *                    without lib prefix on Linux and Mac).
     * @param interfaceClass The class to wrap.
     * @param <T> Any interface that extends Passport
     * @return A class linked to call into a DLL or SO using the Foreign Linker.
     */
    public synchronized static <T extends Passport> T link(String libraryName, Class<T> interfaceClass) throws Throwable
    {
        if (!Passport.class.isAssignableFrom(interfaceClass)) {
            throw new IllegalArgumentException(
                    StringTemplate.STR."Interface ({interfaceClass.getSimpleName()}) of library={libraryName} does not extend {Passport.class.getSimpleName()}");
        } else {
            return buildClass(libraryName, interfaceClass);
        }
    }

    /**
     * Call this method to generate the library linkage. This version of the method uses a dynamic proxy to handle native
     * calls. As a result, the start-up is faster than {@link #link(String, Class) link()}, but the implementation is a bit slower.
     *
     * <p>This method does not support Record -> struct conversions.</p>
     *
     * @param libraryName The library name (the file name of the shared library without extension on all platforms,
     *                    without lib prefix on Linux and Mac).
     * @param interfaceClass The class to wrap.
     * @param <T> Any interface that extends Passport
     * @return A class linked to call into a DLL or SO using the Foreign Linker.
     */
    public static <T extends Passport> T proxy(String libraryName, Class<T> interfaceClass) throws Throwable
    {
        if (!Passport.class.isAssignableFrom(interfaceClass)) {
            throw new IllegalArgumentException("Interface (" + interfaceClass.getSimpleName() + ") of library=" + libraryName + " does not extend " + Passport.class.getSimpleName());
        }

        var methods = loadMethodHandles(libraryName, interfaceClass);
        var handler = new PassportInvocationHandler(methods, interfaceClass);
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[] { interfaceClass },
                handler);
    }

    private static <T extends Passport> T buildClass(String libName, Class<T> interfaceClass) throws Throwable
    {
        HashMap<String, MethodHandle> handles = loadMethodHandles(libName, interfaceClass);
        PassportWriter<T> classWriter = new PassportWriter<>(interfaceClass);

        return classWriter.build(handles);
    }

    /**
     * This method looks up the methods in the requested native library that match non-static
     * methods in the given interface class.
     *
     * @param libName Name of the native library to load.
     * @param interfaceClass The interface class to use as a reference for loading methods.
     * @return A map of Name to method handle pairs for the methods in the interface class.
     */
    public static HashMap<String, MethodHandle> loadMethodHandles(String libName, Class<? extends Passport> interfaceClass)
    {
        if (libName != null) {
            if (Utils.getPlatform().equals(Utils.Platform.Windows) && !libName.endsWith(".dll"))
                libName = libName + ".dll";

            File libPath = new File(libName);
            System.load(libPath.getAbsolutePath());
        }
        Linker cLinker = Linker.nativeLinker();

        List<Method> interfaceMethods = getDeclaredMethods(interfaceClass);
        HashMap<String, MethodHandle> methodMap = new HashMap<>();

        SymbolLookup lookup = SymbolLookup.loaderLookup();

        for (Method method : interfaceMethods) {
            Class<?> retType = method.getReturnType();
            Class<?>[] parameters = method.getParameterTypes();

            for (int n = 0; n < parameters.length; ++n) {
                if (!parameters[n].isPrimitive() && !Arena.class.equals(parameters[n]))
                    parameters[n] = MemorySegment.class;
            }

            MemoryLayout[] memoryLayout = Arrays.stream(parameters).filter(p -> !Arena.class.equals(p)).
                    map(PassportFactory::classToMemory).toArray(MemoryLayout[]::new);

            FunctionDescriptor fd;
            if (void.class.equals(retType))
                fd = FunctionDescriptor.ofVoid(memoryLayout);
            else
                fd = FunctionDescriptor.of(classToMemory(retType), memoryLayout);

            var addr = lookup.find(method.getName()).orElse(null);
            if (addr == null && method.getAnnotation(NotRequired.class) == null)
                throw new PassportException("Could not find method in library: " + method.getName());

            if (addr != null) {

                MethodHandle methodHandle;

                if (method.getAnnotation(Critical.class) == null)
                    methodHandle = cLinker.downcallHandle(addr, fd);
                else
                    methodHandle = cLinker.downcallHandle(addr, fd, Linker.Option.critical(false));

                methodMap.put(method.getName(), methodHandle);
            }
        }
        loadNames(interfaceClass);
        return methodMap;
    }

    /**
     * This methods looks up all of the methods in the requested native library that match non-static
     * methods in the given interface class.
     *
     * @param interfaceClass The interface class to use as a reference for loading methods.
     */
    private static void loadNames(Class<? extends Passport> interfaceClass)
    {
        List<Field> names = getDeclaredNames(interfaceClass);

        SymbolLookup lookup = SymbolLookup.loaderLookup();

        for (Field field : names) {
            try {
                var named = ((NamedLookup)field.get(interfaceClass));
                var addr = lookup.find(named.name());
                if (addr.isEmpty() && field.getAnnotation(NotRequired.class) == null)
                    throw new PassportException("Could not find field in library: " + named.name());
                addr.ifPresent(named::setAddress);
            } catch (IllegalAccessException e) {
                throw new PassportException("Could not find field in library: " + field.getName());
            }
        }
    }

    /**
     * Given an object and method name this will return a memory address that
     * corresponds to a method pointer that can be passed to native code.
     * The method cannot be static.
     *
     * @param ob The object that the method belongs to.
     * @param methodName The name of the method.
     * @return A pointer to a method handle that can be passed to native code.
     * @throws IllegalArgumentException if there is no method with the given name, or there is more
     * than 1 method with the given name.
     */
    public static FunctionPtr createCallback(Object ob, String methodName)
    {
        var methods = getDeclaredMethods(ob.getClass());

        methods = methods.stream().filter(m -> m.getName().equals(methodName)).collect(Collectors.toList());
        if (methods.isEmpty())
            throw new IllegalArgumentException("Could not find method " + methodName + " in class " + ob.getClass().getName());
        else if (methods.size() > 1)
            throw new IllegalArgumentException("Multiple overloads of method " + methodName + " in class " + ob.getClass().getName());

        Method callbackMethod = methods.get(0);

        Class<?> retType = callbackMethod.getReturnType();
        Class<?>[] parameters = callbackMethod.getParameterTypes();

        if (!retType.isPrimitive() && retType != MemorySegment.class)
            throw new IllegalArgumentException("Callback method must return void, primitives, or MemoryAddress, not " + retType.getName());


        for (Class<?> parameter : parameters) {
            if (!parameter.isPrimitive() && parameter != MemorySegment.class)
                throw new IllegalArgumentException("Callback parameters must be primitives or MemoryAddress, not " + parameter.getName());
        }

        MemoryLayout[] memoryLayout = Arrays.stream(parameters).map(PassportFactory::classToMemory).toArray(MemoryLayout[]::new);
        FunctionDescriptor fd;
        if (void.class.equals(retType))
            fd = FunctionDescriptor.ofVoid(memoryLayout);
        else
            fd = FunctionDescriptor.of(classToMemory(retType), memoryLayout);

        try {
            var handle = MethodHandles.publicLookup().findVirtual(ob.getClass(), methodName, MethodType.methodType(retType, parameters));
            var handleToCall = handle.bindTo(ob);

            var scope = Arena.ofAuto();
            return new FunctionPtr(Linker.nativeLinker().upcallStub(handleToCall, fd, scope));
        }
        catch (NoSuchMethodException | IllegalAccessException ex)
        {
            throw new Error("Failed to create callback method", ex);
        }
    }

    static List<Method> getDeclaredMethods(Class<?> interfaceClass) {
        Method[] methods = interfaceClass.getDeclaredMethods();
        return Arrays.stream(methods).
                filter(method -> !Modifier.isStatic(method.getModifiers())).
                filter(method -> !method.isDefault()).toList();
    }

    static List<Field> getDeclaredNames(Class<?> interfaceClass) {
        Field[] fields = interfaceClass.getDeclaredFields();
        return Arrays.stream(fields).
                filter(field -> field.getType().equals(NamedLookup.class)).toList();
    }


    private static MemoryLayout classToMemory(Class<?> type)
    {
        if (double.class.equals(type))
            return ValueLayout.JAVA_DOUBLE;
        if (int.class.equals(type))
            return ValueLayout.JAVA_INT;
        if (float.class.equals(type))
            return ValueLayout.JAVA_FLOAT;
        if (short.class.equals(type))
            return ValueLayout.JAVA_SHORT;
        if (byte.class.equals(type))
            return ValueLayout.JAVA_BYTE;
        if (long.class.equals(type))
            return ValueLayout.JAVA_LONG;
        if (boolean.class.equals(type))
            return ValueLayout.JAVA_BOOLEAN;

        return ValueLayout.ADDRESS;
    }

}
