package functionalTests.component.sca.components;

import functionalTests.component.conform.components.C;
import functionalTests.component.conform.components.I;


public class TestIntentComponent extends C implements TestIntentItf {

    public void m() {
        System.err.println("in method m without param");
    }

    public int n() {
        System.err.println("in method n without param");
        return 0;
    }

}
