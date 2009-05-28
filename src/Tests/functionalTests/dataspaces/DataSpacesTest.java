package functionalTests.dataspaces;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.annotation.ActiveObject;


public class DataSpacesTest extends GCMFunctionalTestDataSpaces {
    private static final int HOST_CAPACITY = 1;
    private static final int VM_CAPACITY = 1;

    public DataSpacesTest() {
        super(HOST_CAPACITY, VM_CAPACITY);
    }

    @Test
    public void action() throws NodeException, ActiveObjectCreationException {
        // TODO
        Node node = getANode();
        final TestActiveObject ao = (TestActiveObject) PAActiveObject.newActive(TestActiveObject.class
                .getName(), null, node);
        assertEquals("hello", ao.sayHello());
    }

    @ActiveObject
    public static class TestActiveObject implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 3003545576343285983L;

        public TestActiveObject() {
        }

        public String sayHello() {
            return "hello";
        }
    }

}
