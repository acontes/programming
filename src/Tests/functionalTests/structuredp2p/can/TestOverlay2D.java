package functionalTests.structuredp2p.can;

import java.util.List;
import java.util.Stack;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.can.NeighborsDataStructure;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.ZoneException;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANLookupResponseMessage;


/**
 * Test the {@link CANOverlay} in a 2D-CAN.
 * 
 * Warning : the constant {@link CANOverlay#NB_DIMENSIONS} must be set to 2.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestOverlay2D {

    private Peer firstPeer;
    private Peer secondPeer;
    private Peer thirdPeer;
    private Peer fourthPeer;

    private LookupMessage msg;

    @Before
    public void setUp() throws ActiveObjectCreationException, NodeException, ZoneException {
        CANOverlay overlay;
        Coordinate[] coordinateMin;
        Coordinate[] coordinateMax;
        Zone zone;
        Stack<int[]> splitHistory;

        /* First peer */

        this.firstPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        coordinateMin = new Coordinate[2];
        coordinateMax = new Coordinate[2];

        coordinateMin[0] = new Coordinate("0");
        coordinateMin[1] = new Coordinate("0");
        coordinateMax[0] = new Coordinate("6");
        coordinateMax[1] = new Coordinate("12");

        zone = new Zone(coordinateMin, coordinateMax);
        overlay = (CANOverlay) this.firstPeer.getStructuredOverlay();
        overlay.setZone(zone);
        splitHistory = new Stack<int[]>();
        splitHistory.add(new int[] { 0, 1 });
        overlay.setHistory(splitHistory);
        this.firstPeer.setStructuredOverlay(overlay);

        /* Second peer */

        this.secondPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        coordinateMin = new Coordinate[2];
        coordinateMax = new Coordinate[2];

        coordinateMin[0] = new Coordinate("6");
        coordinateMin[1] = new Coordinate("0");
        coordinateMax[0] = new Coordinate("12");
        coordinateMax[1] = new Coordinate("6");

        zone = new Zone(coordinateMin, coordinateMax);
        overlay = (CANOverlay) this.secondPeer.getStructuredOverlay();
        overlay.setZone(zone);
        splitHistory = new Stack<int[]>();
        splitHistory.add(new int[] { 0, 0 });
        splitHistory.add(new int[] { 1, 1 });
        overlay.setHistory(splitHistory);
        this.secondPeer.setStructuredOverlay(overlay);

        /* Third peer */

        this.thirdPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        coordinateMin = new Coordinate[2];
        coordinateMax = new Coordinate[2];

        coordinateMin[0] = new Coordinate("6");
        coordinateMin[1] = new Coordinate("6");
        coordinateMax[0] = new Coordinate("9");
        coordinateMax[1] = new Coordinate("12");

        zone = new Zone(coordinateMin, coordinateMax);
        overlay = (CANOverlay) this.thirdPeer.getStructuredOverlay();
        overlay.setZone(zone);
        splitHistory = new Stack<int[]>();
        splitHistory.add(new int[] { 0, 0 });
        splitHistory.add(new int[] { 1, 0 });
        splitHistory.add(new int[] { 0, 1 });
        overlay.setHistory(splitHistory);
        this.thirdPeer.setStructuredOverlay(overlay);

        /* Fourth peer */

        this.fourthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        coordinateMin = new Coordinate[2];
        coordinateMax = new Coordinate[2];

        coordinateMin[0] = new Coordinate("9");
        coordinateMin[1] = new Coordinate("6");
        coordinateMax[0] = new Coordinate("12");
        coordinateMax[1] = new Coordinate("12");

        zone = new Zone(coordinateMin, coordinateMax);
        overlay = (CANOverlay) this.fourthPeer.getStructuredOverlay();
        overlay.setZone(zone);
        splitHistory = new Stack<int[]>();
        splitHistory.add(new int[] { 0, 0 });
        splitHistory.add(new int[] { 1, 0 });
        splitHistory.add(new int[] { 0, 0 });
        overlay.setHistory(splitHistory);
        this.fourthPeer.setStructuredOverlay(overlay);

        /* Add neighbors to peers */

        overlay = (CANOverlay) this.firstPeer.getStructuredOverlay();
        overlay.addNeighbor(this.thirdPeer, 0, 1);
        overlay.addNeighbor(this.secondPeer, 0, 1);
        this.firstPeer.setStructuredOverlay(overlay);

        overlay = (CANOverlay) this.secondPeer.getStructuredOverlay();
        overlay.addNeighbor(this.firstPeer, 0, 0);
        overlay.addNeighbor(this.thirdPeer, 1, 1);
        overlay.addNeighbor(this.fourthPeer, 1, 1);
        this.secondPeer.setStructuredOverlay(overlay);

        overlay = (CANOverlay) this.thirdPeer.getStructuredOverlay();
        overlay.addNeighbor(this.firstPeer, 0, 0);
        overlay.addNeighbor(this.secondPeer, 1, 0);
        overlay.addNeighbor(this.fourthPeer, 0, 1);
        this.thirdPeer.setStructuredOverlay(overlay);

        overlay = (CANOverlay) this.fourthPeer.getStructuredOverlay();
        overlay.addNeighbor(this.thirdPeer, 0, 0);
        overlay.addNeighbor(this.secondPeer, 1, 0);
        this.fourthPeer.setStructuredOverlay(overlay);

        this.msg = new CANLookupMessage(new Coordinate[] { new Coordinate("11"), new Coordinate("11") });
    }

    @Test
    public void testCreate() {
        Assert.assertNotNull(this.firstPeer);
        Assert.assertNotNull(this.secondPeer);
        Assert.assertNotNull(this.thirdPeer);
        Assert.assertNotNull(this.fourthPeer);
        Assert.assertNotNull(this.msg);
    }

    @Test
    public void testContains() {
        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).contains(new Coordinate[] {
                new Coordinate("2"), new Coordinate("2") }));
        Assert.assertTrue(((CANOverlay) this.secondPeer.getStructuredOverlay()).contains(new Coordinate[] {
                new Coordinate("6"), new Coordinate("0") }));
        Assert.assertFalse(((CANOverlay) this.secondPeer.getStructuredOverlay()).contains(new Coordinate[] {
                new Coordinate("12"), new Coordinate("6") }));
        Assert.assertTrue(((CANOverlay) this.fourthPeer.getStructuredOverlay()).contains(new Coordinate[] {
                new Coordinate("11"), new Coordinate("11") }));
    }

    @Test
    public void testHasNeighbor() {
        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors().hasNeighbor(
                this.secondPeer));
        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors().hasNeighbor(
                this.thirdPeer));
        Assert.assertFalse(((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors().hasNeighbor(
                this.fourthPeer));
        Assert.assertFalse(((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors().hasNeighbor(
                this.firstPeer));
    }

    @Test
    public void testGetNearestNeighborFrom() {
        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors()
                .getNearestNeighborFrom(new Coordinate("11"), 0, 1).equals(this.thirdPeer));
    }

    @Test
    public void testIsBordered() {
        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).getZone().isBordered(
                ((CANOverlay) this.secondPeer.getStructuredOverlay()).getZone(), 0));
        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).getZone().isBordered(
                ((CANOverlay) this.thirdPeer.getStructuredOverlay()).getZone(), 0));
    }

    @Test
    public void testSendMessage() {
        // Lookup for peer which manages (11, 11)
        CANLookupResponseMessage response = (CANLookupResponseMessage) this.firstPeer.sendMessage(this.msg);
        Assert.assertEquals(this.fourthPeer, response.getPeer());
        Assert.assertTrue(response.getLatency() > 0);
    }

    @Test
    public void testNeighborsDataStructureOrder() {
        List<Peer> neighbors = ((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors()
                .getNeighbors(0, NeighborsDataStructure.SUPERIOR_DIRECTION);

        Assert
                .assertTrue(((CANOverlay) neighbors.get(0).getStructuredOverlay()).getZone()
                        .getCoordinateMax(1).compareTo(
                                ((CANOverlay) neighbors.get(1).getStructuredOverlay()).getZone()
                                        .getCoordinateMax(1)) < 0);
    }

    @Test
    public void testLeavingPeer() {
        this.thirdPeer.leave();

        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors().hasNeighbor(
                this.fourthPeer));
        Assert.assertTrue(((CANOverlay) this.fourthPeer.getStructuredOverlay()).getNeighbors().hasNeighbor(
                this.firstPeer));

        this.fourthPeer.leave();
        this.secondPeer.leave();

        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors().size() == 0);
    }

    @After
    public void tearDown() {
        this.msg = null;
        PAActiveObject.terminateActiveObject(this.firstPeer, false);
        PAActiveObject.terminateActiveObject(this.secondPeer, false);
        PAActiveObject.terminateActiveObject(this.thirdPeer, false);
        PAActiveObject.terminateActiveObject(this.fourthPeer, false);
    }

}