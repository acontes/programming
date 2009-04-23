package functionalTests.structuredp2p.canoverlay;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CanMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;


public class TestNeighbors {
    private Peer entryPoint;
    private Peer neighbor;

    @Before
    public void initTest() throws ActiveObjectCreationException, NodeException {
        this.entryPoint = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        this.neighbor = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN }, Deployment.getVirtualNode("StructuredP2P").getANode());
    }

    @Test
    public void testJoin() {
        Assert.assertNotNull(this.entryPoint);
        Assert.assertNotNull(this.neighbor);

        this.neighbor.join(this.entryPoint);

        // Test if the new added peer is in the neighbor list
        Assert.assertTrue(this.neighbor.getNeighbors().contains(this.entryPoint));
        Assert.assertTrue(this.entryPoint.getNeighbors().contains(this.neighbor));

        // Test if the new added peer has the good coordinates
        CanMessage msgToEntryPoint = new CanMessage(this.entryPoint.getCoordinates());
        CanMessage msgToNeighbor = new CanMessage(this.neighbor.getCoordinates());

        Assert.assertEquals(((CanResponseMessage) this.neighbor.sendMessageTo(msgToEntryPoint)).getPeer(),
                this.entryPoint);
        Assert.assertEquals(((CanResponseMessage) this.entryPoint.sendMessageTo(msgToNeighbor)).getPeer(),
                this.neighbor);

        // TODO tests with split !
    }

    @Test
    public void testLeave() {
        Assert.assertNotNull(this.entryPoint);
        Assert.assertNotNull(this.neighbor);

        this.neighbor.leave();

        // Test if the leaved peer is no ;ore in the neighbor list
        Assert.assertFalse(this.neighbor.getNeighbors().contains(this.entryPoint));
        Assert.assertFalse(this.entryPoint.getNeighbors().contains(this.neighbor));

        // Test if the leaved peer is no more in the overlay
        CanMessage msgToEntryPoint = new CanMessage(this.entryPoint.getCoordinates());
        CanMessage msgToNeighbor = new CanMessage(this.neighbor.getCoordinates());

        Assert.assertNotEquals(this.neighbor.sendMessageTo(msgToEntryPoint), this.entryPoint);
        Assert.assertNotEquals(this.entryPoint.sendMessageTo(msgToNeighbor), this.neighbor);

        // TODO tests with merge !
    }

    @After
    public void stopTest() {
        PAActiveObject.terminateActiveObject(this.neighbor, true);
        PAActiveObject.terminateActiveObject(this.entryPoint, true);
        Deployment.kill();
    }

}
