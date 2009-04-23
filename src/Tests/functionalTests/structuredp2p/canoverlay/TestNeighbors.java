package functionalTests.structuredp2p.canoverlay;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


public class TestNeighbors {

    @Test
    public void testAddNeighbors() {
        // First peer
        Peer first = new Peer(OverlayType.CAN);
        Peer neighbor = new Peer(OverlayType.CAN);

        neighbor.join(first);

        Assert.assertEquals(neighbor.getNeighbor(0, 1), first);
        Assert.assertEquals(first.getNeighbor(0, 0), neighbor);
    }
}
