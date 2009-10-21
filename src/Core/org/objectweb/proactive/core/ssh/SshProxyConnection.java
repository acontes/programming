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

import com.trilead.ssh2.Session;


/** 
 * Implement the OpenSSH proxyCommand behavior
 */
public class SshProxyConnection extends SshConnection {

    final private static String FORWARDER_COMMAND = "nc";
    final private static String SSHCLIENTCOMMAND = "ssh";

    private String proxyCommandTemplate = "";

    /**
     * Because special processing is needed before constructor call
     * 
     * @param hostGW
     *          The gateway to use for contacting the host (if needed)
     * @param localhostGW
     *          The gateway to use to relay outgoing connection (if needed)
     * @param config
     *          The information about the ssh connection
     * @param keys
     *          All the private keys to use 
     * 
     * @return
     *          A Ssh Connection which can provide proxyCommand sessions 
     *          
     * @throws IOException
     */
    public static SshProxyConnection getInstance(String gateway, String outGateway, SshConfig config)
            throws IOException {
        String username;
        int port;
        String proxyCommand = FORWARDER_COMMAND + " " + "%h" + " " + "%p";
        String hostname = null;

        if (outGateway != null) {
            hostname = outGateway;
            if (gateway != null) {
                String user = config.getUsername(gateway);
                proxyCommand = SSHCLIENTCOMMAND + " " + user + "@" + gateway + " " + FORWARDER_COMMAND + " " +
                    "%h" + " " + "%p";
            }
        } else {
            if (gateway != null) {
                hostname = gateway;
            } else {
                return null;
            }
        }

        username = config.getUsername(hostname);
        port = config.getPort(hostname);
        String key[] = { config.getPrivateKeyPath(hostname) };
        return new SshProxyConnection(username, hostname, port, key, proxyCommand);
    }

    private SshProxyConnection(String username, String hostname, int port, String[] keys, String proxyCommand)
            throws IOException {
        super(username, hostname, port, keys);
        this.proxyCommandTemplate = proxyCommand;
    }

    /**
     * Return an Ssh Session for contacting host on port which use the 
     *     proxyCommand mechanism
     * 
     * @param host
     *          the machine to contact
     * @param port
     *          the port to use
     */
    public SshProxySession getSession(String host, int port) throws IOException {
        String proxyCommand = proxyCommandTemplate;
        proxyCommand = proxyCommand.replace("%h", host);
        proxyCommand = proxyCommand.replace("%p", Integer.toString(port));
        Session sess = this.getTrileadConnection().openSession();
        sess.execCommand(proxyCommand);
        return new SshProxySession(sess);
    }
}
