package functionalTests.structuredp2p.can;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.ZoneException;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can.RDFQuery;
import org.objectweb.proactive.extensions.structuredp2p.responses.asynchronous.ResponseMessage;


/**
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestMessagesND {

    private Peer firstPeer;
    private Peer secondPeer;
    private Peer thirdPeer;
    private Query lMsg;
    private Coordinate messCoord[];
    private Coordinate minCoord[];
    private Coordinate maxCoord[];
    private ResponseMessage srcResponse;
    private ResponseMessage myResponse;
    private Zone zone;
    private Zone areaSplit1;
    private Zone areaSplit2;
    private CANOverlay can;
    private CANOverlay splitCan1;
    private CANOverlay splitCan2;
    private int dim;

    @Before
    public void init() throws ActiveObjectCreationException, NodeException, ZoneException {
        this.firstPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        this.secondPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        this.thirdPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        this.dim = CANOverlay.NB_DIMENSIONS;
        this.messCoord = new Coordinate[this.dim];

        for (int i = 0; i < this.dim; i++) {
            int random = (int) (Math.random() * (7 - 5)) + 5;
            this.messCoord[i] = new Coordinate("" + random);
        }

        this.minCoord = new Coordinate[this.dim];

        for (int i = 0; i < this.dim; i++) {
            int random = (int) (Math.random() * (9 - 7)) + 7;
            this.minCoord[i] = new Coordinate("" + random);

        }

        this.maxCoord = new Coordinate[this.dim];
        for (int i = 0; i < this.dim; i++) {
            int random = (int) (Math.random() * 4);
            this.maxCoord[i] = new Coordinate("" + random);

        }

        this.zone = new Zone(this.minCoord, this.maxCoord);

        this.can = ((CANOverlay) (this.firstPeer.getStructuredOverlay()));
        this.can.setZone(this.zone);
        this.firstPeer.setStructuredOverlay(this.can);
        this.lMsg = new RDFQuery(this.messCoord);
    }

    @Test
    public void testCreate() {
        Assert.assertNotNull("create a new peer", this.firstPeer);
        Assert.assertNotNull("zone set on the overlay",
                ((CANOverlay) (this.firstPeer.getStructuredOverlay())).getZone().getCoordinatesMin());
        Assert.assertNotNull("get new peer", this.secondPeer);
        Assert.assertNotNull("create a new CAN message", this.lMsg);
        Assert.assertNotNull("create a new coordinate table", this.messCoord);
        Assert.assertNotNull("create a new coordinate table", this.minCoord);
        Assert.assertNotNull("create a new coordinate table", this.maxCoord);
    }

    @Test
    public void testSendMessage() {

        this.srcResponse = this.firstPeer.search(this.lMsg);
        Assert.assertNotNull("the src response is not null", this.srcResponse);
        for (int i = 0; i < this.dim; i++) {
            Assert.assertEquals(i + "th coordinate ok",
                    ((CANOverlay) ((CANLookupResponseMessage) this.srcResponse).getPeer()
                            .getStructuredOverlay()).contains(i, this.messCoord[i]), 0);
        }
    }

    @Test
    public void testJoinAndSendMessage() throws Exception {
        this.secondPeer.join(this.firstPeer);
        // assertTrue("joining neighbor", ((CanOverlay)
        // srcPeer.getStructuredOverlay()).hasNeighbor(myPeer));
        // assertTrue("joining 2", ((CanOverlay)
        // myPeer.getStructuredOverlay()).hasNeighbor(srcPeer));
        // srcPeer.join(secondPeer);
        // method split not yet implemented

        // assertTrue("joining neighbor",
        // ((CanOverlay)srcPeer.getStructuredOverlay()).hasNeighbor(secondPeer));
        // assertTrue("joining 2",((CanOverlay)secondPeer.getStructuredOverlay()).hasNeighbor(srcPeer));

        int randAxe = (int) (Math.random() * this.dim);
        this.minCoord = new Coordinate[this.dim];

        for (int i = 0; i < this.dim; i++) {
            this.minCoord[i] = new Coordinate("9");
        }

        this.maxCoord = new Coordinate[this.dim];
        for (int i = 0; i < this.dim; i++) {
            if (i == randAxe) {
                this.maxCoord[i] = new Coordinate("4");
            } else {
                this.maxCoord[i] = new Coordinate("0");
            }
        }

        this.areaSplit1 = new Zone(this.minCoord, this.maxCoord);
        //
        this.minCoord = new Coordinate[this.dim];
        for (int i = 0; i < this.dim; i++) {
            if (i == randAxe) {
                this.minCoord[i] = new Coordinate("4");
            } else {
                this.minCoord[i] = new Coordinate("9");
            }
        }

        this.maxCoord = new Coordinate[this.dim];
        for (int i = 0; i < this.dim; i++) {
            this.maxCoord[i] = new Coordinate("0");
        }

        this.areaSplit2 = new Zone(this.minCoord, this.maxCoord);
        //
        this.splitCan1 = ((CANOverlay) (this.firstPeer.getStructuredOverlay()));
        this.splitCan1.setZone(this.areaSplit1);

        this.splitCan2 = ((CANOverlay) (this.secondPeer.getStructuredOverlay()));
        this.splitCan2.setZone(this.areaSplit2);

        this.firstPeer.setStructuredOverlay(this.splitCan1);
        this.secondPeer.setStructuredOverlay(this.splitCan2);

        this.myResponse = this.firstPeer.sendMessage(this.lMsg);
        this.srcResponse = this.secondPeer.sendTo(this.firstPeer, new PingMessage());
        Assert.assertNotNull("the src response is not null", this.myResponse);

        for (int i = 0; i < this.dim; i++) {
            Assert.assertEquals(i + "th coordinate ok",
                    ((CANOverlay) ((CANLookupResponseMessage) this.myResponse).getPeer()
                            .getStructuredOverlay()).contains(i, this.messCoord[i]), 0);

        }

    }

    @After
    public void clean() {
        this.firstPeer = null;
        this.secondPeer = null;
        this.lMsg = null;
        this.messCoord = null;
    }

}