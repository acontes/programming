package functionalTests.component.sca.components;

import functionalTests.component.conform.components.C;


public class IntentControllerTestComp extends C implements IntentTestInterface {

    public void m() {
        System.err.println("in method m without param");
    }

    public int n() {
        System.err.println("in method n without param");
        return 0;
    }

}
