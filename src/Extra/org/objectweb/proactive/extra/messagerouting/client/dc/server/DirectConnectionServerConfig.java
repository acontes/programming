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

    private int port;

    private final InetAddress inetAddress;

    private static final String PORT_RANGE_SEPARATOR = "-";

    /**
     * Read the configuration from the ProActive properties set on this virtual machine
     * @throws UnknownHostException if the IP address set on the {@link PAProperties#PA_PAMR_DC_ADDRESS} is unknown
     * @throws DirectConnectionDisabledException if the {@link PAProperties#PA_PAMR_DIRECT_CONNECTION} property is not set
     * @throws IllegalArgumentException if invalid inputs were provided for the ProActive properties
     */
    public DirectConnectionServerConfig() throws UnknownHostException, DirectConnectionDisabledException,
            IllegalArgumentException {

        if (!(PAProperties.PA_PAMR_DIRECT_CONNECTION.isSet() && PAProperties.PA_PAMR_DIRECT_CONNECTION
                .isTrue()))
            throw new DirectConnectionDisabledException(" The direct connection flag " +
                PAProperties.PA_PAMR_DIRECT_CONNECTION.getKey() + " is not set as a ProActive property ");

        if (PAProperties.PA_PAMR_DC_ADDRESS.isSet()) {
            String addr = PAProperties.PA_PAMR_DC_ADDRESS.getValue();
            try {
                this.inetAddress = InetAddress.getByName(addr);
            } catch (UnknownHostException e) {
                throw new UnknownHostException("Problem with the value of the " +
                    PAProperties.PA_PAMR_DC_ADDRESS.getKey() + " ProActive Property: " +
                    " cannot get the IP address of the host " + addr + " reason: " + e.getMessage());
            }
        } else {
            try {
                this.inetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new UnknownHostException("Cannot get the IP address of the local host!");
            }
        }

        if (PAProperties.PA_PAMR_DC_PORT.isSet()) {
            this.port = PAProperties.PA_PAMR_DC_PORT.getValueAsInt();
            if (port < 0 || port > 65535)
                throw new IllegalArgumentException("Invalid value for the " +
                    PAProperties.PA_PAMR_DC_PORT.getKey() + " ProActive property: " + port);
        } else if (PAProperties.PA_PAMR_DC_PORT_RANGE.isSet()) {
            String range = PAProperties.PA_PAMR_DC_PORT_RANGE.getValue();
            String[] portLimits = range.split(PORT_RANGE_SEPARATOR);
            if (portLimits.length != 2)
                throw new IllegalArgumentException("Invalid value for the " +
                    PAProperties.PA_PAMR_DC_PORT_RANGE.getKey() + " ProActive property: " + range);
            try {
                int lower = Integer.parseInt(portLimits[0]);
                int upper = Integer.parseInt(portLimits[1]);
                this.port = searchAvailablePort(lower, upper);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for the " +
                    PAProperties.PA_PAMR_DC_PORT_RANGE.getKey() + " ProActive property: " + range);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid value for the " +
                    PAProperties.PA_PAMR_DC_PORT_RANGE.getKey() + " ProActive property: " + range + " - " +
                    e.getMessage());
            }
        } else {
            // random port by default
            this.port = 0;
        }

    }

    /**
     * Attempt to find an available port on the local machine.
     *
     * All ports within the range (lower, upper) will be tried, in order.
     *
     * If all attempts fail, an {@link IllegalArgumentException} is thrown
     */
    private static int searchAvailablePort(int lower, int upper) {

        if (lower <= 0)
            throw new IllegalArgumentException("The bind port range goes below the valid port values range");
        if (lower > upper)
            throw new IllegalArgumentException("Invalid port range : lower value is greater than upper value");
        if (upper > 65535)
            throw new IllegalArgumentException("The bind port range goes above the valid port values range");

        for (int portToTry = lower; portToTry <= upper; portToTry++) {
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
                // next attempt
                continue;
            }
        }
        throw new IllegalArgumentException("Could not find a free port within the specified range");
    }

    public static class DirectConnectionDisabledException extends Exception {

        public DirectConnectionDisabledException(String string) {
            super(string);
        }
    }

    public DirectConnectionServerConfig(int lowerRange, int upperRange) throws UnknownHostException {
        this(searchAvailablePort(lowerRange, upperRange), InetAddress.getLocalHost());
    }

    public DirectConnectionServerConfig(int port, InetAddress inetAddress) {

        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port value:" + port);

        if (inetAddress == null)
            throw new IllegalArgumentException("The inetAddress argument is null");

        this.port = port;
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int realPort) {
        this.port = realPort;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

}
