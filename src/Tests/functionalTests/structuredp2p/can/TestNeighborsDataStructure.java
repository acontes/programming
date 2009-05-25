package functionalTests.structuredp2p.can;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


public class TestNeighborsDataStructure {

    private Peer peer;

    @Before
    public void setUp() throws Exception {
        this.peer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });
    }

    @Test
    public void testAddAll() throws ActiveObjectCreationException, NodeException {
        Peer peer1 = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });
        Peer peer2 = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });
        NeighborsDataStructure neighbors = new NeighborsDataStructure(this.peer);
        neighbors.add(peer1, 0, 1);
        neighbors.add(peer2, 0, 0);

        Assert.assertTrue(neighbors.hasNeighbor(peer1, 0, 1));
        Assert.assertTrue(neighbors.hasNeighbor(peer2, 0, 0));

        CANOverlay overlay = ((CANOverlay) this.peer.getStructuredOverlay());
        overlay.addNeighbor(neighbors);
        this.peer.setStructuredOverlay(overlay);

        Assert.assertTrue(((CANOverlay) this.peer.getStructuredOverlay()).getNeighbors().hasNeighbor(peer1,
                0, 1));
        Assert.assertTrue(((CANOverlay) this.peer.getStructuredOverlay()).getNeighbors().hasNeighbor(peer2,
                0, 0));
    }

    @After
    public void tearDown() throws Exception {
        this.peer = null;
    }

}