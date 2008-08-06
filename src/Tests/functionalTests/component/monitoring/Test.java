package functionalTests.component.monitoring;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.util.Fractal;

import functionalTests.ComponentTest;


/**
 * Test the monitor controller.
 *
 * @author The ProActive Team
 */
public class Test extends ComponentTest {
    @Before
    public void setUp() throws Exception {
        Factory factory = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();

        // ----------------------------------------------------------
        // Load the ADL definition
        // ----------------------------------------------------------
        Map<Object, Object> context = new HashMap<Object, Object>();
        Component root = (Component) factory.newComponent(
                "functionalTests.component.monitoring.adl.Composite", context);

        // ----------------------------------------------------------
        // Start the Root component
        // ----------------------------------------------------------
        Fractal.getLifeCycleController(root).startFc();
    }

    // -----------------------------------------------------------------------------------
    // Full test
    // -----------------------------------------------------------------------------------
    @org.junit.Test
    public void testMonitoring() throws Exception {

    }
}
