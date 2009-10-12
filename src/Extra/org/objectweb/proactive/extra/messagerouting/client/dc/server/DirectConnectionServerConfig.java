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
package org.objectweb.proactive.extra.messagerouting.client.dc.server;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Configuration options for the direct connection server
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionServerConfig {

    private int port;

    private int nbWorkerThreads;

    private InetAddress inetAddress;

    public DirectConnectionServerConfig(int port) throws UnknownHostException {
        this(port, 4, InetAddress.getLocalHost());
    }

    public DirectConnectionServerConfig(int port, int nbWorkerThreads, InetAddress inetAddress) {

        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port value:" + port);

        if (nbWorkerThreads <= 0)
            throw new IllegalArgumentException("Invalid number of worker threads: " + nbWorkerThreads);

        if (inetAddress == null)
            throw new IllegalArgumentException("The inetAddress argument is null");

        this.port = port;
        this.nbWorkerThreads = nbWorkerThreads;
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNbWorkerThreads() {
        return nbWorkerThreads;
    }

    public void setNbWorkerThreads(int nbWorkerThreads) {
        this.nbWorkerThreads = nbWorkerThreads;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

}
