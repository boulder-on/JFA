package jpassport;

import java.lang.annotation.Annotation;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static jpassport.PassportWriter.isGenericPtr;

public class PassportInvocationHandler  implements InvocationHandler {
    HashMap<String, MethodHandle> handles;
    boolean allArraysAreReadBack = false;

    PassportInvocationHandler( HashMap<String, MethodHandle> methods, Class interfaceClass)
    {
        handles = methods;
        allArraysAreReadBack = PassportWriter.isRefArg(interfaceClass.getAnnotations());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Class<?> retType = method.getReturnType();
        Class<?>[] parameters = method.getParameterTypes();
        var params = method.getParameters();
        var mh = handles.get(method.getName());
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        if (method.getName().equals("hasMethod"))
            return handles.containsKey(args[0].toString());
        if (mh == null)
            throw new Error("Method does not exist");

        var passedArena = Arrays.stream(args).filter(a -> a instanceof Arena).findFirst();

        try (var scope = Arena.ofConfined()) {
            Arena useScope = (Arena) passedArena.orElse(scope);
            var largs = new ArrayList();
            for (int i = 0; i < parameters.length; ++i)
            {
                if (Arena.class.equals(parameters[i]))
                    continue;
                largs.add(toArg(parameters[i], args[i], useScope, paramAnnotations[i], params[i]));
            }

            var ret = mh.invokeWithArguments(largs);

            for (int i = 0; i < largs.size(); ++i)
            {
                if (PassportWriter.isRefArg(paramAnnotations[i]) ||
                        MemoryBlock.class.equals(parameters[i]) ||
                        (parameters[i].isArray() && allArraysAreReadBack))
                    readBack(args[i], largs.get(i));
            }

            if (String.class.equals(retType))
                return Utils.readString((MemorySegment) ret);
            else if (isGenericPtr(retType))
            {
                var cons = retType.getConstructor(MemorySegment.class);
                return cons.newInstance((MemorySegment)ret);
            }
            return ret;
        }
    }


    Object toArg(Class<?> type, Object value, Arena arena, Annotation[] annotations, Parameter p)
    {
        if (type.isRecord() || (type.isArray() && type.getComponentType().isRecord()))
            throw new IllegalArgumentException("Record types not supported");

        if (type.isPrimitive())
            return value;

        if (PassportWriter.isPtrPtrArg(annotations))
        {
            return switch (value)
            {
                case null -> MemorySegment.NULL;
                case byte[][] i -> Utils.toPtrPTrMS(arena, i);
                case char[][] i -> Utils.toPtrPTrMS(arena, i);
                case short[][] i -> Utils.toPtrPTrMS(arena, i);
                case int[][] i -> Utils.toPtrPTrMS(arena, i);
                case long[][] i -> Utils.toPtrPTrMS(arena, i);
                case float[][] i -> Utils.toPtrPTrMS(arena, i);
                case double[][] i -> Utils.toPtrPTrMS(arena, i);

                default -> value;
            };
        }

        var readBack = PassportWriter.isRefArgReadBackOnly(p);
        return switch (value)
        {
            case null -> MemorySegment.NULL;
            case GenericPointer g -> g.getPtr();
            case GenericPointer[] g -> Utils.toMS(arena, g,readBack);
            case String i -> Utils.toCString(i, arena);
            case MemoryBlock i -> i.toPtr(arena);
            case String[] i -> Utils.toCString(i, arena);
            case byte[] i -> Utils.toMS(arena, i, readBack);
            case char[] i -> Utils.toMS(arena, i, readBack);
            case short[] i -> Utils.toMS(arena, i, readBack);
            case int[] i -> Utils.toMS(arena, i, readBack);
            case long[] i -> Utils.toMS(arena, i, readBack);
            case float[] i -> Utils.toMS(arena, i, readBack);
            case double[] i -> Utils.toMS(arena, i, readBack);
            case byte[][] i -> Utils.toMS(arena, i, readBack);
            case char[][] i -> Utils.toMS(arena, i, readBack);
            case short[][] i -> Utils.toMS(arena, i, readBack);
            case int[][] i -> Utils.toMS(arena, i, readBack);
            case long[][] i -> Utils.toMS(arena, i, readBack);
            case float[][] i -> Utils.toMS(arena, i, readBack);
            case double[][] i -> Utils.toMS(arena, i, readBack);

            default -> value;
        };
    }

    void readBack(Object value, Object called)
    {
        if (value == null)
            return;

        switch (value)
        {
//            case null -> GenericPointer.NULL();
            //case GenericPointer g -> new GenericPointer();

            case byte[] i -> Utils.toArr(i, (MemorySegment) called);
            case char[] i -> Utils.toArr(i, (MemorySegment) called);
            case short[] i -> Utils.toArr(i, (MemorySegment) called);
            case int[] i -> Utils.toArr(i, (MemorySegment) called);
            case long[] i -> Utils.toArr(i, (MemorySegment) called);
            case float[] i -> Utils.toArr(i, (MemorySegment) called);
            case double[] i -> Utils.toArr(i, (MemorySegment) called);

            case GenericPointer[] g -> Utils.toArr(g, (MemorySegment) called);
            case String[] g -> Utils.fromCString((MemorySegment) called, g);
            case MemoryBlock fs -> fs.readBack();
//            case byte[][] i -> Utils.toArr(i, (MemorySegment) called);
//            case short[][] i -> Utils.toMS(arena, i, false);
//            case int[][] i -> Utils.toMS(arena, i, false);
//            case float[][] i -> Utils.toMS(arena, i, false);
//            case double[][] i -> Utils.toMS(arena, i, false);

            default -> {
                break;
            }
        }
    }
}
