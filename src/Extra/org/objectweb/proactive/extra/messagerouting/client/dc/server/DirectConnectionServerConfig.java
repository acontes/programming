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

import org.objectweb.proactive.core.config.PAProperties;


/**
 * Configuration options for the direct connection server
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionServerConfig {

    private final int port;

    private final int nbWorkerThreads;

    private static final int DEFAULT_NB_WORKER_THREADS = 4;

    private final InetAddress inetAddress;

    /**
     * Read the configuration from the ProActive properties set on this virtual machine
     * @throws UnknownHostException if the IP address set on the {@link PAProperties#PA_NET_ROUTER_DC_ADDRESS} is unknown
     * @throws DirectConnectionDisabledException if the {@link PAProperties#PA_NET_ROUTER_DIRECT_CONNECTION} property is not set
     * @throws MissingPortException if the mandatory port {@link PAProperties#PA_NET_ROUTER_DC_PORT} is missing
     */
    public DirectConnectionServerConfig() throws UnknownHostException, DirectConnectionDisabledException,
            MissingPortException {

        if (!(PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.isSet() && PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION
                .isTrue()))
            throw new DirectConnectionDisabledException(" The direct connection flag " +
                PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.getKey() +
                " is not set as a ProActive property ");

        if (PAProperties.PA_NET_ROUTER_DC_ADDRESS.isSet()) {
            String addr = PAProperties.PA_NET_ROUTER_DC_ADDRESS.getValue();
            try {
                this.inetAddress = InetAddress.getByName(addr);
            } catch (UnknownHostException e) {
                throw new UnknownHostException("Problem with the value of the " +
                    PAProperties.PA_NET_ROUTER_DC_ADDRESS.getKey() + " ProActive Property: " +
                    " cannot get the IP address of the host " + addr + " reason: " + e.getMessage());
            }
        } else {
            try {
                this.inetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new UnknownHostException("Cannot get the IP address of the local host!");
            }
        }

        if (PAProperties.PA_NET_ROUTER_DC_PORT.isSet()) {
            this.port = PAProperties.PA_NET_ROUTER_DC_PORT.getValueAsInt();
        } else
            throw new MissingPortException("Mandatory option " + PAProperties.PA_NET_ROUTER_DC_PORT.getKey() +
                " is not set as a ProActive property ");

        if (PAProperties.PA_NET_ROUTER_DC_WORKERS_NO.isSet())
            this.nbWorkerThreads = PAProperties.PA_NET_ROUTER_DC_WORKERS_NO.getValueAsInt();
        else
            this.nbWorkerThreads = DEFAULT_NB_WORKER_THREADS;

    }

    public static class DirectConnectionDisabledException extends Exception {

        public DirectConnectionDisabledException(String string) {
            super(string);
        }
    }

    public static class MissingPortException extends Exception {

        public MissingPortException(String string) {
            super(string);
        }

    }

    public DirectConnectionServerConfig(int port) throws UnknownHostException {
        this(port, DEFAULT_NB_WORKER_THREADS, InetAddress.getLocalHost());
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

    public int getNbWorkerThreads() {
        return nbWorkerThreads;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

}
