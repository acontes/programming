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

import functionalTests.component.sca.SCAComponentTest;
import functionalTests.component.sca.conformADL.components.Action;
import functionalTests.component.sca.control.components.IntentHandlerTest;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;

/**
 *
 * @author mug
 */
public class TestSimpleSCAComponentWithIntent extends SCAComponentTest {

    Component dummy;

    public TestSimpleSCAComponentWithIntent() {
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
        dummy = (Component) f.newComponent("functionalTests.component.sca.conformADL.components.availability-test", context);
        GCM.getGCMLifeCycleController(dummy).startFc();
        //System.err.println("the result : " + ((Action) dummy.getFcInterface("Service")).doSomething());
        Assert.assertEquals("This component is storing the info : hello world", ((Action) dummy.getFcInterface("Service")).doSomething());
    }
}
