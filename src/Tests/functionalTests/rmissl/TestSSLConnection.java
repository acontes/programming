package functionalTests.rmissl;

import org.junit.Assert;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.Node;

import functionalTests.GCMFunctionalTestDefaultNodes;


public class TestSSLConnection extends GCMFunctionalTestDefaultNodes {

    public TestSSLConnection() {
        super(1, 1);
    }

    @org.junit.Test
    public void action() throws Exception {

        Node node1 = super.getANode();
        DummyAO createdDummy = (DummyAO) PAActiveObject.newActive(DummyAO.class.getName(), new Object[] {},
                node1);

        //performs a method call in order to wait for end of InitActivity
        PAFuture.waitFor(createdDummy.sayHello(""));

        String lookupURL = "rmi://" + node1.getVMInformation().getHostName() + ":" +
            PAProperties.PA_RMI_PORT.getValue() + "/" + DummyAO.class.getName();
        logger.info("lookup URL : " + lookupURL);

        DummyAO dummy = (DummyAO) PAActiveObject.lookupActive(DummyAO.class.getName(), lookupURL);

        logger.info("send message through RMIS SSL connection");
        String messToSend = "hey";
        String messReceived = dummy.sayHello(messToSend);
        Assert.assertEquals(messToSend, messReceived);
    }
}