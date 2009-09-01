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
package org.objectweb.proactive.core.remoteobject.rmissh;

import java.io.File;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.remoteobject.rmi.AbstractRmiRemoteObjectFactory;
import org.objectweb.proactive.core.ssh.SshConfig;
import org.objectweb.proactive.core.ssh.SshRMIClientSocketFactory;


public class RmiSshRemoteObjectFactory extends AbstractRmiRemoteObjectFactory {
    final static SshRMIClientSocketFactory sf;

    static {
        SshConfig sshConfig = new SshConfig();
        sshConfig.setTryPlainSocket(PAProperties.PA_RMISSH_TRY_NORMAL_FIRST.isTrue());

        int gcInterval = 10000;
        if (PAProperties.PA_RMISSH_GC_PERIOD.isSet()) {
            gcInterval = PAProperties.PA_RMISSH_GC_PERIOD.getValueAsInt();
        }
        sshConfig.setGcInterval(gcInterval);

        int gcIdleTime = 10000;
        if (PAProperties.PA_RMISSH_GC_IDLETIME.isSet()) {
            gcIdleTime = PAProperties.PA_RMISSH_GC_IDLETIME.getValueAsInt();
        }
        sshConfig.setGcIdleTime(gcIdleTime);

        int connectTimeout = 2000;
        if (PAProperties.PA_RMISSH_CONNECT_TIMEOUT.isSet()) {
            connectTimeout = PAProperties.PA_RMISSH_CONNECT_TIMEOUT.getValueAsInt();
        }
        sshConfig.setConnectTimeout(connectTimeout);

        String knownHostfile = System.getProperty("user.home") + File.separator + ".ssh" + File.separator +
            "know_hosts";
        if (PAProperties.PA_RMISSH_KNOW_HOSTS.isSet()) {
            knownHostfile = PAProperties.PA_RMISSH_KNOW_HOSTS.getValue();
        }
        sshConfig.setKnowHostFile(knownHostfile);

        String keyDir = System.getProperty("user.home") + File.separator + ".ssh" + File.separator;
        if (PAProperties.PA_RMISSH_KEY_DIR.isSet()) {
            keyDir = PAProperties.PA_RMISSH_KEY_DIR.getValue();
        }
        sshConfig.setKeyDir(keyDir);

        sf = new SshRMIClientSocketFactory(sshConfig);
    }

    public RmiSshRemoteObjectFactory() {
        super(Constants.RMISSH_PROTOCOL_IDENTIFIER, RmiSshRemoteObjectImpl.class);
    }

    protected Registry getRegistry(URI url) throws RemoteException {
        return LocateRegistry.getRegistry(url.getHost(), url.getPort(), sf);
    }

}
