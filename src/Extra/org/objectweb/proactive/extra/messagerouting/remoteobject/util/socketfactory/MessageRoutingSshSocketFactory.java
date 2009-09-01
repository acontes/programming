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
package org.objectweb.proactive.extra.messagerouting.remoteobject.util.socketfactory;

import java.io.IOException;
import java.net.Socket;

import org.objectweb.proactive.core.ssh.SshConfig;
import org.objectweb.proactive.core.ssh.SshRMIClientSocketFactory;
import org.objectweb.proactive.core.ssh.SshTunnelPool;


/**
 * This implementation for message routing socket factory
 * offers secure SSH sockets
 */
public class MessageRoutingSshSocketFactory implements MessageRoutingSocketFactorySPI {

    final private SshConfig config;
    final private SshTunnelPool tp;

    public MessageRoutingSshSocketFactory() {
        this.config = new SshConfig();

        // No plain socket, we want to use SSH !
        this.config.setTryPlainSocket(false);

        this.config.setConnectTimeout(10000);

        // GC is useless, since only one tunnel to the router is opened
        this.config.setGcIdleTime(60000);
        this.config.setGcInterval(60000);

        this.tp = new SshTunnelPool(this.config);
    }

    public Socket createSocket(String host, int port) throws IOException {
        return tp.getSocket(host, port);
    }

    public String getAlias() {
        return "ssh";
    }
}
