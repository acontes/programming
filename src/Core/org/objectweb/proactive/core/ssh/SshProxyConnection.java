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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Session;


/** 
 * Implement the OpenSSH proxyCommand behavior
 */
public class SshProxyConnection extends SshConnection {

    final private static String FORWARDER_COMMAND = "nc";
    final private static String SSHCLIENTCOMMAND = "ssh";

    private String proxyCommandTemplate = "";

    /**
     * Because special processing is needed before parent call
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
    public static SshConnection getInstance(String hostGW, String localhostGW, SshConfigStorer config,
            String[] keys) throws IOException {
        String username;
        int port;
        String proxyCommand = "";
        String hostname = null;

        if (localhostGW != null) {
            hostname = localhostGW;
            proxyCommand = FORWARDER_COMMAND + " " + "%h" + " " + "%p";
            if (hostGW != null) {
                String user = config.getUsername(hostGW);
                proxyCommand = SSHCLIENTCOMMAND + " " + user + "@" + hostGW + " " + FORWARDER_COMMAND + " " +
                    "%h" + " " + "%p";
            }
        } else {
            proxyCommand = FORWARDER_COMMAND + " " + "%h" + " " + "%p";
            hostname = hostGW;
        }
        username = config.getUsername(hostname);
        port = config.getPort(hostname);
        return new SshProxyConnection(username, hostname, port, keys, proxyCommand);
    }

    private SshProxyConnection(String username, String hostname, int port, String[] keys, String proxyCommand)
            throws IOException {
        super(username, hostname, port, keys);
        this.proxyCommandTemplate = proxyCommand;
    }

    /**
     * Return an Ssh Tunnel for contacting host on port which use the 
     *     proxyCommand mechanism
     * 
     * @param host
     *          the machine to contact
     * @param port
     *          the port to use
     */
    public SshTunnelStateFullInterface getSession(String host, int port) {
        String proxyCommand = proxyCommandTemplate;
        proxyCommand = proxyCommand.replace("%h", host);
        proxyCommand = proxyCommand.replace("%p", Integer.toString(port));
        try {
            Session sess = this.getTrileadConnection().openSession();
            System.out.println(proxyCommand);
            sess.execCommand(proxyCommand);
            return new SshTunnelProxy(sess, host, port);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class SshTunnelProxy implements SshTunnelStateFullInterface {

        /** number of currently open sockets */
        final private AtomicInteger users = new AtomicInteger();
        /** If users == 0, the timestamp of the last call to close() */
        final private AtomicLong unusedSince = new AtomicLong();

        private Session session;
        private String host;
        private int port;
        private FakeSocket sock;

        SshTunnelProxy(Session sess, String host, int port) {
            this.session = sess;
            this.host = host;
            this.port = port;
            this.sock = new FakeSocket(session.getStdout(), session.getStdin());
        }

        public Socket getSocket() throws IOException {
            this.users.incrementAndGet();
            return sock;
        }

        public void close() throws IOException {
            unusedSince.set(System.currentTimeMillis());
            users.decrementAndGet();

            this.sock.flush();
            this.session.waitForCondition(ChannelCondition.EOF, 1);
            this.sock.close();
            /* Now its hopefully safe to close the session */
            this.session.close();
        }

        public String getDistantHost() {
            return host;
        }

        public int getDistantPort() {
            return port;
        }

        public long unusedSince() {
            if (this.users.get() == 0) {
                return this.unusedSince.get();
            } else {
                return Long.MAX_VALUE;
            }
        }
    }

    /**
     * Expose the proxyCommand session streams as a socket
     */
    private class FakeSocket extends Socket {
        private InputStream is;
        private OutputStream os;

        public FakeSocket(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.is;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return this.os;
        }

        public void flush() throws IOException {
            this.os.flush();
        }

        @Override
        public void close() throws IOException {
            this.is.close();
            this.os.close();
        }

        @Override
        public String toString() {
            return "Socket[ProxyCommand fake socket connected]";
        }
    }
}
