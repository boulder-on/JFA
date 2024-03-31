package jpassport.test.callback;

import jpassport.PassportFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static jpassport.test.TestLinkHelp.getLibName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCallback {

    @Test
    public void testCallback() throws Throwable {
        var callBack = PassportFactory.link(getLibName(), CallbackNative.class);

        var myCB = new CallbackObj();

        int ret = callBack.call_CB(myCB.getAsFunctionPtr(), 5, 1);
        assertEquals(5, myCB.calls);
        assertEquals(30, ret);

        int[] test = {1, 2, 3 ,4 ,5};
        callBack.call_CBArr(myCB.getAsFunctionArrPtr(), test, test.length);
        assertEquals(Arrays.stream(test).sum(), myCB.sum);
    }
}
