package functionalTests.structuredp2p.can;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;


public class TestNeighborsDataStructure {

    private Peer peer;

    @Before
    public void setUp() throws Exception {
        this.peer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });
    }

    @Test
    public void testAddAll() throws ActiveObjectCreationException, NodeException {
        Peer peer1 = Peer.newActivePeer(OverlayType.CAN);
        Peer peer2 = Peer.newActivePeer(OverlayType.CAN);
        NeighborsDataStructure neighbors = new NeighborsDataStructure();
        neighbors.add(peer1, 0, 1);
        neighbors.add(peer2, 0, 0);

        Assert.assertTrue(neighbors.hasNeighbor(peer1, 0, 1));
        Assert.assertTrue(neighbors.hasNeighbor(peer2, 0, 0));

        CANOverlay overlay = ((CANOverlay) this.peer.getStructuredOverlay());
        overlay.addNeighbor(neighbors);
        this.peer.setStructuredOverlay(overlay);

        Assert.assertTrue(((CANOverlay) this.peer.getStructuredOverlay()).getNeighborsDataStructure()
                .hasNeighbor(peer1, 0, 1));
        Assert.assertTrue(((CANOverlay) this.peer.getStructuredOverlay()).getNeighborsDataStructure()
                .hasNeighbor(peer2, 0, 0));
    }

    @Test
    public void testOrder() {
        NeighborsDataStructure neighbors = ((CANOverlay) this.peer.getStructuredOverlay())
                .getNeighborsDataStructure();

        Iterator<Peer> it = neighbors.iterator();

        Peer cur = null;
        if (it.hasNext()) {
            cur = it.next();

            while (it.hasNext()) {
                Zone curZ = ((CANOverlay) cur.getStructuredOverlay()).getZone();

                cur = it.next();
                Zone nextZ = ((CANOverlay) cur.getStructuredOverlay()).getZone();

                Assert.assertNotSame(-1, curZ.getBorderedDimension(nextZ));
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        this.peer = null;
    }

}