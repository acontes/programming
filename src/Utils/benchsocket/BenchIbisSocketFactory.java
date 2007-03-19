/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package benchsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import ibis.util.IbisSocketFactory;


public class BenchIbisSocketFactory extends IbisSocketFactory
    implements BenchFactoryInterface {
    protected static ArrayList<BenchStream> streamList = new ArrayList<BenchStream>();

    public void addStream(BenchStream s) {
        synchronized (streamList) {
            streamList.add(s);
        }
    }

    public static void dumpStreamIntermediateResults() {
        synchronized (streamList) {
            Iterator<BenchStream> it = streamList.iterator();
            while (it.hasNext()) {
                it.next().dumpIntermediateResults();
            }
        }
    }

    @Override
    public Socket accept(ServerSocket a) throws IOException {
        Socket s = a.accept();
        return s;
    }

    @Override
    public void close(InputStream in, OutputStream out, Socket s) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.flush();
                out.close();
            }
            if (s != null) {
                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int allocLocalPort() {
        return 0;
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog,
        InetAddress addr) throws IOException {
        return new BenchServerSocket(port, addr, this);
    }

    @Override
    public Socket createSocket(InetAddress rAddr, int rPort)
        throws IOException {
        return new BenchClientSocket(rAddr, rPort, this);
    }

    @Override
    public Socket createSocket(InetAddress dest, int port, InetAddress localIP,
        long timeoutMillis) throws IOException {
        return new BenchClientSocket(dest, port, this);
    }

    @Override
    public ServerSocket createServerSocket(int port, InetAddress localAddress,
        boolean retry) throws IOException {
        return new BenchServerSocket(port, localAddress, this);
    }
}
