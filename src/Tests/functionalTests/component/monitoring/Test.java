package functionalTests.component.monitoring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.controller.MethodStatistics;
import org.objectweb.proactive.core.component.controller.MonitorController;

import functionalTests.ComponentTest;


/**
 * Test the monitor controller.
 *
 * @author The ProActive Team
 */
public class Test extends ComponentTest {
    private Factory factory;
    private Component root;
    private MonitorController monitor;
    
    @Before
    public void setUp() throws Exception {
        factory = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map<Object, Object> context = new HashMap<Object, Object>();
        root = (Component) factory.newComponent(
                "functionalTests.component.monitoring.adl.Composite", context);

        Component[] subComponents = Fractal.getContentController(root).getFcSubComponents();
        for (int i = 0; i < subComponents.length; i++) {
            if (((NameController) subComponents[i].getFcInterface(Constants.NAME_CONTROLLER)).getFcName().equals("server"))
                monitor = (MonitorController)  subComponents[i].getFcInterface(Constants.MONITOR_CONTROLLER);
        }

        Fractal.getLifeCycleController(root).startFc();
        monitor.registerMethods();
    }

    private void printStats() {
        Iterator<MethodStatistics> stats = monitor.getAllStatistics().values().iterator();
        while (stats.hasNext()) {
            System.out.println(stats.next().toString());
            System.out.println();
        }
    }
    // -----------------------------------------------------------------------------------
    // Full test
    // -----------------------------------------------------------------------------------
    @org.junit.Test
    public void testMonitoring() throws Exception {
        monitor.startMonitoring();
        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("Before execution:");
        System.out.println();
        printStats();
        ((Runner) root.getFcInterface("runner1")).run();
        ((Runner) root.getFcInterface("runner2")).run();
        System.out.println();
        System.out.println("-----------------------------------------------------------");
        Thread.sleep(1000);
        System.out.println("During execution:");
        printStats();
        Thread.sleep(20000);
        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("After execution:");
        printStats();
    }
}
