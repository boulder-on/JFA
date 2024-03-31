package jpassport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used to annotate a member of a Record that is also a Record and should be
 * treated as a pointer in the struct. Ex
 *
 *  struct MyStruct1
 *  {
 *      ......
 *  }
 *
 *  struct MyStruct2
 *  {
 *      struct MyStruct1* ptrToStruct;
 *      struct MyStruct1 regStruct;
 *  }
 *
 *  public record MyStruct1( .... ){};
 *  public record MyStruct2(@Ptr MyStruct1 ptrToStruct, MyStruct1 regStruct) {
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
public @interface Ptr {
}
