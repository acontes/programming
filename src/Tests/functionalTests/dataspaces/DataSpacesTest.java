package functionalTests.dataspaces;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


public class DataSpacesTest extends GCMFunctionalTestDataSpaces {
    private static final int HOST_CAPACITY = 1;
    private static final int VM_CAPACITY = 1;

    public DataSpacesTest() {
        super(HOST_CAPACITY, VM_CAPACITY);
    }

    @Test
    public void action() throws NodeException {
        // TODO
        for (final Node node : getAllNodes()) {
            assertEquals(0, node.getNumberOfActiveObjects());
        }
    }
}
