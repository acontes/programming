package functionalTests.structuredp2p.canoverlay;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CANLookupMessage;


/**
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
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

        // Test if the new added peer is in the neighbor list
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CANOverlay entryPointOverlay = (CANOverlay) this.entryPoint.getStructuredOverlay();
        CANOverlay neighborOverlay = (CANOverlay) this.neighbor.getStructuredOverlay();

        /*
         * Assert.assertTrue(this.neighbor.sendMessage());
         * Assert.assertTrue(this.entryPoint.hasNeighbor(this.neighbor.getStub()));
         */

        // Test if the new added peer has the good coordinates CanLookupMessage msgToEntryPoint =
        /*
         * new CanLookupMessage(entryPointOverlay.getArea() .getCoordinatesMin()); CanLookupMessage
         * msgToNeighbor = new CanLookupMessage(neighborOverlay.getArea().getCoordinatesMin());
         * 
         * Assert.assertEquals( ((CanLookupResponseMessage)
         * this.neighbor.sendMessage(msgToEntryPoint)).getPeer(), this.entryPoint);
         * Assert.assertEquals( ((CanLookupResponseMessage)
         * this.entryPoint.sendMessage(msgToNeighbor)).getPeer(), this.neighbor);
         */
        // TODO tests with split !
    }

    public void testLeave() {
        Assert.assertNotNull(this.entryPoint);
        Assert.assertNotNull(this.neighbor);

        this.neighbor.leave();

        CANOverlay entryPointOverlay = (CANOverlay) this.entryPoint.getStructuredOverlay();
        CANOverlay neighborOverlay = (CANOverlay) this.neighbor.getStructuredOverlay();

        // Test if the leaved peer is no more in the neighbor list
        Assert.assertFalse(neighborOverlay.hasNeighbor(this.entryPoint));
        Assert.assertFalse(entryPointOverlay.hasNeighbor(this.neighbor));

        // Test if the leaved peer is no more in the overlay
        CANLookupMessage msgToEntryPoint = new CANLookupMessage(entryPointOverlay.getArea()
                .getCoordinatesMin());
        CANLookupMessage msgToNeighbor = new CANLookupMessage(neighborOverlay.getArea().getCoordinatesMin());

        Assert.assertNotSame(this.neighbor.sendMessage(msgToEntryPoint), this.entryPoint);
        Assert.assertNotSame(this.entryPoint.sendMessage(msgToNeighbor), this.neighbor);

        // TODO tests with merge !
    }

    @After
    public void stopTest() {

    }

}
