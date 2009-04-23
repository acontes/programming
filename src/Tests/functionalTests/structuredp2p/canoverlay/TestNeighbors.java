package functionalTests.structuredp2p.canoverlay;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.CanOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CanLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanLookupResponseMessage;


public class TestNeighbors {
    private Peer entryPoint;
    private Peer neighbor;

    @Before
    public void initTest() throws ActiveObjectCreationException, NodeException {
        this.entryPoint = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        this.neighbor = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
    }

    @Test
    public void testJoin() {
        Assert.assertNotNull(this.entryPoint);
        Assert.assertNotNull(this.neighbor);

        this.neighbor.join(this.entryPoint);

        CanOverlay entryPointOverlay = (CanOverlay) this.entryPoint.getStructuredOverlay();
        CanOverlay neighborOverlay = (CanOverlay) this.neighbor.getStructuredOverlay();

        // Test if the new added peer is in the neighbor list
        Assert.assertTrue(neighborOverlay.hasNeighbor(this.entryPoint));
        Assert.assertTrue(entryPointOverlay.hasNeighbor(this.neighbor));

        // Test if the new added peer has the good coordinates
        CanLookupMessage msgToEntryPoint = new CanLookupMessage(entryPointOverlay.getArea()
                .getCoordinatesMin());
        CanLookupMessage msgToNeighbor = new CanLookupMessage(neighborOverlay.getArea().getCoordinatesMin());

        Assert.assertEquals(
                ((CanLookupResponseMessage) this.neighbor.sendMessage(msgToEntryPoint)).getPeer(),
                this.entryPoint);
        Assert.assertEquals(
                ((CanLookupResponseMessage) this.entryPoint.sendMessage(msgToNeighbor)).getPeer(),
                this.neighbor);

        // TODO tests with split !
    }

    @Test
    public void testLeave() {
        Assert.assertNotNull(this.entryPoint);
        Assert.assertNotNull(this.neighbor);

        this.neighbor.leave();

        CanOverlay entryPointOverlay = (CanOverlay) this.entryPoint.getStructuredOverlay();
        CanOverlay neighborOverlay = (CanOverlay) this.neighbor.getStructuredOverlay();

        // Test if the leaved peer is no ;ore in the neighbor list
        Assert.assertFalse(neighborOverlay.hasNeighbor(this.entryPoint));
        Assert.assertFalse(entryPointOverlay.hasNeighbor(this.neighbor));

        // Test if the leaved peer is no more in the overlay
        CanLookupMessage msgToEntryPoint = new CanLookupMessage(entryPointOverlay.getArea()
                .getCoordinatesMin());
        CanLookupMessage msgToNeighbor = new CanLookupMessage(neighborOverlay.getArea().getCoordinatesMin());

        Assert.assertNotSame(this.neighbor.sendMessage(msgToEntryPoint), this.entryPoint);
        Assert.assertNotSame(this.entryPoint.sendMessage(msgToNeighbor), this.neighbor);

        // TODO tests with merge !
    }

    @After
    public void stopTest() {
        PAActiveObject.terminateActiveObject(this.neighbor, true);
        PAActiveObject.terminateActiveObject(this.entryPoint, true);
    }

}
