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
package unitTests.messagerouting.dc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.Ignore;
import org.objectweb.proactive.extra.messagerouting.client.Tunnel;


/**
 * Test tunnel. It replaces the read/write operations exported by the Tunnel
 * with phony ones
 */
@Ignore
public class TestTunnel extends Tunnel {

    public TestTunnel(InetAddress routerAddr, int routerPort) throws IOException {
        super(new Socket(routerAddr, routerPort));
    }

    public TestTunnel(Socket socket) throws IOException {
        super(socket);
    }

    /* read-write operations do nothing */
    synchronized public void write(byte[] buf) throws IOException {
    }

    synchronized public void write(byte[] buf, long timeout) throws IOException {
    }

    synchronized public void write(byte[] buf, int offset, int length, long timeout) throws IOException {
    }

    public void read(byte[] buf) throws IOException {
    }

    public void read(byte[] buf, long timeout) throws IOException {
    }

    public void read(byte[] buf, int offset, int length, long timeout) throws IOException {
    }

    public byte[] readMessage() throws IOException {
        // called by MessageReader. block its thread
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        return new byte[0];
    }
}
