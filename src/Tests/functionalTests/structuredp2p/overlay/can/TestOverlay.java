package functionalTests.structuredp2p.overlay.can;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.LookupQueryMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can.LookupResponseMessage;


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
    private static Peer fifthPeer;
    private static Peer sixthPeer;
    private static Peer seventhPeer;
    private static Peer eighthPeer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        TestOverlay.firstPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.secondPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.thirdPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.fourthPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.fifthPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.sixthPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.seventhPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.eighthPeer = Peer.newActivePeer(OverlayType.CAN);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Test
    public void testSecondPeer() {
        try {
            Assert.assertTrue(TestOverlay.secondPeer.join(TestOverlay.firstPeer));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check that the peers are neighbors
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.secondPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(TestOverlay.firstPeer));

        // Check if a peer has itself in it neighbors list
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.firstPeer));
        Assert
                .assertFalse(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(
                        TestOverlay.secondPeer));

        // Check the zone of the peers are bordered
        Assert.assertNotSame(TestOverlay.getOverlay(TestOverlay.firstPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.secondPeer).getZone()), -1);
    }

    @Test
    public void testThirdPeer() {
        try {
            Assert.assertTrue(TestOverlay.thirdPeer.join(TestOverlay.secondPeer));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check that the peers are neighbors
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.thirdPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(TestOverlay.thirdPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.firstPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.secondPeer));

        // Check if a peer has itself in it neighbors list
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.thirdPeer));

        // Check the zone of the peers are bordered
        Assert.assertNotSame(TestOverlay.getOverlay(TestOverlay.firstPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.thirdPeer).getZone()), -1);
        Assert.assertNotSame(TestOverlay.getOverlay(TestOverlay.secondPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.thirdPeer).getZone()), -1);
    }

    @Test
    public void testFourthPeer() {
        try {
            Assert.assertTrue(TestOverlay.fourthPeer.join(TestOverlay.thirdPeer));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check that the peers are neighbors
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(TestOverlay.fourthPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.fourthPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.fourthPeer).hasNeighbor(TestOverlay.secondPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.fourthPeer).hasNeighbor(TestOverlay.thirdPeer));

        // Check if a peer has itself in it neighbors list
        Assert
                .assertFalse(TestOverlay.getOverlay(TestOverlay.fourthPeer).hasNeighbor(
                        TestOverlay.fourthPeer));

        // Check the zone of the peers are bordered
        Assert.assertNotSame(TestOverlay.getOverlay(TestOverlay.secondPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.thirdPeer).getZone()), -1);

        if (TestOverlay.getOverlay(TestOverlay.firstPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.thirdPeer).getZone()) != -1) {
            Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.firstPeer)
                    .hasNeighbor(TestOverlay.thirdPeer));
            Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.thirdPeer)
                    .hasNeighbor(TestOverlay.firstPeer));
        } else {
            Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(
                    TestOverlay.thirdPeer));
            Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(
                    TestOverlay.firstPeer));
        }

        if (TestOverlay.getOverlay(TestOverlay.firstPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.fourthPeer).getZone()) != -1) {
            Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(
                    TestOverlay.fourthPeer));
            Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.fourthPeer).hasNeighbor(
                    TestOverlay.firstPeer));
        } else {
            Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(
                    TestOverlay.fourthPeer));
            Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.fourthPeer).hasNeighbor(
                    TestOverlay.firstPeer));
        }

    }

    @Test
    public void testLeave() {
        int second = TestOverlay.getOverlay(TestOverlay.secondPeer).getNeighborsDataStructure().size();

        Assert.assertTrue(TestOverlay.fourthPeer.leave());

        Assert.assertEquals(second - 1, TestOverlay.getOverlay(TestOverlay.secondPeer)
                .getNeighborsDataStructure().size());

        // Is the peer a neighbor again ??
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.fourthPeer));
        Assert
                .assertFalse(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(
                        TestOverlay.fourthPeer));
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.fourthPeer));

        // Is a peer neighbor to itself ??
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.firstPeer));
        Assert
                .assertFalse(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(
                        TestOverlay.secondPeer));
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.thirdPeer));
    }

    @Test
    public void leaveAll() {
        Assert.assertTrue(TestOverlay.thirdPeer.leave());
        Assert.assertTrue(TestOverlay.secondPeer.leave());
        Assert.assertTrue(TestOverlay.firstPeer.leave());
    }

    @Test
    public void testSendMessage() throws Exception {
        TestOverlay.firstPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.secondPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.thirdPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.fourthPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.fifthPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.sixthPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.seventhPeer = Peer.newActivePeer(OverlayType.CAN);
        TestOverlay.eighthPeer = Peer.newActivePeer(OverlayType.CAN);

        TestOverlay.secondPeer.join(TestOverlay.firstPeer);
        TestOverlay.thirdPeer.join(TestOverlay.secondPeer);

        Peer[] peers = new Peer[] { TestOverlay.firstPeer, TestOverlay.secondPeer, TestOverlay.thirdPeer,
                TestOverlay.fifthPeer, TestOverlay.sixthPeer, TestOverlay.seventhPeer, TestOverlay.eighthPeer };

        Random rand = new Random();

        for (int i = 3; i < peers.length; i++) {
            try {
                peers[i].join(peers[rand.nextInt(i)]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Peer toFind = peers[rand.nextInt(peers.length)];
        Peer sender = peers[rand.nextInt(peers.length)];

        LookupQueryMessage msg = new LookupQueryMessage(sender, TestOverlay.getOverlay(toFind).getZone()
                .getCoordinatesMin());

        LookupResponseMessage response = (LookupResponseMessage) PAFuture.getFutureValue(sender.search(msg));

        Assert.assertEquals(TestOverlay.getOverlay(toFind).getZone(), TestOverlay.getOverlay(
                response.getPeerFound()).getZone());
        Assert.assertEquals(toFind, response.getPeerFound());
    }

    public static CANOverlay getOverlay(Peer peer) {
        return ((CANOverlay) peer.getStructuredOverlay());
    }
}
