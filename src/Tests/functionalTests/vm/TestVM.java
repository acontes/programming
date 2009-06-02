package functionalTests.vm;

import java.io.IOException;

import org.junit.Test;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import functionalTests.FunctionalTest;


public class TestVM extends FunctionalTest {

    @Test
    public void test() throws ProActiveException, IOException, InterruptedException {

        GCMVirtualNode workers;
        GCMApplication gcmad;

        gcmad = PAGCMDeployment.loadApplicationDescriptor(TestVM.class.getResource("gcma.xml"));
        gcmad.startDeployment();
        workers = gcmad.getVirtualNode("Workers");
        Node firstNode = workers.getANode();
        Node secondNode = firstNode;

        // create active objects
        AOCompute ao1 = (AOCompute) PAActiveObject.newActive(AOCompute.class.getName(), null, firstNode);
        AOCompute ao2 = (AOCompute) PAActiveObject.newActive(AOCompute.class.getName(), null, secondNode);
        AOCompute ao3 = (AOCompute) PAActiveObject.newActive(AOCompute.class.getName(), null, firstNode);
        AOCompute ao4 = (AOCompute) PAActiveObject.newActive(AOCompute.class.getName(), null, secondNode);

        ao1.setRemote(ao2);
        ao2.setRemote(ao3);
        ao3.setRemote(ao4);
        ao4.setRemote(ao1);

        System.out.println("Begining test");

        // Compute
        for (int i = 0; i < 4; i++) {
            AORandom rand = (AORandom) PAActiveObject.newActive(AORandom.class.getName(), null,
                    i % 2 == 0 ? firstNode : secondNode);
            ao1.setRandom(rand);
            ao2.setRandom(rand);
            ao3.setRandom(rand);
            ao4.setRandom(rand);
            System.out.println("Compute #" + i + " : " + ao1.compute(42, 1));
            PAActiveObject.terminateActiveObject(rand, false);
            Thread.sleep(1000);
        }

        System.out.println("Test done.");

        // Stop active objects
        PAActiveObject.terminateActiveObject(ao1, false);
        PAActiveObject.terminateActiveObject(ao2, false);
        PAActiveObject.terminateActiveObject(ao3, false);
        PAActiveObject.terminateActiveObject(ao4, false);

        gcmad.kill();

        PALifeCycle.exitSuccess();
    }
}
