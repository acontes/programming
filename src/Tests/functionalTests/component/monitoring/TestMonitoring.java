package functionalTests.component.monitoring;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
public class TestMonitoring extends ComponentTest {
    private Factory factory;
    private Component root;
    private MonitorController monitor;

    @org.junit.Test
    public void testMonitoringPrimitiveComponent() throws Exception {
        factory = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map<Object, Object> context = new HashMap<Object, Object>();
        root = (Component) factory.newComponent("functionalTests.component.monitoring.adl.TestPrimitive",
                context);

        Component[] subComponents = Fractal.getContentController(root).getFcSubComponents();
        for (int i = 0; i < subComponents.length; i++) {
            if (((NameController) subComponents[i].getFcInterface(Constants.NAME_CONTROLLER)).getFcName()
                    .equals("server"))
                monitor = (MonitorController) subComponents[i].getFcInterface(Constants.MONITOR_CONTROLLER);
        }

        Fractal.getLifeCycleController(root).startFc();
        start();
    }

    @org.junit.Test
    @Ignore
    public void testMonitoringCompositeComponent() throws Exception {
        factory = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map<Object, Object> context = new HashMap<Object, Object>();
        root = (Component) factory.newComponent("functionalTests.component.monitoring.adl.TestComposite",
                context);

        Component[] subComponents = Fractal.getContentController(root).getFcSubComponents();
        for (int i = 0; i < subComponents.length; i++) {
            if (((NameController) subComponents[i].getFcInterface(Constants.NAME_CONTROLLER)).getFcName()
                    .equals("servercomposite"))
                monitor = (MonitorController) subComponents[i].getFcInterface(Constants.MONITOR_CONTROLLER);
        }

        Fractal.getLifeCycleController(root).startFc();
        start();
    }

    private void printStats() {
        Iterator<MethodStatistics> stats = monitor.getAllStatistics().values().iterator();
        while (stats.hasNext())
            System.out.println(stats.next().toString());
    }

    private boolean checkTime(double supposedTime, double realTime) {
        return ((supposedTime * 0.70) <= realTime);
    }

    private void checkMethodStatistics(String itfName, String methodName, int nbCalls, int nbMethods,
            long sleepTimeCallMethod) {
        MethodStatistics methodStats = monitor.getStatistics(itfName, methodName);
        assertTrue(checkTime(ServerImpl.EXECUTION_TIME, methodStats.getAverageServiceTime()));
        assertTrue(checkTime(nbMethods * sleepTimeCallMethod, methodStats.getAverageInterArrivalTime()));
    }

    public void start() throws Exception {
        Runner runner1 = ((Runner) root.getFcInterface("runner1"));
        Runner runner2 = ((Runner) root.getFcInterface("runner2"));
        monitor.startMonitoring();

        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("Before execution:");
        System.out.println();
        printStats();

        runner1.run();
        runner2.run();

        int totalNbMethodCalls = runner1.getTotalNbMethodCalls() + runner2.getTotalNbMethodCalls();

        Thread.sleep(ServerImpl.EXECUTION_TIME * totalNbMethodCalls / 2);

        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("During execution:");
        System.out.println();
        printStats();

        Thread.sleep(ServerImpl.EXECUTION_TIME * totalNbMethodCalls / 2);

        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("After execution:");
        System.out.println();
        printStats();

        String[] itfNamesForEachMethod = runner1.getItfNamesForEachMethod();
        String[] methodNames = runner1.getMethodNames();
        int[] nbCallsPerMethod = runner1.getNbCallsPerMethod();
        for (int i = 0; i < methodNames.length; i++) {
            checkMethodStatistics(itfNamesForEachMethod[i], methodNames[i], nbCallsPerMethod[i],
                    methodNames.length, runner1.getSleepTime());
        }
        itfNamesForEachMethod = runner2.getItfNamesForEachMethod();
        methodNames = runner2.getMethodNames();
        nbCallsPerMethod = runner2.getNbCallsPerMethod();
        for (int i = 0; i < methodNames.length; i++) {
            checkMethodStatistics(itfNamesForEachMethod[i], methodNames[i], nbCallsPerMethod[i],
                    methodNames.length, runner2.getSleepTime());
        }
    }
}
