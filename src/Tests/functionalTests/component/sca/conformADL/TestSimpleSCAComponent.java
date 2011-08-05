/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package functionalTests.component.sca.conformADL;

import java.util.HashMap;
import java.util.Map;

import org.etsi.uri.gcm.util.GCM;
import org.junit.Assert;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;

import functionalTests.ComponentTest;
import functionalTests.component.conformADL.components.Action;
import functionalTests.component.sca.SCAComponentTest;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.extensions.sca.SCAPAPropertyRepository;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.sca.control.SCAPropertyController;


/**
 *
 * @author mug
 */
public class TestSimpleSCAComponent extends SCAComponentTest {

    Component dummy;

    public TestSimpleSCAComponent() {
        super("Configuration with ADL arguments and AttributeController",
                "Configuration with ADL arguments and AttributeController");
    }

    /*
     * (non-Javadoc)
     * 
     * @see testsuite.test.FunctionalTest#action()
     */
    @org.junit.Test
    @SuppressWarnings("unchecked")
    public void action() throws Exception {
        Factory f = org.objectweb.proactive.extensions.sca.adl.FactoryFactory.getFactory();
        Map context = new HashMap();
        context.put("message", "hello world");
        dummy = (Component) f.newComponent("functionalTests.component.sca.conformADL.helloworld-property",
                context);
        GCM.getGCMLifeCycleController(dummy).startFc();
        SCAPropertyController scapcClient = org.objectweb.proactive.extensions.sca.Utils
                .getSCAPropertyController(dummy);
        scapcClient.setValue("info", "testtest");
        System.err.println("the result : " + ((Action) dummy.getFcInterface("action")).doSomething());

        //        Assert.assertEquals("This component is storing the info : hello world", ((Action) dummy
        //                .getFcInterface("action")).doSomething());
    }

}
