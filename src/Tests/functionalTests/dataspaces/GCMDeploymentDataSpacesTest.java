package functionalTests.dataspaces;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


public class GCMDeploymentDataSpacesTest extends GCMFunctionalTestDataSpaces {

    public GCMDeploymentDataSpacesTest() {
        super(2, 2);
    }

    @Test
    public void action() throws NodeException {
        final Node node = getANode();
        assertEquals(0, node.getNumberOfActiveObjects());
    }
}
