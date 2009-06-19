package functionalTests.structuredp2p;

import java.io.Serializable;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;


public class TestBodyTerminate implements RunActive, Serializable {

    public TestBodyTerminate() {

    }

    public void runActivity(Body body) {
        System.out.println("Dodo de 2 secondes.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Reprise du boulot, avec un terminate tout proche.");

        PAActiveObject.getBodyOnThis().terminate();

        while (true) {
            System.out.println("IAM ALIVE !!!!!!! PROACTIVE POWAAAA");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] argv) {
        try {
            TestBodyTerminate ao = (TestBodyTerminate) PAActiveObject.newActive(TestBodyTerminate.class.getName(), null);
        } catch (ActiveObjectCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
