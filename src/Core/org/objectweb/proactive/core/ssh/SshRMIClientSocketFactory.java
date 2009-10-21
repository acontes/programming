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
 * $$ACTIVEEON_CONTRIBUTOR$$
 */
package org.objectweb.proactive.core.ssh;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;


public class SshRMIClientSocketFactory implements RMIClientSocketFactory, Serializable {

    static final private SshTunnelPool tunnelPool = new SshTunnelPool();
    static SshConfig sshConfig;
    private String specificGatewayRule = "";
    private transient boolean isChecked = false;

    public SshRMIClientSocketFactory(SshConfig config) {
        this.isChecked = true;
        sshConfig = config;
        tunnelPool.setSshConfig(sshConfig);
        tunnelPool.createAndStartGCThread();
    }

    public Socket createSocket(String host, int port) throws IOException {
        if (specificGatewayRule.isEmpty()) {
            specificGatewayRule = sshConfig.getRule(host);
        } else {
            if (!this.isChecked) {
                SshConfigFileParser parser = new SshConfigFileParser();
                parser.parseProperties(specificGatewayRule, sshConfig);
                this.isChecked = true;
            }
        }
        return tunnelPool.getSocket(host, port);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }
}
