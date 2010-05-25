package functionalTests.component.sca.components;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


public class C2 implements C2Attribute {
    BooleanWrapper x1;

    public C2() {
        // TODO Auto-generated constructor stub
    }

    public C2(BooleanWrapper a) {
        this.x1 = a;
    }

    public BooleanWrapper getX1() {
        // TODO Auto-generated method stub
        return x1;
    }

    public void setX1(BooleanWrapper x) {
        // TODO Auto-generated method stub
        this.x1 = x;
    }
}
