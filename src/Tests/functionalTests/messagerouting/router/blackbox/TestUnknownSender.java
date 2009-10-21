/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package functionalTests.messagerouting.router.blackbox;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage.ErrorType;

import functionalTests.messagerouting.BlackBox;


public class TestUnknownSender extends BlackBox {

    /* - Connect to the router
     * - Send a message to a non existent recipient with a bogus sender
     * - The router will reply with a ERR_MALFORMED_MESSAGE error message
     */

    @Test
    public void testNOK() throws IOException {
        AgentID srcAgentID = new AgentID(ProActiveRandom.nextPosLong());
        AgentID dstAgentID = new AgentID(ProActiveRandom.nextPosLong());
        long msgId = ProActiveRandom.nextPosLong();

        Message message = new DataRequestMessage(srcAgentID, dstAgentID, msgId, null);
        tunnel.write(message.toByteArray());

        // router should reply with a ErrorMessage, code ERR_MALFORMED_MESSAGE
        byte[] resp = tunnel.readMessage();
        ErrorMessage errMsg = new ErrorMessage(resp, 0);
        Assert.assertEquals(errMsg.getErrorType(), ErrorType.ERR_MALFORMED_MESSAGE);
        Assert.assertEquals(errMsg.getRecipient(), srcAgentID);
        // faulty is the dstAgent; this is because on the agent side we should unlock the waiter
        Assert.assertEquals(errMsg.getFaulty(), dstAgentID);

    }

    /* - Connect to the router
     * - Send a message to a non existent recipient
     * - a ERR_UNKNOW_RCPT message is expected in response
     */
    @Test
    public void testOK() throws IOException, MalformedMessageException {
        // Connect
        Message message = new RegistrationRequestMessage(null, ProActiveRandom.nextPosLong(), 0);
        tunnel.write(message.toByteArray());

        byte[] resp = tunnel.readMessage();
        RegistrationReplyMessage reply = (RegistrationReplyMessage) Message.constructMessage(resp, 0);
        AgentID myAgentId = reply.getAgentID();

        // Send a message
        AgentID dstAgentID = new AgentID(ProActiveRandom.nextPosLong());
        long msgId = ProActiveRandom.nextPosLong();

        message = new DataRequestMessage(myAgentId, dstAgentID, msgId, null);
        tunnel.write(message.toByteArray());

        resp = tunnel.readMessage();
        ErrorMessage error = new ErrorMessage(resp, 0);
        Assert.assertEquals(error.getErrorType(), ErrorType.ERR_UNKNOW_RCPT);
        Assert.assertEquals(error.getMessageID(), msgId);
        Assert.assertEquals(error.getSender(), dstAgentID);
        Assert.assertEquals(error.getRecipient(), myAgentId);
    }
}
