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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.ProActiveRandom;


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

    // the real value of the port onto which the Server should bind
    private final int realPort;

    // configuration options for rebind attempts
    public static final int DEFAULT_BIND_ATTEMPTS = 3;
    public static final int DEFAULT_BIND_PORT_RANGE = 5;

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

        // TODO config with PAProperties?
        int bindAttempts = DEFAULT_BIND_ATTEMPTS;
        int bindPortRange = DEFAULT_BIND_PORT_RANGE;
        this.realPort = searchAvailablePort(this.port, bindAttempts, bindPortRange);

    }

    /**
     * Attempt to find an available port on the local machine.
     *
     * First, the initial port value is tried. If that does not succeed, random port values
     * within the range
     * ( initialPort - bindPortRange, initialPort + bindPortRange )
     * are tried. The total number of attempts - including the initial atempt
     * for the original port value - is specified by the bindAttempts parameter.
     *
     * If all attempts fail, a {@link MissingPortException} is thrown
     */
    private static int searchAvailablePort(int initialPort, int bindPortRange, int bindAttempts)
            throws MissingPortException {

        if (!(0 <= initialPort && initialPort < 65535))
            throw new IllegalArgumentException("Invalid initial port value: ");
        int lower = initialPort - bindPortRange;
        if (lower <= 0)
            throw new IllegalArgumentException("The bind port range goes below the valid port values range");
        int upper = initialPort + bindPortRange;
        if (upper > 65535)
            throw new IllegalArgumentException("The bind port range goes above the valid port values range");

        int portToTry = initialPort;
        for (int attemptNo = 0; attemptNo < bindAttempts; attemptNo++) {
            // tentative bind
            try {
                ServerSocket ss = new ServerSocket(portToTry);
                // bind succeeded
                try {
                    ss.close();
                    return portToTry;
                } catch (IOException e) {
                    // the error reason could cause a next bind to fail. keep trying
                    continue;
                }
            } catch (IOException e) {
                // prepare the next attempt
                portToTry = nextIntBetween(lower, upper);
            }
        }
        throw new MissingPortException("The value of the " + PAProperties.PA_NET_ROUTER_DC_PORT.getKey() +
            " ProActive property specifies an unavailable port, and all " + bindAttempts +
            " attempts to find a free port within the range of ( " + lower + " , " + upper + " ) failed.");
    }

    private static final int nextIntBetween(int lower, int upper) {
        if (lower == upper)
            return lower;
        return lower + ProActiveRandom.nextInt(upper - lower);
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

    public DirectConnectionServerConfig(int port, int bindPortRange, int bindAttempts)
            throws UnknownHostException, MissingPortException {
        this(port, searchAvailablePort(port, bindPortRange, bindAttempts), DEFAULT_NB_WORKER_THREADS,
                InetAddress.getLocalHost());
    }

    public DirectConnectionServerConfig(int port, int realPort, int nbWorkerThreads, InetAddress inetAddress) {

        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port value:" + port);

        if (realPort < 0 || realPort > 65535)
            throw new IllegalArgumentException("Invalid (real) port value:" + realPort);

        if (nbWorkerThreads <= 0)
            throw new IllegalArgumentException("Invalid number of worker threads: " + nbWorkerThreads);

        if (inetAddress == null)
            throw new IllegalArgumentException("The inetAddress argument is null");

        this.port = port;
        this.realPort = realPort;
        this.nbWorkerThreads = nbWorkerThreads;
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return realPort;
    }

    public int getNbWorkerThreads() {
        return nbWorkerThreads;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

}
