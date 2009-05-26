package functionalTests.structuredp2p.can;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


/**
 * Test basic operations in a CAN network like {@link CANOverlay#join(Peer)},
 * {@link CANOverlay#leave()}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestBasicOperations {
    private static Peer entryPoint;
    private static Peer neighbor;

    @BeforeClass
    public static void initTest() {
        try {
            TestBasicOperations.entryPoint = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                    new Object[] { OverlayType.CAN });
            TestBasicOperations.neighbor = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                    new Object[] { OverlayType.CAN });
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJoin() {
        Assert.assertNotNull(TestBasicOperations.entryPoint);
        Assert.assertNotNull(TestBasicOperations.neighbor);

        TestBasicOperations.neighbor.join(TestBasicOperations.entryPoint);

        // Test if the new added peer is in the neighbor list
        CANOverlay entryPointOverlay = (CANOverlay) TestBasicOperations.entryPoint.getStructuredOverlay();
        CANOverlay neighborOverlay = (CANOverlay) TestBasicOperations.neighbor.getStructuredOverlay();

        Assert.assertTrue(entryPointOverlay.hasNeighbor(TestBasicOperations.neighbor));
        Assert.assertTrue(neighborOverlay.hasNeighbor(TestBasicOperations.entryPoint));

        // Test with split areas !
        try {
            Assert
                    .assertTrue(new Zone().equals(entryPointOverlay.getArea()
                            .merge(neighborOverlay.getArea())));
        } catch (AreaException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLeave() {
        Assert.assertNotNull(TestBasicOperations.entryPoint);
        Assert.assertNotNull(TestBasicOperations.neighbor);

        int nbNeighbors = ((CANOverlay) TestBasicOperations.entryPoint.getStructuredOverlay()).getNeighbors()
                .size();

        PAFuture.waitFor(TestBasicOperations.neighbor.leave());

        Assert.assertEquals(nbNeighbors - 1, ((CANOverlay) TestBasicOperations.entryPoint
                .getStructuredOverlay()).getNeighbors().size());

        // TODO tests with merged areas !
        Assert.assertEquals(new Zone(), ((CANOverlay) TestBasicOperations.entryPoint.getStructuredOverlay())
                .getArea());
    }

    @AfterClass
    public static void stopTest() {
        TestBasicOperations.entryPoint.leave();
    }

}