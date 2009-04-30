package functionalTests.structuredp2p.message;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.PingResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;

/**
 * Test {@link PingMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestPingMessage {
	private Peer senderPeer;
	private Peer receiverPeer;
	private Message msg;
	private ResponseMessage senderResponse;
	private ResponseMessage receiverResponse;

	@Before
	public void init() throws ActiveObjectCreationException, NodeException {
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
		this.senderResponse = senderPeer.sendMessageTo(receiverPeer, msg);
		this.receiverResponse = receiverPeer.sendMessageTo(senderPeer, msg);

		Assert.assertTrue(((PingResponseMessage) PAFuture
				.getFutureValue(senderResponse)).getLatency() >= 0);
		Assert.assertTrue(((PingResponseMessage) PAFuture
				.getFutureValue(receiverResponse)).getLatency() >= 0);
	}

	@After
	public void clean() {
		this.senderPeer = null;
		this.receiverPeer = null;
		this.msg = null;
	}

}
