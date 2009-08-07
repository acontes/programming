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
package org.objectweb.proactive.extra.messagerouting.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.ProActiveException;


/**
 * Implementation of the local message routing client.
 * 
 * 
 * It contacts the router as soon as created and try to maintain the connection
 * open (eg. if the connection is closed then it will be reopened).
 * 
 * @since ProActive 4.1.0
 */
public class AgentImpl extends AbstractAgent {

    /**
     * Create a routing agent
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
    public AgentImpl(InetAddress routerAddr, int routerPort,
            Class<? extends MessageHandler> messageHandlerClass) throws ProActiveException {
        this(routerAddr, routerPort, messageHandlerClass, new ArrayList<Valve>());
    }

    /**
     * Create a routing agent
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
    public AgentImpl(InetAddress routerAddr, int routerPort,
            Class<? extends MessageHandler> messageHandlerClass, List<Valve> valves)
            throws ProActiveException {
        super(routerAddr, routerPort, messageHandlerClass, valves);
    }

    @Override
    protected String getMBeanName() {
        return "org.objectweb.proactive.extra.messagerouting:type=AgentImpl,name=" + this.agentID;
    }

    @Override
    protected Tunnel createTunnel() throws IOException {
        return new Tunnel(this.routerAddr, this.routerPort);
    }
}
