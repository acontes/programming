package functionalTests.structuredp2p.can;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


public class TestMe {

    private static Peer firstPeer;
    private static Peer secondPeer;
    private static Peer thirdPeer;
    private static Peer fourthPeer;
    private static Peer fifthPeer;
    private static Peer sixthPeer;
    private static Peer seventhPeer;
    private static Peer eighthPeer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        TestMe.firstPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestMe.secondPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestMe.thirdPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestMe.fourthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestMe.fifthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestMe.sixthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestMe.seventhPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestMe.eighthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Test
    public void testSecondPeer() {
        TestMe.secondPeer.join(TestMe.firstPeer);
        TestMe.thirdPeer.join(TestMe.secondPeer);
        TestMe.fourthPeer.join(TestMe.thirdPeer);
        TestMe.fifthPeer.join(TestMe.thirdPeer);
        TestMe.sixthPeer.join(TestMe.fourthPeer);

        System.out.println("peer1 = " + ((CANOverlay) TestMe.firstPeer.getStructuredOverlay()).getZone());
        System.out.println("peer2 = " + ((CANOverlay) TestMe.secondPeer.getStructuredOverlay()).getZone());
        System.out.println("peer3 = " + ((CANOverlay) TestMe.thirdPeer.getStructuredOverlay()).getZone());
        System.out.println("peer4 = " + ((CANOverlay) TestMe.fourthPeer.getStructuredOverlay()).getZone());
        System.out.println("peer5 = " + ((CANOverlay) TestMe.fifthPeer.getStructuredOverlay()).getZone());
        System.out.println("peer6 = " + ((CANOverlay) TestMe.sixthPeer.getStructuredOverlay()).getZone());

        int[] lastOperation = ((CANOverlay) TestMe.firstPeer.getStructuredOverlay()).getSplitHistory().pop();
        int lastDimension = lastOperation[0];
        int lastDirection = lastOperation[1];
        List<Peer> neighborsToMergeWith = ((CANOverlay) TestMe.firstPeer.getStructuredOverlay())
                .getNeighbors().getNeighbors(lastDimension, CANOverlay.getOppositeDirection(lastDirection));

        System.out.println("last dimension = " + lastDimension);
        System.out.println("last direction = " + lastDirection);

        System.out.println("p3 last dim = " +
            ((CANOverlay) TestMe.thirdPeer.getStructuredOverlay()).getSplitHistory().pop()[0]);

        System.out.println("Neighbors of peer 1 ");
        for (Peer neighbor : neighborsToMergeWith) {
            System.out.println(((CANOverlay) neighbor.getStructuredOverlay()).getZone());
        }

    }
}
