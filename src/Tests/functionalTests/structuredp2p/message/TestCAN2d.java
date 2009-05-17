package functionalTests.structuredp2p.message;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.can.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.can.CANLookupResponseMessage;


/**
 * Test the framework in a 2D-CAN.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestCAN2d {

    private Peer firstPeer;
    private Peer secondPeer;
    private Peer thirdPeer;
    private Peer fourthPeer;

    private LookupMessage msg;

    @Before
    public void setUp() throws ActiveObjectCreationException, NodeException {
        CANOverlay overlay;
        Coordinate[] coordinateMin;
        Coordinate[] coordinateMax;
        Area area;

        this.firstPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        this.secondPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        this.thirdPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        this.fourthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        /* First peer */

        coordinateMin = new Coordinate[2];
        coordinateMax = new Coordinate[2];

        coordinateMin[0] = new Coordinate("0");
        coordinateMin[1] = new Coordinate("0");
        coordinateMax[0] = new Coordinate("6");
        coordinateMax[1] = new Coordinate("12");

        area = new Area(coordinateMin, coordinateMax);
        overlay = (CANOverlay) this.firstPeer.getStructuredOverlay();
        overlay.setArea(area);
        overlay.addNeighbor(this.secondPeer, 0, 1);
        overlay.addNeighbor(this.thirdPeer, 0, 1);
        this.firstPeer.setStructuredOverlay(overlay);

        /* Second peer */

        coordinateMin = new Coordinate[2];
        coordinateMax = new Coordinate[2];

        coordinateMin[0] = new Coordinate("6");
        coordinateMin[1] = new Coordinate("0");
        coordinateMax[0] = new Coordinate("12");
        coordinateMax[1] = new Coordinate("6");

        area = new Area(coordinateMin, coordinateMax);
        overlay = (CANOverlay) this.secondPeer.getStructuredOverlay();
        overlay.setArea(area);
        overlay.addNeighbor(this.thirdPeer, 1, 1);
        overlay.addNeighbor(this.fourthPeer, 1, 1);
        this.secondPeer.setStructuredOverlay(overlay);

        /* Third peer */

        coordinateMin = new Coordinate[2];
        coordinateMax = new Coordinate[2];

        coordinateMin[0] = new Coordinate("6");
        coordinateMin[1] = new Coordinate("6");
        coordinateMax[0] = new Coordinate("9");
        coordinateMax[1] = new Coordinate("12");

        area = new Area(coordinateMin, coordinateMax);
        overlay = (CANOverlay) this.thirdPeer.getStructuredOverlay();
        overlay.setArea(area);
        overlay.addNeighbor(this.secondPeer, 0, 0);
        overlay.addNeighbor(this.fourthPeer, 0, 1);
        this.thirdPeer.setStructuredOverlay(overlay);

        /* Fourth peer */

        coordinateMin = new Coordinate[2];
        coordinateMax = new Coordinate[2];

        coordinateMin[0] = new Coordinate("9");
        coordinateMin[1] = new Coordinate("6");
        coordinateMax[0] = new Coordinate("12");
        coordinateMax[1] = new Coordinate("12");

        area = new Area(coordinateMin, coordinateMax);
        overlay = (CANOverlay) this.fourthPeer.getStructuredOverlay();
        overlay.setArea(area);
        overlay.addNeighbor(this.thirdPeer, 0, 0);
        overlay.addNeighbor(this.secondPeer, 1, 0);
        this.fourthPeer.setStructuredOverlay(overlay);

        System.out.println("neighbors peer 1 = " +
            ((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors());

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

    @Ignore
    public void testGetNearestNeighborFrom() {
        Assert.assertTrue(((CANOverlay) this.firstPeer.getStructuredOverlay()).getNeighbors()
                .getNearestNeighborFrom(new Coordinate("11"), 0, 1).equals(this.thirdPeer));
    }

    @Test
    public void testSendMessage() {
        // Lookup for peer which manages (11, 11)
        CANLookupResponseMessage response = (CANLookupResponseMessage) this.firstPeer.sendMessage(this.msg);
        Assert.assertEquals(this.fourthPeer, response.getPeer());
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
