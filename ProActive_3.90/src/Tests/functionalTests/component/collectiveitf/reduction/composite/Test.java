package functionalTests.component.collectiveitf.reduction.composite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;

import functionalTests.ComponentTest;


public class Test extends ComponentTest {

    public Test() {
        super("Multicast reduction mixing composite and primitive components",
                "Multicast reduction mixing composite and primitive components");
    }

    /*
     * @see testsuite.test.FunctionalTest#action()
     */
    @org.junit.Test
    public void action() throws Exception {
        try {
            Component boot = Fractal.getBootstrapComponent();
            TypeFactory type_factory = Fractal.getTypeFactory(boot);
            GenericFactory cf = Fractal.getGenericFactory(boot);

            Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();

            Map<String, Object> context = new HashMap<String, Object>();

            Component root = null;
            root = (Component) f
                    .newComponent(
                            "functionalTests.component.collectiveitf.reduction.composite.adl.CompPrimReduce",
                            context);
            if (root == null) {
                System.err.println("Component CompPrimReduce creation failed!");
                return;
            }

            Fractal.getLifeCycleController(root).startFc();
            Reduction reductionItf = ((Reduction) root.getFcInterface("mcast"));

            /*throws exception here ... please ask me (Elton) why*/
            IntWrapper rval = reductionItf.doIt();
            Assert.assertEquals(new IntWrapper(123), rval);

            rval = reductionItf.doItInt(new IntWrapper(321));
            Assert.assertEquals(new IntWrapper(123), rval);

            reductionItf.voidDoIt();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
