/**
 * 
 */
package functionalTests.structuredp2p.can;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANLookupResponseMessage;


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
        TestOverlay.firstPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestOverlay.secondPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestOverlay.thirdPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestOverlay.fourthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestOverlay.fifthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestOverlay.sixthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestOverlay.seventhPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestOverlay.eighthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Test
    public void testSecondPeer() {
        Assert.assertTrue(TestOverlay.secondPeer.join(TestOverlay.firstPeer));

        // Are they neighbors ??
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.secondPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(TestOverlay.firstPeer));

        // Is a peer neighbor to itself ??
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.firstPeer));
        Assert
                .assertFalse(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(
                        TestOverlay.secondPeer));

        // Are there zones bordered ??
        Assert.assertNotSame(TestOverlay.getOverlay(TestOverlay.firstPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.secondPeer).getZone()), -1);
    }

    @Test
    public void testThirdPeer() {
        Assert.assertTrue(TestOverlay.thirdPeer.join(TestOverlay.secondPeer));

        // Are they neighbors ??
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.thirdPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(TestOverlay.thirdPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.firstPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.secondPeer));

        // Is a peer neighbor to itself ??
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.firstPeer));
        Assert
                .assertFalse(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(
                        TestOverlay.secondPeer));
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.thirdPeer));

        // Are there zones bordered ??
        Assert.assertNotSame(TestOverlay.getOverlay(TestOverlay.firstPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.thirdPeer).getZone()), -1);
        Assert.assertNotSame(TestOverlay.getOverlay(TestOverlay.secondPeer).getZone().getBorderedDimension(
                TestOverlay.getOverlay(TestOverlay.thirdPeer).getZone()), -1);
    }

    @Test
    public void testFourthPeer() {
        Assert.assertTrue(TestOverlay.fourthPeer.join(TestOverlay.thirdPeer));

        // Are they neighbors ??
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(TestOverlay.fourthPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.fourthPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.fourthPeer).hasNeighbor(TestOverlay.secondPeer));
        Assert.assertTrue(TestOverlay.getOverlay(TestOverlay.fourthPeer).hasNeighbor(TestOverlay.thirdPeer));

        // Is a peer neighbor to itself ??
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.firstPeer).hasNeighbor(TestOverlay.firstPeer));
        Assert
                .assertFalse(TestOverlay.getOverlay(TestOverlay.secondPeer).hasNeighbor(
                        TestOverlay.secondPeer));
        Assert.assertFalse(TestOverlay.getOverlay(TestOverlay.thirdPeer).hasNeighbor(TestOverlay.thirdPeer));
        Assert
                .assertFalse(TestOverlay.getOverlay(TestOverlay.fourthPeer).hasNeighbor(
                        TestOverlay.fourthPeer));

        // Are there zones bordered ??
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
        int second = TestOverlay.getOverlay(TestOverlay.secondPeer).getNeighbors().size();

        Assert.assertTrue(TestOverlay.fourthPeer.leave());

        Assert.assertEquals(second - 1, TestOverlay.getOverlay(TestOverlay.secondPeer).getNeighbors().size());

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
    public void testSendMessage() {
        Peer[] peers = new Peer[] { TestOverlay.firstPeer, TestOverlay.secondPeer, TestOverlay.thirdPeer,
                TestOverlay.fifthPeer, TestOverlay.sixthPeer, TestOverlay.seventhPeer, TestOverlay.eighthPeer };

        Random rand = new Random();

        for (int i = 3; i < peers.length; i++) {
            peers[i].join(peers[rand.nextInt(i)]);
        }

        Peer toFind = peers[rand.nextInt(peers.length)];
        Peer sender = peers[rand.nextInt(peers.length)];

        CANLookupMessage msg = new CANLookupMessage(TestOverlay.getOverlay(toFind).getZone()
                .getCoordinatesMin());

        CANLookupResponseMessage response = (CANLookupResponseMessage) sender.sendMessage(msg);

        Assert.assertEquals(TestOverlay.getOverlay(toFind).getZone(), TestOverlay.getOverlay(
                response.getPeer()).getZone());
        Assert.assertEquals(toFind, response.getPeer());
    }

    @Test
    public void leaveAll() {
        Assert.assertTrue(TestOverlay.thirdPeer.leave());
        Assert.assertTrue(TestOverlay.secondPeer.leave());
        Assert.assertTrue(TestOverlay.firstPeer.leave());
    }

    public static CANOverlay getOverlay(Peer peer) {
        return ((CANOverlay) peer.getStructuredOverlay());
    }
}