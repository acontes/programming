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
package org.objectweb.proactive.extra.messagerouting.client.dc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;


/**
 * Implementation of {@link DirectConnection}
 * 	using blocking sockets
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class BlockingDirectConnection implements DirectConnection {

    private final Socket socket;

    public BlockingDirectConnection() {
        super();
        this.socket = new Socket();
    }

    public boolean connect(InetSocketAddress remote) throws IOException {
        this.socket.connect(remote);
        // always succeeds
        return true;
    }

    public synchronized void push(Message msg) throws IOException {
        byte[] msgBuf = msg.toByteArray();
        socket.getOutputStream().write(msgBuf, 0, msgBuf.length);
        socket.getOutputStream().flush();
    }

    public void close() throws IOException {
        socket.close();
    }

    public String toString() {
        return socket.toString();
    }

}
