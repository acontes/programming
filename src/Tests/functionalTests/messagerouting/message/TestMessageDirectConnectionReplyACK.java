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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.protocol.TypeHelper;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyACKMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyACKMessage.Field;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/**
 * Test for the {@link MessageType#DIRECT_CONNECTION_ACK} message
 */
public class TestMessageDirectConnectionReplyACK extends MessageFunctionalTest {

    @Test
    public void test() {

        DirectConnectionReplyACKGenerator msgGen = new DirectConnectionReplyACKGenerator();
        try {
            for (int i = 0; i < NB_CHECK; i++) {
                msgGen.buildValidMessage();
                msgGen.testFields();
                msgGen.testConversion();
            }
        } catch (MalformedMessageException e) {
            Assert.fail("There is a problem in the " + MessageType.DIRECT_CONNECTION_ACK +
                " implementation:" + " the message " + msgGen.getMessage() +
                " cannot be reconstructed from its " + "raw byte form, because:" + e.getMessage());
        }

        msgGen.testInvalidMessage();

    }

    private class DirectConnectionReplyACKGenerator extends MessageGenerator {

        private long msgId;
        private InetAddress ipAddr;
        private int port;

        public DirectConnectionReplyACKGenerator(MessageType type) {
            super(type);
        }

        public DirectConnectionReplyACKGenerator() {
            this(MessageType.DIRECT_CONNECTION_ACK);
        }

        @Override
        protected void buildValidMessage() {
            msgId = ProActiveRandom.nextPosLong();
            logger.debug("msgId " + msgId);
            ipAddr = buildValidInetAddress();
            logger.debug("IP addr " + ipAddr);
            port = ProActiveRandom.nextInt(65535) + 1;
            logger.debug("port " + port);
            msg = new DirectConnectionReplyACKMessage(msgId, ipAddr, port);
        }

        @Override
        protected void testFields() {
            DirectConnectionReplyACKMessage msg = (DirectConnectionReplyACKMessage) this.msg;
            Assert.assertEquals(Message.PROTOV1, msg.getProtoID());
            Assert.assertEquals(MessageType.DIRECT_CONNECTION_ACK, msg.getType());
            Assert.assertEquals(msgId, msg.getMessageID());
            Assert.assertEquals(ipAddr, msg.getInetAddress());
            Assert.assertEquals(port, msg.getPort());
        }

        @Override
        protected void testConversion() throws MalformedMessageException {
            byte[] buf = msg.toByteArray();
            DirectConnectionReplyACKMessage original = (DirectConnectionReplyACKMessage) this.msg;
            DirectConnectionReplyACKMessage convMsg = new DirectConnectionReplyACKMessage(buf, 0);

            Assert.assertEquals(original.getProtoID(), convMsg.getProtoID());
            Assert.assertEquals(original.getLength(), convMsg.getLength());
            Assert.assertEquals(original.getType(), convMsg.getType());
            Assert.assertEquals(original.getMessageID(), convMsg.getMessageID());
            Assert.assertEquals(original.getInetAddress(), convMsg.getInetAddress());
            Assert.assertEquals(original.getPort(), convMsg.getPort());
        }

        @Override
        protected void testInvalidMessage() {
            super.testInvalidMessage();

            // port
            try {
                short invalidPort = 0;
                logger.debug("invalid port " + invalidPort);
                byte[] corruptedMsg = alterPort(invalidPort);
                Message m = new DirectConnectionReplyACKMessage(corruptedMsg, 0);
                Assert.fail("Problem with " + type + " implementation: Attemp to reconstruct the message " +
                    m + " with a corrupted " + DirectConnectionReplyACKMessage.Field.PORT + " field " +
                    " from its raw byte array representation actually succeeded!");
            } catch (MalformedMessageException e) {
                // success
            }

            // ip address
            int[] invalidAddresses = new int[] { 0, // anycast
                    0xe0000000 // multicast
            };
            for (int invalidAddress : invalidAddresses) {
                try {
                    byte[] corruptedMsg = alterIP(invalidAddress);
                    Message m;
                    try {
                        m = new DirectConnectionReplyACKMessage(corruptedMsg, 0);
                        Assert.fail("Problem with " + type +
                            " implementation: Attemp to reconstruct the message " + m + " with a corrupted " +
                            DirectConnectionReplyACKMessage.Field.IP_ADDR + " field " +
                            " from its raw byte array representation actually succeeded!");
                    } catch (MalformedMessageException e) {
                        // success
                    }
                } catch (UnknownHostException e) {
                    logger.warn("Cannot generate IP address from int " + invalidAddress + ",skipping.", e);
                }
            }

        }

        private byte[] alterIP(int invalidAddress) throws UnknownHostException {
            byte[] ipBytes = new byte[4];
            TypeHelper.intToByteArray(invalidAddress, ipBytes, 0);
            Inet4Address inetAddr = (Inet4Address) Inet4Address.getByAddress(ipBytes);
            logger.debug("invalid IP " + inetAddr);
            byte[] corrupted = msg.toByteArray();
            TypeHelper.inetAddrToByteArray(inetAddr, corrupted, Message.Field.getTotalOffset() +
                Field.IP_ADDR.getOffset());
            return corrupted;
        }

        private byte[] alterPort(short invalidPort) {
            byte[] corrupted = msg.toByteArray();
            TypeHelper.shortToByteArray(invalidPort, corrupted, Message.Field.getTotalOffset() +
                Field.PORT.getOffset());
            return corrupted;
        }

        private InetAddress buildValidInetAddress() {
            while (true) {
                int candidate = ProActiveRandom.nextInt();
                if ((candidate & 0xf0000000) == 0xe0000000)
                    // multicast
                    continue;
                if (candidate == 0)
                    // anycast
                    continue;
                byte[] ipBytes = new byte[4];
                TypeHelper.intToByteArray(candidate, ipBytes, 0);
                try {
                    return (Inet4Address) Inet4Address.getByAddress(ipBytes);
                } catch (Exception e) {
                    // fail directly, as there is a problem with getByAddress()
                    Assert.fail("Exception occured while generating test IP addresses:" + e.getMessage());
                    return null;
                }
            }
        }

    }

}
