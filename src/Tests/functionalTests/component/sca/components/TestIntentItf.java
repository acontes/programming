package functionalTests.component.sca.components;

import functionalTests.component.conform.components.I;


public interface TestIntentItf extends I {
    public static final String CLIENT_ITF_NAME = "client";
    public static final String SERVER_ITF_NAME = "server";

    public void m();

    public int n();
}
