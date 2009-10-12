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
import java.net.InetAddress;
import java.net.Socket;


/**
 * Manages a single direct connection with a remote agent
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnection {

    private final Socket socket;

    public DirectConnection(InetAddress addr, int port) throws IOException {
        this(new Socket(addr, port));
    }

    public DirectConnection(Socket socket) throws IOException {
        this.socket = socket;
    }

    synchronized public void push(byte[] msgBuf) throws IOException {
        socket.getOutputStream().write(msgBuf, 0, msgBuf.length);
        socket.getOutputStream().flush();
    }

    @Override
    public String toString() {
        return socket.toString();
    }

}
