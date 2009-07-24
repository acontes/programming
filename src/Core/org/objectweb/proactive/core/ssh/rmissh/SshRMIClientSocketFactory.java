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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.ssh.rmissh;

import static org.objectweb.proactive.core.ssh.SSH.logger;

import java.io.IOException;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import org.objectweb.proactive.core.ssh.SshProxy;
import org.objectweb.proactive.core.util.GatewaysInfos;
import org.objectweb.proactive.core.util.HostsInfos;
import org.objectweb.proactive.core.util.ProActiveInet;


/**
 * @author The ProActive Team
 */
public class SshRMIClientSocketFactory implements RMIClientSocketFactory, java.io.Serializable {
    String username;
    String hostname;

    public SshRMIClientSocketFactory() {
        this.username = System.getProperty("user.name");
        this.hostname = ProActiveInet.getInstance().getInetAddress().getCanonicalHostName();
    }

    public Socket createSocket(String host, int port) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating a SSH socket for " + host + ":" + port);
        }

        Socket socket = null;
        String gateway = GatewaysInfos.getGatewayName(host);

        if (gateway != null) {

            if (logger.isDebugEnabled()) {
                logger.info("Using the gateway " + gateway + " to contact " + host + ":" + port);
            }

            int gwPort = GatewaysInfos.getGatewayPort(host);
            socket = new SshProxy(gateway, gwPort, host, port);

        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Gateway not used for " + host);
            }
        String realName = HostsInfos.getSecondaryName(host);
            socket = new SshSocket(realName, port);
        }
        return socket;
    }

    @Override
    public boolean equals(Object obj) {
        // the equals method is class based, since all instances are functionally equivalent.
        // We could if needed compare on an instance basic for instance with the host and port
        // Same for hashCode
        return this.getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        HostsInfos.setUserName(hostname, username);
    }
}
