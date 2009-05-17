package functionalTests.structuredp2p.can;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


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

        Assert.assertTrue(entryPointOverlay.getNeighbors().hasNeighbor(TestNeighbors.neighbor));
        Assert.assertTrue(neighborOverlay.getNeighbors().hasNeighbor(TestNeighbors.entryPoint));

        // tests with split areas !
        try {
            Assert
                    .assertTrue(new Area().equals(entryPointOverlay.getArea()
                            .merge(neighborOverlay.getArea())));
        } catch (AreaException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLeave() {
        Assert.assertNotNull(TestNeighbors.entryPoint);
        Assert.assertNotNull(TestNeighbors.neighbor);

        CANOverlay entryPointOverlay = (CANOverlay) TestNeighbors.entryPoint.getStructuredOverlay();

        int nbNeighbors = entryPointOverlay.getNeighbors().size();

        TestNeighbors.neighbor.leave();

        Assert.assertEquals(nbNeighbors - 1, entryPointOverlay.getNeighbors().size());

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
