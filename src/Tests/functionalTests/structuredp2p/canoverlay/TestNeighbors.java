package functionalTests.structuredp2p.canoverlay;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


/**
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestNeighbors {
    private static Peer entryPoint;
    private static Peer neighbor;

    @BeforeClass
    public static void initTest() {
        try {
            TestNeighbors.entryPoint = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                    new Object[] { OverlayType.CAN });
            TestNeighbors.neighbor = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                    new Object[] { OverlayType.CAN });
        } catch (ActiveObjectCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testJoin() {
        Assert.assertNotNull(TestNeighbors.entryPoint);
        Assert.assertNotNull(TestNeighbors.neighbor);

        TestNeighbors.neighbor.join(TestNeighbors.entryPoint);

        // Test if the new added peer is in the neighbor list
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CANOverlay entryPointOverlay = (CANOverlay) TestNeighbors.entryPoint.getStructuredOverlay();
        CANOverlay neighborOverlay = (CANOverlay) TestNeighbors.neighbor.getStructuredOverlay();

        Assert.assertTrue(entryPointOverlay.hasNeighbor(TestNeighbors.neighbor));
        Assert.assertTrue(neighborOverlay.hasNeighbor(TestNeighbors.entryPoint));

        // TODO tests with splited areas !
    }

    @Test
    public void testLeave() {
        Assert.assertNotNull(TestNeighbors.entryPoint);
        Assert.assertNotNull(TestNeighbors.neighbor);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TestNeighbors.neighbor.leave();

        CANOverlay entryPointOverlay = (CANOverlay) TestNeighbors.entryPoint.getStructuredOverlay();
        // CANOverlay neighborOverlay = (CANOverlay) this.neighbor.getStructuredOverlay();

        // Test if the leaved peer is no more in the neighbor list
        // Assert.assertFalse(neighborOverlay.hasNeighbor(this.entryPoint));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TODO tests with neighbors !
        System.out.println("neighbor : " + TestNeighbors.neighbor);
        Assert.assertFalse(entryPointOverlay.hasNeighbor(TestNeighbors.neighbor));

        // TODO tests with merged areas !
    }

    @AfterClass
    public static void stopTest() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TestNeighbors.entryPoint.leave();
    }

}
