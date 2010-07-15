package functionalTests.component.sca.control;

import org.objectweb.proactive.core.ProActiveRuntimeException;


public class CoolParent {
    public CoolParent() {
        throw new ProActiveRuntimeException("");
    }

    public void foo() {
        System.err.println("jsuis dans CoolParent!::foo");
    }

    public int bar() {
        System.err.println("jsuis dans CoolParent!::bar");
        return 0;
    }

    protected void cool() {
        System.err.println("cool de parent");
    }

    public void test() {
        cool();
    }

}
