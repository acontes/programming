/**
 * 
 */
package functionalTests.structuredp2p.can;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


/**
 * Test the {@link CANOverlay} for any {@link CANOverlay#NB_DIMENSIONS}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestOverlay {

    private static Peer firstPeer;
    private static Peer secondPeer;
    private static Peer thirdPeer;
    private static Peer fourthPeer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        TestOverlay.firstPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TestOverlay.thirdPeer.leave();
        TestOverlay.secondPeer.leave();
        TestOverlay.firstPeer.leave();
    }

    @Test
    public void testJoin() {
        try {
            TestOverlay.secondPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                    new Object[] { OverlayType.CAN });
            TestOverlay.thirdPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                    new Object[] { OverlayType.CAN });
            TestOverlay.fourthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                    new Object[] { OverlayType.CAN });
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }

        TestOverlay.secondPeer.join(TestOverlay.firstPeer);
        TestOverlay.thirdPeer.join(TestOverlay.secondPeer);
        TestOverlay.fourthPeer.join(TestOverlay.thirdPeer);

        Assert.assertTrue(((CANOverlay) TestOverlay.firstPeer.getStructuredOverlay())
                .hasNeighbor(TestOverlay.secondPeer));

        System.out
                .println("peer1 : " + ((CANOverlay) TestOverlay.firstPeer.getStructuredOverlay()).getZone());
        System.out.println("peer1 neighbors : " +
            ((CANOverlay) TestOverlay.firstPeer.getStructuredOverlay()).getNeighbors());
        System.out.println("peer2 : " +
            ((CANOverlay) TestOverlay.secondPeer.getStructuredOverlay()).getZone());
        System.out.println("peer2 neighbors : " +
            ((CANOverlay) TestOverlay.secondPeer.getStructuredOverlay()).getNeighbors());
        System.out
                .println("peer3 : " + ((CANOverlay) TestOverlay.thirdPeer.getStructuredOverlay()).getZone());
        System.out.println("peer3 neighbors : " +
            ((CANOverlay) TestOverlay.thirdPeer.getStructuredOverlay()).getNeighbors());
        System.out.println("peer4 : " +
            ((CANOverlay) TestOverlay.fourthPeer.getStructuredOverlay()).getZone());
        System.out.println("peer4 neighbors : " +
            ((CANOverlay) TestOverlay.fourthPeer.getStructuredOverlay()).getNeighbors());
    }

    @Test
    public void testLeave() {
        TestOverlay.fourthPeer.leave();

        Assert.assertTrue(((CANOverlay) TestOverlay.firstPeer.getStructuredOverlay())
                .hasNeighbor(TestOverlay.secondPeer));

        System.out
                .println("peer1 : " + ((CANOverlay) TestOverlay.firstPeer.getStructuredOverlay()).getZone());
        System.out.println("peer1 neighbors : " +
            ((CANOverlay) TestOverlay.firstPeer.getStructuredOverlay()).getNeighbors());
        System.out.println("peer2 : " +
            ((CANOverlay) TestOverlay.secondPeer.getStructuredOverlay()).getZone());
        System.out.println("peer2 neighbors : " +
            ((CANOverlay) TestOverlay.secondPeer.getStructuredOverlay()).getNeighbors());
        System.out
                .println("peer3 : " + ((CANOverlay) TestOverlay.thirdPeer.getStructuredOverlay()).getZone());
        System.out.println("peer3 neighbors : " +
            ((CANOverlay) TestOverlay.thirdPeer.getStructuredOverlay()).getNeighbors());
    }
}
