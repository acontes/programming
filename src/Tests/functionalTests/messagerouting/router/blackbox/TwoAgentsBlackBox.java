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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.messagerouting.client.Tunnel;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationRequestMessage;

import functionalTests.messagerouting.BlackBox;


/**
 * A Black Box with a Router and two registered Agents
 *
 * It is assumed(but not verified) that TestConnection passes
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class TwoAgentsBlackBox extends BlackBox {

    protected Tunnel r_tunnel;
    protected AgentID agentID;
    protected AgentID r_agentID;

    // send a registration request using the given tunnel.
    // return the AgentID assigned by the router
    private AgentID register(Tunnel t) throws IOException {
        Message message = new RegistrationRequestMessage(null, ProActiveRandom.nextPosLong(), 0);
        t.write(message.toByteArray());

        byte[] resp = t.readMessage();
        RegistrationReplyMessage reply = (RegistrationReplyMessage) Message.constructMessage(resp, 0);
        return reply.getAgentID();
    }

    @Before
    public void startInfrastructure() throws UnknownHostException, IOException {
        this.r_tunnel = new Tunnel(new Socket(InetAddress.getLocalHost(), this.router.getPort()));
        agentID = register(tunnel);
        r_agentID = register(r_tunnel);
    }

    @After
    public void stopInfrastructure() {
        this.r_tunnel.shutdown();
    }

}
