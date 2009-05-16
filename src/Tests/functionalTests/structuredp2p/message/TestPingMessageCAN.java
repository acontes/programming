package functionalTests.structuredp2p.message;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.messages.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;

/**
 * Test {@link PingMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestPingMessageCAN {

	private Peer senderPeer;
	private Peer receiverPeer;
	private Message msg;
	private ResponseMessage senderResponse;
	private ResponseMessage receiverResponse;

	@Before
	public void setUp() throws ActiveObjectCreationException, NodeException {
		this.senderPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
				new Object[] { OverlayType.CAN });

		this.receiverPeer = (Peer) PAActiveObject.newActive(Peer.class
				.getName(), new Object[] { OverlayType.CAN });

		this.msg = new PingMessage();
	}

	@Test
	public void testCreate() {
		Assert.assertNotNull(this.senderPeer);
		Assert.assertNotNull(this.receiverPeer);
		Assert.assertNotNull(this.msg);
	}

	@Test
	public void testSendMessageTo() {
		this.senderResponse = this.senderPeer.sendMessageTo(this.receiverPeer,
				this.msg);
		this.receiverResponse = this.receiverPeer.sendMessageTo(
				this.senderPeer, this.msg);

		Assert.assertTrue(this.senderResponse.getLatency() >= 0);
		Assert.assertTrue(this.receiverResponse.getLatency() >= 0);
	}

	@After
	public void tearDown() {
		this.msg = null;
		this.receiverResponse = null;
		this.senderResponse = null;
		PAActiveObject.terminateActiveObject(this.senderPeer, false);
		PAActiveObject.terminateActiveObject(this.receiverPeer, false);
	}

}
