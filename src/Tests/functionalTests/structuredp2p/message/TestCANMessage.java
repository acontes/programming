package functionalTests.structuredp2p.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CANLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CANLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestCANMessage {

    private Peer srcPeer;
    private Peer myPeer;
    private Peer secondPeer;
    private LookupMessage lMsg;
    private Coordinate messCoord[];
    private Coordinate minCoord[];
    private Coordinate maxCoord[];
    private ResponseMessage srcResponse;
    private ResponseMessage myResponse;
    private Area area;
    private Area areaSplit1;
    private Area areaSplit2;
    private CANOverlay can;
    private CANOverlay splitCan1;
    private CANOverlay splitCan2;
    private int dim;

    @Before
    public void init() throws ActiveObjectCreationException, NodeException {
        srcPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });

        myPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });

        secondPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });
        dim = CANOverlay.NB_DIMENSIONS;
        messCoord = new Coordinate[dim];

        for (int i = 0; i < dim; i++) {
            int random = (int) (Math.random() * (7 - 5)) + 5;
            messCoord[i] = new Coordinate("" + random);
        }

        minCoord = new Coordinate[dim];

        for (int i = 0; i < dim; i++) {
            int random = (int) (Math.random() * (9 - 7)) + 7;
            minCoord[i] = new Coordinate("" + random);

        }

        maxCoord = new Coordinate[dim];
        for (int i = 0; i < dim; i++) {
            int random = (int) (Math.random() * 4);
            maxCoord[i] = new Coordinate("" + random);

        }

        area = new Area(minCoord, maxCoord);

        can = ((CANOverlay) (srcPeer.getStructuredOverlay()));
        can.setArea(area);
        srcPeer.setStructuredOverlay(can);
        lMsg = new CANLookupMessage(messCoord);
    }

    @Test
    public void testCreate() {
        assertNotNull("create a new peer", srcPeer);
        assertNotNull("area set on the overlay", ((CANOverlay) (srcPeer.getStructuredOverlay())).getArea()
                .getCoordinatesMin());
        assertNotNull("get new peer", myPeer);
        assertNotNull("create a new CAN message", lMsg);
        assertNotNull("create a new coordinate table", messCoord);
        assertNotNull("create a new coordinate table", minCoord);
        assertNotNull("create a new coordinate table", maxCoord);
    }

    @Test
    public void testSendMessage() {

        srcResponse = srcPeer.sendMessage(lMsg);
        assertNotNull("the src response is not null", srcResponse);
        for (int i = 0; i < dim; i++) {
            assertEquals(i + "th coordinate ok", ((CANOverlay) ((CANLookupResponseMessage) srcResponse)
                    .getPeer().getStructuredOverlay()).contains(i, messCoord[i]), 0);
        }
    }

    @Test
    public void testJoinAndSendMessage() {
        myPeer.join(srcPeer);
        // assertTrue("joining neighbor", ((CanOverlay)
        // srcPeer.getStructuredOverlay()).hasNeighbor(myPeer));
        // assertTrue("joining 2", ((CanOverlay)
        // myPeer.getStructuredOverlay()).hasNeighbor(srcPeer));
        // srcPeer.join(secondPeer);
        // method split not yet implemented

        // assertTrue("joining neighbor",
        // ((CanOverlay)srcPeer.getStructuredOverlay()).hasNeighbor(secondPeer));
        // assertTrue("joining 2",((CanOverlay)secondPeer.getStructuredOverlay()).hasNeighbor(srcPeer));

        int randAxe = (int) (Math.random() * dim);
        minCoord = new Coordinate[dim];

        for (int i = 0; i < dim; i++) {
            minCoord[i] = new Coordinate("9");
        }

        maxCoord = new Coordinate[dim];
        for (int i = 0; i < dim; i++) {
            if (i == randAxe) {
                maxCoord[i] = new Coordinate("4");
            } else {
                maxCoord[i] = new Coordinate("0");
            }
        }

        areaSplit1 = new Area(minCoord, maxCoord);
        //
        minCoord = new Coordinate[dim];
        for (int i = 0; i < dim; i++) {
            if (i == randAxe) {
                minCoord[i] = new Coordinate("4");
            } else {
                minCoord[i] = new Coordinate("9");
            }
        }

        maxCoord = new Coordinate[dim];
        for (int i = 0; i < dim; i++) {
            maxCoord[i] = new Coordinate("0");
        }

        areaSplit2 = new Area(minCoord, maxCoord);
        //
        splitCan1 = ((CANOverlay) (srcPeer.getStructuredOverlay()));
        splitCan1.setArea(areaSplit1);

        splitCan2 = ((CANOverlay) (myPeer.getStructuredOverlay()));
        splitCan2.setArea(areaSplit2);

        srcPeer.setStructuredOverlay(splitCan1);
        myPeer.setStructuredOverlay(splitCan2);

        myResponse = srcPeer.sendMessage(lMsg);
        srcResponse = myPeer.sendMessageTo(srcPeer, new PingMessage());
        assertNotNull("the src response is not null", myResponse);

        for (int i = 0; i < dim; i++) {
            assertEquals(i + "th coordinate ok", ((CANOverlay) ((CANLookupResponseMessage) myResponse)
                    .getPeer().getStructuredOverlay()).contains(i, messCoord[i]), 0);

        }

    }

    @After
    public void clean() {
        srcPeer = null;
        myPeer = null;
        lMsg = null;
        messCoord = null;
    }

}
