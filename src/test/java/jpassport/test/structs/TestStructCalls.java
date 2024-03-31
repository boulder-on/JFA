package jpassport.test.structs;

import jpassport.Passport;
import jpassport.annotations.NotRequired;
import jpassport.annotations.RefArg;

public interface TestStructCalls extends Passport {
    double passStruct(TestStruct address);
    double passComplex(@RefArg ComplexStruct[] complexStruct);
    double passStructWithArrays(@RefArg PassingArrays[] arrays);
    @NotRequired
    void testAddrCall(StructWithPrt test);
}
