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
package functionalTests.messagerouting.router.blackbox;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage.ErrorType;

import functionalTests.messagerouting.BlackBoxRegistered;


/**
 * Tests how the router reacts while
 * receiving a corrupted Direct Connection Request
 *
 */
public class TestDirectConnectionCorruption extends BlackBoxRegistered {

    @Test
    public void test() throws IOException {
        long invalidID = -ProActiveRandom.nextPosLong();
        AgentID invalidAgentID = new AgentID(invalidID);
        long msgId = ProActiveRandom.nextPosLong();

        DirectConnectionRequestMessage msg = new DirectConnectionRequestMessage(msgId, this.agentId,
            invalidAgentID);
        tunnel.write(msg.toByteArray());

        byte[] resp = tunnel.readMessage();
        // expect to get ERR_MALFORMED_MESSAGE
        ErrorMessage err = new ErrorMessage(resp, 0);
        Assert.assertEquals(ErrorType.ERR_MALFORMED_MESSAGE, err.getErrorType());
        Assert.assertEquals(msgId, err.getMessageID());
        Assert.assertEquals(this.agentId, err.getRecipient());
    }
}
