package functionalTests.structuredp2p.message;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.CanOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CanLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestCanMessage {

    private Peer srcPeer;
    private Peer myPeer;
    private LookupMessage lMsg;
    private Coordinate messCord[];
    private Coordinate minCord[];
    private Coordinate maxCord[];
    private ResponseMessage srcResponse;
    private ResponseMessage myResponse;
    private Area area;
    private Area areaSplit1;
    private Area areaSplit2;
    private CanOverlay can;
    private CanOverlay splitCan1;
    private CanOverlay splitCan2;
    private int dim;
    @Before
    public void init() throws ActiveObjectCreationException, NodeException {
        srcPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });

        myPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });
        dim = CanOverlay.NB_DIMENSIONS;
        messCord = new Coordinate[dim];
        
        messCord[0] = new Coordinate("2");
        messCord[1] = new Coordinate("2");

        minCord = new Coordinate[dim];
        minCord[0] = new Coordinate("9");
        minCord[1] = new Coordinate("9");

        maxCord = new Coordinate[dim];
        maxCord[0] = new Coordinate("0");
        maxCord[1] = new Coordinate("0");

        area = new Area(minCord, maxCord);

        can = ((CanOverlay) (srcPeer.getStructuredOverlay()));
        can.setArea(area);
        srcPeer.setStructuredOverlay(can);
        lMsg = new CanLookupMessage(messCord);
    }

    @Test
    public void testCreate() {
        assertNotNull("create a new peer", srcPeer);
        assertNotNull("area set on the overlay", ((CanOverlay) (srcPeer.getStructuredOverlay())).getArea()
                .getCoordinatesMin());
        assertNotNull("get new peer", myPeer);
        assertNotNull("create a new CAN message", lMsg);
        assertNotNull("create a new coordinate table", messCord);
        assertNotNull("create a new coordinate table", minCord);
        assertNotNull("create a new coordinate table", maxCord);
    }

    @Test
    public void testSendMessage() {
        srcResponse = srcPeer.sendMessage(lMsg);
        assertNotNull("the src response is not null", srcResponse);
        assertArrayEquals("routing by bootStrap succes", ((CanLookupResponseMessage) srcResponse)
                .getCoordinates(), messCord);
        assertEquals("first coordinate ok", ((CanOverlay) ((CanLookupResponseMessage) srcResponse).getPeer()
                .getStructuredOverlay()).contains(0, messCord[0]), 0);
        assertEquals("second coordinate ok", ((CanOverlay) ((CanLookupResponseMessage) srcResponse).getPeer()
                .getStructuredOverlay()).contains(1, messCord[1]), 0);
    }

    @Test
    public void testJoinAndSendMessage() {
        srcPeer.join(myPeer);
        // method split not yet implemented
        minCord = new Coordinate[dim];
        minCord[0] = new Coordinate("9");
        minCord[1] = new Coordinate("9");

        maxCord = new Coordinate[dim];
        maxCord[0] = new Coordinate("4");
        maxCord[1] = new Coordinate("0");

        areaSplit1 = new Area(minCord, maxCord);
        //
        minCord = new Coordinate[dim];
        minCord[0] = new Coordinate("4");
        minCord[1] = new Coordinate("9");

        maxCord = new Coordinate[dim];
        maxCord[0] = new Coordinate("0");
        maxCord[1] = new Coordinate("0");

        areaSplit2 = new Area(minCord, maxCord);
        //
        splitCan1 = ((CanOverlay) (srcPeer.getStructuredOverlay()));
        splitCan1.setArea(areaSplit1);

        splitCan2 = ((CanOverlay) (myPeer.getStructuredOverlay()));
        splitCan2.setArea(areaSplit2);

        srcPeer.setStructuredOverlay(splitCan1);
        myPeer.setStructuredOverlay(splitCan2);

        myResponse = myPeer.sendMessage(lMsg);

        assertNotNull("the src response is not null", myResponse);
        assertEquals("first coordinate ok", ((CanOverlay) ((CanLookupResponseMessage) myResponse).getPeer()
                .getStructuredOverlay()).contains(0, messCord[0]), 0);
        assertEquals("second coordinate ok", ((CanOverlay) ((CanLookupResponseMessage) myResponse).getPeer()
                .getStructuredOverlay()).contains(1, messCord[1]), 0);

    }

    @After
    public void clean() {
        srcPeer = null;
        myPeer = null;
        lMsg = null;
        messCord = null;
    }

}
