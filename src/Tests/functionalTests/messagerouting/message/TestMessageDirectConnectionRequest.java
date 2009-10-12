/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package functionalTests.messagerouting.message;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.TypeHelper;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionRequestMessage.Field;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;

/**
 * Test for the {@link MessageType#DIRECT_CONNECTION_REQUEST} message
 */
public class TestMessageDirectConnectionRequest extends MessageFunctionalTest {

	@Test
	public void test() {

		DirectConnectionRequestGenerator msgGen =
			new DirectConnectionRequestGenerator();
		try{
			for (int i = 0; i < NB_CHECK; i++) {
				msgGen.buildValidMessage();
				msgGen.testFields();
				msgGen.testConversion();
				msgGen.testInvalidMessage();
			}
		} catch (MalformedMessageException e) {
			Assert.fail("There is a problem in the " + MessageType.DIRECT_CONNECTION_REQUEST + " implementation:" +
					" the message " + msgGen.getMessage() + " cannot be reconstructed from its " +
					"raw byte form, because:" + e.getMessage());
		}

	}

	private class DirectConnectionRequestGenerator extends MessageGenerator {

		private long msgId;
		private AgentID agent;
		private AgentID remoteAgent;

		public DirectConnectionRequestGenerator(MessageType type) {
			super(type);
		}

		public DirectConnectionRequestGenerator() {
			this(MessageType.DIRECT_CONNECTION_REQUEST);
		}

		@Override
		protected void buildValidMessage() {
			agent = new AgentID(ProActiveRandom.nextPosLong());
            logger.debug("agent " + agent);
            msgId = ProActiveRandom.nextPosLong();
            logger.debug("msgId " + msgId);
            remoteAgent = new AgentID(ProActiveRandom.nextPosLong());
            logger.debug("remoteAgent " + remoteAgent);
			msg = new DirectConnectionRequestMessage(msgId, agent, remoteAgent);
		}

		@Override
		protected void testFields() {
			DirectConnectionRequestMessage msg = (DirectConnectionRequestMessage)this.msg;
			Assert.assertEquals(Message.PROTOV1, msg.getProtoID());
	        Assert.assertEquals(MessageType.DIRECT_CONNECTION_REQUEST, msg.getType());
	        Assert.assertEquals(msgId, msg.getMessageID());
	        Assert.assertEquals(agent, msg.getAgentID());
	        Assert.assertEquals(remoteAgent, msg.getRemoteAgentID());
		}

		@Override
		protected void testConversion() throws MalformedMessageException {
			byte[] buf = msg.toByteArray();
			DirectConnectionRequestMessage original = (DirectConnectionRequestMessage)this.msg;
			DirectConnectionRequestMessage convMsg = new DirectConnectionRequestMessage(buf,0);

			Assert.assertEquals(original.getProtoID(), convMsg.getProtoID());
			Assert.assertEquals(original.getLength(), convMsg.getLength());
			Assert.assertEquals(original.getType(), convMsg.getType());
			Assert.assertEquals(original.getMessageID(), convMsg.getMessageID());
			Assert.assertEquals(original.getAgentID(), convMsg.getAgentID());
			Assert.assertEquals(original.getRemoteAgentID(), convMsg.getRemoteAgentID());
		}

		@Override
		protected void testInvalidMessage() {
			super.testInvalidMessage();

			// agentId
            try {
                long invalidAgentId = nextLongLEQ(-1);
                logger.debug("invalid agentId " + invalidAgentId);
                byte[] corruptedMsg = alterAgentId(invalidAgentId, Message.Field.getTotalOffset() +
				Field.AGENT_ID.getOffset());
                DirectConnectionRequestMessage m = new DirectConnectionRequestMessage(corruptedMsg, 0);
                Assert.fail("Problem with " + type + " implementation: Attemp to reconstruct the message " +
                    m + " with a corrupted " + DirectConnectionRequestMessage.Field.AGENT_ID +
                    " from its raw byte array representation actually succeeded!");
            } catch (MalformedMessageException e) {
                // success
            }

            // remoteAgentId
            try {
		long invalidAgentId = nextLongLEQ(-1);
		logger.debug("invalid remoteAgentId " + invalidAgentId);
		byte[] corruptedMsg = alterAgentId(invalidAgentId, Message.Field.getTotalOffset() +
				Field.REMOTE_AGENT_ID.getOffset());
		DirectConnectionRequestMessage m = new DirectConnectionRequestMessage(corruptedMsg, 0);
		Assert.fail("Problem with " + type + " implementation: Attemp to reconstruct the message " +
				m + " with a corrupted " + DirectConnectionRequestMessage.Field.REMOTE_AGENT_ID +
		" from its raw byte array representation actually succeeded!");
            } catch (MalformedMessageException e) {
		// success
            }

		}

		private byte[] alterAgentId(long invalidAgentId, int offset) {
			byte[] corruptedMsg = msg.toByteArray();
			TypeHelper.longToByteArray(invalidAgentId, corruptedMsg, offset);
			return corruptedMsg;
		}

	}

}
