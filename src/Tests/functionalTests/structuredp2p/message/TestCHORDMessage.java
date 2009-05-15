package functionalTests.structuredp2p.message;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.messages.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.chord.CHORDLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;


public class TestCHORDMessage {

    private Peer srcPeer;
    private Peer myPeer;
    private LookupMessage lMsg;
    private String id;
    private ResponseMessage response;

    @Before
    public void init() throws ActiveObjectCreationException, NodeException {
        this.srcPeer = (Peer) PAActiveObject
                .newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });

        this.myPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CAN });
        this.id = "55";
        this.lMsg = new CHORDLookupMessage(this.id);
    }

    @After
    public void clean() {
        this.srcPeer = null;
        this.myPeer = null;
        this.lMsg = null;
        this.id = null;
    }

    @Test
    public void testCreate() {
        Assert.assertNotNull("create a new peer", this.srcPeer);
        Assert.assertNotNull("create a new CAN message", this.lMsg);
        Assert.assertNotNull("create a new coordinate table", this.id);
    }

    @Test
    public void testSendMessage() {

        this.response = this.srcPeer.sendMessage(this.lMsg);
        Assert.assertNotNull("the response is not null", this.response);
        // TODO test a chord message
    }

}
