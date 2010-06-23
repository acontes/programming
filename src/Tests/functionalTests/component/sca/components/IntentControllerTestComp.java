package functionalTests.component.sca.components;

import functionalTests.component.conform.components.C;


public class IntentControllerTestComp extends C implements IntentTestInterface {

    @Override
    public void m() {
        System.err.println("in method m without param");
    }

    @Override
    public int n() {
        return 0;
    }

}
