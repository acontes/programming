package functionalTests.component.collectiveitf.reduction.composite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public class NonReductionImpl implements NonReduction {

    public IntWrapper doIt() {
        return new IntWrapper(123);
    }

    public IntWrapper doItInt(IntWrapper val) {
        System.out.println(" Server received " + val.intValue());
        return new IntWrapper(123);
    }

    public void voidDoIt() {
        System.out.println(" Server received call on voidDoIt");
    }

}
