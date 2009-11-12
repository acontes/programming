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

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.TypeHelper;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage.ErrorType;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/**
 * Testing the {@link MessageType#ERR_} messages 
 */
public class TestErrorMessage extends MessageFunctionalTest {

    @Test
    public void test() {
        ErrorMessageGenerator msgGen = new ErrorMessageGenerator();
        try {
            for (int i = 0; i < NB_CHECK; i++) {
                msgGen.buildValidMessage();
                msgGen.testFields();
                msgGen.testConversion();
                msgGen.testInvalidMessage();
            }
        } catch (MalformedMessageException e) {
            Assert.fail("There is a problem in the " + MessageType.ERR_ + " implementation:" +
                " the message " + msgGen.getMessage() + " cannot be reconstructed from its " +
                "raw byte form, because:" + e.getMessage());
        }
    }

    private static class ErrorMessageGenerator extends MessageGenerator {

        private AgentID recipient;
        private AgentID faulty;
        private long msgId;
        private ErrorType error;
        // implementation details
        // - the ErrorMessage is a DataMessage with the error code in the payload 
        private static final int ERROR_OFFSET = Message.Field.getTotalOffset() +
            DataMessage.Field.getTotalOffset();
        // - the total length of an Error Message
        private static final int ERROR_MESSAGE_LEN = ERROR_OFFSET + 4;

        public ErrorMessageGenerator() {
            super(MessageType.ERR_);
        }

        @Override
        protected void buildValidMessage() {
            recipient = new AgentID(ProActiveRandom.nextPosLong());
            logger.debug("recipient " + recipient);
            faulty = new AgentID(ProActiveRandom.nextPosLong());
            logger.debug("faulty " + faulty);
            msgId = ProActiveRandom.nextPosLong();
            logger.debug("msgId " + msgId);
            int errorCode = ProActiveRandom.nextInt(ErrorType.values().length);
            error = ErrorType.getErrorType(errorCode);
            logger.debug("error code:" + error);
            this.msg = new ErrorMessage(error, recipient, faulty, msgId);
        }

        @Override
        protected void testFields() {
            ErrorMessage m = (ErrorMessage) this.msg;
            Assert.assertEquals(Message.PROTOV11, m.getProtoID());
            Assert.assertEquals(type, m.getType());
            Assert.assertEquals(msgId, m.getMessageID());
            Assert.assertEquals(recipient, m.getRecipient());
            Assert.assertEquals(faulty, m.getFaulty());
            Assert.assertEquals(error, m.getErrorType());
            Assert.assertEquals(m.getLength(), ERROR_MESSAGE_LEN);
        }

        @Override
        protected void testConversion() throws MalformedMessageException {
            ErrorMessage m = (ErrorMessage) this.msg;
            byte[] buf = m.toByteArray();
            Assert.assertEquals(buf.length, m.getLength());

            ErrorMessage convMsg = new ErrorMessage(buf, 0);
            Assert.assertEquals(m.getLength(), convMsg.getLength());
            Assert.assertEquals(m.getProtoID(), convMsg.getProtoID());
            Assert.assertEquals(m.getType(), convMsg.getType());
            Assert.assertEquals(m.getMessageID(), convMsg.getMessageID());
            Assert.assertEquals(m.getFaulty(), convMsg.getFaulty());
            Assert.assertEquals(m.getRecipient(), convMsg.getRecipient());
            Assert.assertEquals(m.getErrorType(), convMsg.getErrorType());

        }

        @Override
        protected void testInvalidMessage() {
            super.testInvalidMessage();

            try {
                int invalidErrorCode = nextIntGT(ErrorType.values().length);
                logger.debug("Invalid error code:" + invalidErrorCode);
                ErrorMessage errMsg = corruptErrorCode(invalidErrorCode, ERROR_OFFSET);
                Assert.fail("Problem with " + type + " implementation: Attemp to reconstruct the message " +
                    errMsg + " with a corrupted error code field " +
                    " from its raw byte array representation actually succeeded!");
            } catch (MalformedMessageException e) {
                // success
            }
        }

        private ErrorMessage corruptErrorCode(int invalidErrorCode, int offset)
                throws MalformedMessageException {
            byte[] corruptedMsg = msg.toByteArray();
            TypeHelper.intToByteArray(invalidErrorCode, corruptedMsg, offset);
            return new ErrorMessage(corruptedMsg, 0);
        }

        private final int nextIntGT(int length) {
            return length + ProActiveRandom.nextInt(Integer.MAX_VALUE - length);
        }
    }
}
