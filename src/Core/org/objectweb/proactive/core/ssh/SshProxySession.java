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

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Session;


class SshProxySession extends Socket {

    private Session session;
    private boolean unused;

    private InputStream is;
    private OutputStream os;

    SshProxySession(Session sess) {
        this.session = sess;
        this.is = sess.getStdout();
        this.os = sess.getStdin();
    }

    public Socket getSocket() throws IOException {
        this.unused = false;
        return this;
    }

    public void close() throws IOException {
        this.flush();
        this.session.waitForCondition(ChannelCondition.EOF, 1);
        /* Now its hopefully safe to close the session */
        this.session.close();
        // Socket::close() 
        this.unused = true;
    }

    public boolean isUnused() {
        return this.unused;
    }

    // Socket methods
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
    public String toString() {
        return "Socket[ProxyCommand fake socket connected]";
    }
}
