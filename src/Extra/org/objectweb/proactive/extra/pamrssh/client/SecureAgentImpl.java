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
package org.objectweb.proactive.extra.pamrssh.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ssh.rmissh.SshSocket;
import org.objectweb.proactive.extra.messagerouting.client.AbstractAgent;
import org.objectweb.proactive.extra.messagerouting.client.MessageHandler;
import org.objectweb.proactive.extra.messagerouting.client.Tunnel;
import org.objectweb.proactive.extra.messagerouting.client.Valve;


/**
 * An {@link Agent} implementation using a secured tunnel connection
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class SecureAgentImpl extends AbstractAgent {

    /**
     * Create a secured routing agent
     * 
     * The router must be available when the constructor is called.
     * 
     * @param routerAddr
     *            Address of the router
     * @param routerPort
     *            TCP port on which the router listen
     * @param messageHandlerClass
     *            Class the will handled received message
     * @throws ProActiveException
     *             If the router cannot be contacted.
     */
    public SecureAgentImpl(InetAddress routerAddr, int routerPort,
            Class<? extends MessageHandler> messageHandlerClass) throws ProActiveException {
        this(routerAddr, routerPort, messageHandlerClass, new ArrayList<Valve>());
    }

    /**
     * Create a secured routing agent
     * This constructor just invokes the superconstructor;
     * the actual secured tunnel creation is handled by overriding getTunnel().
     * 
     * The router must be available when the constructor is called.
     * 
     * @param routerAddr
     *            Address of the router
     * @param routerPort
     *            TCP port on which the router listen
     * @param messageHandlerClass
     *            Class the will handled received message
     * @param valves
     *            List of {@link Valve} to be applied to all incomming and
     *            outgoing messages.
     * @throws ProActiveException
     *             If the router cannot be contacted.
     */
    public SecureAgentImpl(InetAddress routerAddr, int routerPort,
            Class<? extends MessageHandler> messageHandlerClass, List<Valve> valves)
            throws ProActiveException {
        super(routerAddr, routerPort, messageHandlerClass, valves);
    }

    @Override
    protected Tunnel createTunnel() throws IOException {
        Socket secureSocket = new SshSocket(routerAddr.getHostAddress(), routerPort);
        return new Tunnel(secureSocket);
    }

    @Override
    protected String getMBeanName() {
        return "org.objectweb.proactive.extra.pamrssh:type=SecureAgentImpl,name=" + this.agentID;
    }

}
