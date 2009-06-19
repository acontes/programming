package functionalTests.structuredp2p.can;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;


public class TestTerminate {

    /**
     * @param args
     */
    public static void main(String[] args) {
        AwareObject ao1 = null;
        AwareObject ao2 = null;
        try {
            ao1 = (AwareObject) PAActiveObject.newActive(AwareObject.class.getName(), null);
            ao2 = (AwareObject) PAActiveObject.newActive(AwareObject.class.getName(), null);
            ao2.setPeer(ao1);
        } catch (ActiveObjectCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ao.callMe();
        System.out.println("--> Terminate here <--");
        ao.getBody().terminate();
    }
}
