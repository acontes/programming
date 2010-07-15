package functionalTests.component.sca.control;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extensions.component.sca.control.IntentJoinPoint;


public class simpleTest {
    CoolParent par;
    CoolChild chi;

    @Before
    public void setUp() throws Exception {
        par = new CoolParent();
        chi = new CoolChild();
    }

    @Test
    public void test() throws Exception {
        chi.foo();
        chi.bar();
        chi.test();
        par.test();
    }
}
