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
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyNACKMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/**
 * Test for the {@link MessageType#DIRECT_CONNECTION_ACK} message
 */
public class TestMessageDirectConnectionReplyNACK extends MessageFunctionalTest {

	@Test
	public void test() {

		DirectConnectionReplyNACKGenerator msgGen = new DirectConnectionReplyNACKGenerator();
		try{
			for (int i = 0; i < NB_CHECK; i++) {
				msgGen.buildValidMessage();
				msgGen.testFields();
				msgGen.testConversion();
				msgGen.testInvalidMessage();
			}
		} catch (MalformedMessageException e) {
			Assert.fail("There is a problem in the " + MessageType.DIRECT_CONNECTION_NACK + " implementation:" +
					" the message " + msgGen.getMessage() + " cannot be reconstructed from its " +
					"raw byte form, because:" + e.getMessage());
		}

	}

	private class DirectConnectionReplyNACKGenerator extends MessageGenerator {

		private long msgId;

		public DirectConnectionReplyNACKGenerator() {
			this(MessageType.DIRECT_CONNECTION_NACK);
		}

		public DirectConnectionReplyNACKGenerator(MessageType type) {
			super(type);
		}

		@Override
		protected void buildValidMessage() {
			msgId = ProActiveRandom.nextPosLong();
			logger.debug("msgId " + msgId);
			msg = new DirectConnectionReplyNACKMessage(msgId);
		}

		@Override
		protected void testFields() {
			Assert.assertEquals(Message.PROTOV1, msg.getProtoID());
	        Assert.assertEquals(MessageType.DIRECT_CONNECTION_NACK, msg.getType());
	        Assert.assertEquals(msgId, msg.getMessageID());
		}

		@Override
		protected void testConversion() throws MalformedMessageException {
			byte[] buf = msg.toByteArray();
			DirectConnectionReplyNACKMessage convMsg = new DirectConnectionReplyNACKMessage(buf,0);

			Assert.assertEquals(msg.getProtoID(), convMsg.getProtoID());
			Assert.assertEquals(msg.getLength(), convMsg.getLength());
			Assert.assertEquals(msg.getType(), convMsg.getType());
			Assert.assertEquals(msg.getMessageID(), convMsg.getMessageID());
		}

	}
}
