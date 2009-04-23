package functionalTests.structuredp2p.canoverlay;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.CanMessage;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;


public class TestNeighbors {
    private Peer entrypoint;
    private Peer neighbor;

    @Before
    public void initTest() throws ActiveObjectCreationException, NodeException {
        this.entrypoint = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        this.neighbor = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN }, Deployment.getVirtualNode("StructuredP2P").getANode());
    }

    @Test
    public void testAddNeighbors() {
        this.neighbor.join(this.entrypoint);

        CanMessage msgToEntryPoint = new CanMessage(this.entrypoint.getCoordinate());
        CanMessage msgToNeighbor = new CanMessage(this.neighbor.getCoordinate());

        Assert.assertEquals(this.neighbor.sendMessageTo(msgToEntryPoint), this.entrypoint);
        Assert.assertEquals(this.entrypoint.sendMessageTo(msgToNeighbor), this.neighbor);
    }
}
