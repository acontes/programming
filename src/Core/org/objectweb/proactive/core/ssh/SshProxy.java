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
package org.objectweb.proactive.core.ssh;

import static org.objectweb.proactive.core.ssh.SSH.logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.ProActiveInet;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;


/**
 * Implement the OpenSSH proxyCommand behavior
 */
public class SshProxy extends Socket {

    private String FORWARDER_COMMAND = "nc";
    private String PROXY_COMMAND;
    private String SSHCLIENTCOMMAND = "ssh";

    private String gateway = null;

    private Connection connection = null;

    private OutputStream output = null;
    private InputStream input = null;
    private Session sess = null;

    /**
     * Create a socket which use a proxycommand to contact the host
     * 
     * @param distantHost
     *            The wanted host to connect to
     * @param distantPort
     *            The wanted port to connect on the host
     * @see SshHelper             
     * @throws IOException
     */
    public SshProxy(String hostname, int port) throws IOException {

        this.PROXY_COMMAND = FORWARDER_COMMAND + " " + hostname + " " + port;

        SshHelper sshHlp = SshHelper.getInstance();

        this.gateway = sshHlp.getGatewayName(hostname);
        int gwPort = Integer.parseInt(sshHlp.getGatewayPort(hostname));

        buildProxyCommand(hostname, port, gateway);

        // Reach the username to connect to the gateway
        String username = sshHlp.getGatewayUsername(gateway);

        if (logger.isDebugEnabled()) {
            logger.debug("Running a ProxyCommand on " + gateway + " to contact " + hostname + ":" + port +
                " " + PROXY_COMMAND);
            logger.debug("Create SSH Connection from " + ProActiveInet.getInstance().getInetAddress() +
                " to " + gateway + ":" + gwPort);
        }

        // Create the ssh connection
        SshConfig sshConfig = new SshConfig();
        SSHKeys keys = new SSHKeys(sshConfig.getKeyDir());

        SshConnection connection = new SshConnection(username, gateway, gwPort, keys.getKeys());

        this.sess = connection.getTrileadConnection().openSession();
        // Session open, ready to "talk"
        this.sess.execCommand(PROXY_COMMAND);
        // Proxy Command executed
        this.output = sess.getStdin();
        this.input = sess.getStdout();
    }

    private void buildProxyCommand(String hostname, int port, String gateway) {

        String localHostname = "";
        try {
            localHostname = (ProActiveInet.getInstance().getInetAddress().getCanonicalHostName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String OutGW = PAProperties.PA_RMISSH_PROXY_OUT_CONN.getValue();
        if (OutGW != null) {
            String[] OutGWs = OutGW.split(":");
            this.gateway = OutGWs[0];
            // Remote shell command specification
            if (OutGWs.length == 2)
                this.SSHCLIENTCOMMAND = OutGWs[1];

            if (logger.isDebugEnabled()) {
                logger.debug(localHostname + " need " + this.gateway + " to relay outgoing connexion");
            }
            this.PROXY_COMMAND = SSHCLIENTCOMMAND + " " + gateway + " " + FORWARDER_COMMAND + " " + hostname +
                " " + port;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(ProActiveInet.getInstance().getHostname() + " can open outgoing connexion");
            }
            this.gateway = gateway;
            this.PROXY_COMMAND = FORWARDER_COMMAND + " " + hostname + " " + port;
        }
    }

    @Override
    public synchronized void close() throws IOException {
        /* The following flush() is only needed if you wrap the */
        /* stdin stream (e.g., with a BufferedOutputStream). */
        this.output.flush();
        /* Now let's send EOF */
        this.input.close();
        this.output.close();
        /* Let's wait until cat has finished */
        // this.sess.waitForCondition(ChannelCondition.EXIT_STATUS, 50);
        this.sess.waitForCondition(ChannelCondition.EOF, 1);

        /* Now its hopefully safe to close the session */
        this.sess.close();
        if (this.connection != null)
            this.connection.close();

        /* Show exit status, if available (otherwise "null") */
        if (logger.isDebugEnabled())
            logger.debug("Proxy exit code : " + sess.getExitStatus());

        this.input = null;
        this.output = null;
        if (logger.isDebugEnabled())
            logger.debug("Proxy closed.");
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.input;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.output;
    }

}
