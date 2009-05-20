package functionalTests.structuredp2p.can;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANPeer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


public class TestNeighborsDataStructure {

    private CANPeer peer;
    private CANPeer firstNeighbor;
    private CANPeer secondNeighbor;

    @Before
    public void setUp() throws Exception {
        this.peer = (CANPeer) PAActiveObject.newActive(CANPeer.class.getName(),
                new Object[] { OverlayType.CAN });
        this.firstNeighbor = (CANPeer) PAActiveObject.newActive(CANPeer.class.getName(),
                new Object[] { OverlayType.CAN });
        this.secondNeighbor = (CANPeer) PAActiveObject.newActive(CANPeer.class.getName(),
                new Object[] { OverlayType.CAN });
        // Overlay overlay
        // this.peer.setStructuredOverlay(((CANOverlay)
        // this.peer.getStructuredOverlay()).addNeighbor(
        // this.firstNeighbor, 0, 1));
        // this.peer.setStructuredOverlay(((CANOverlay)
        // this.peer.getStructuredOverlay()).addNeighbor(
        // this.secondNeighbor, 0, 0));
    }

    @Test
    public void testAddAll() throws ActiveObjectCreationException, NodeException {
        NeighborsDataStructure neighbors = new NeighborsDataStructure();

        Assert.assertTrue(neighbors.hasNeighbor(this.firstNeighbor, 0, 1));
        Assert.assertTrue(neighbors.hasNeighbor(this.secondNeighbor, 0, 0));

        CANOverlay overlay = (this.peer.getStructuredOverlay());
        overlay.addNeighbor(neighbors);
        this.peer.setStructuredOverlay(overlay);

        Assert.assertTrue((this.peer.getStructuredOverlay()).getNeighbors().hasNeighbor(this.firstNeighbor,
                0, 1));
        Assert.assertTrue((this.peer.getStructuredOverlay()).getNeighbors().hasNeighbor(this.secondNeighbor,
                0, 0));
    }

    @Test
    public void testUpdateArea() {

    }

    @After
    public void tearDown() throws Exception {
        this.peer = null;
    }

}
