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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.eclipse.proactive.extendeddebugger.core.tunneling;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;



public class DebuggerSocketServer extends AbstractDebuggerSocket {
    private static final long serialVersionUID = 8136088432502063948L;

    /** The socket server */
    protected ServerSocket serverSocket;

    public DebuggerSocketServer() {
    }

    /**
     * @see org.eclipse.proactive.extendeddebugger.core.tunneling.ic2d.debug.connection.AbstractDebuggerSocket#setTarget(AbstractDebuggerSocket)
     *
     * @param target
     *            The target (must be a {@link DebuggerSocketClient})
     */
    public void setTarget(AbstractDebuggerSocket target) {
        if (target instanceof DebuggerSocketClient) {
            super.setTarget(target);
        } else {
            throw new UnsupportedOperationException("Target must be a DebuggerSocketClient");
        }
    }

    /**
     * @see org.eclipse.proactive.extendeddebugger.core.tunneling.ic2d.debug.connection.AbstractDebuggerSocket#handshake(Socket)
     */
    public void handshake(Socket socket) throws IOException {
        Data handshake = new Data(14);
        socket.getInputStream().read(new byte[14], 0, 14);
        handshake.read("JDWP-Handshake");
        handshake.write(socket.getOutputStream());
        socket.getOutputStream().flush();
    }

    /**
     * Connect the server socket and wait for an external debugger connection
     */
    public void connect() {
        new Thread() {
            public void run() {
                try {
                    DebuggerSocketClient client = (DebuggerSocketClient) target;
                    Socket socket;
                    serverSocket = new ServerSocket(port);
                    port = serverSocket.getLocalPort();
                    if ((socket = serverSocket.accept()) != null) {
                        addConnection(socket);
                        client.connect();
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * @see org.eclipse.proactive.extendeddebugger.core.tunneling.ic2d.debug.connection.AbstractDebuggerSocket#closeConnection()
     */
    @Override
    public void closeConnection() {
        super.closeConnection();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
