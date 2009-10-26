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
package org.objectweb.proactive.extra.messagerouting.router.dc.config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The central repository for the network topology 
 * 	information available to the router
 * 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class NetworkConfigurationRegistry {

    /**  The map of allowed communications.
     * 	 The addresses which are stored in this map are resolved IP addresses.
     *   If an entry in the List is an {@link InetSocketAddress} with the
     *   port value of ANY_PORT, it means that communications are allowed 
     *   on any port for the given hostname
     *  */
    private final Map<InetAddress, List<InetSocketAddress>> allowedCommunications;
    /** If this is activated, communication is allowed between any two endpoints */
    private final boolean allAllowed;
    /** This is the value to indicate that communication on any port can be initiated */
    private static final int ANY_PORT = 0;

    public NetworkConfigurationRegistry() {
        this.allowedCommunications = new HashMap<InetAddress, List<InetSocketAddress>>();
        this.allAllowed = false;
    }

    public NetworkConfigurationRegistry(boolean allowAll) {
        this.allAllowed = allowAll;
        if (allowAll) {
            this.allowedCommunications = null;
        } else {
            this.allowedCommunications = new HashMap<InetAddress, List<InetSocketAddress>>();
        }
    }

    /** Communication allowed from srcHost to dstHost on any port 
     * @throws UnknownHostException if dstHost cannot be resolved */
    public void addAllowedHosts(String srcHost, String dstHost) throws UnknownHostException {
        addAllowed(srcHost, dstHost, ANY_PORT);
        addAllowed(dstHost, srcHost, ANY_PORT);
    }

    /** Communication allowed from srcHost to dstHost only on the given destPort 
     * @throws UnknownHostException if destHost cannot be resolved */
    public void addAllowed(String src, String destHost, int destPort) throws UnknownHostException {
        // try to resolve destHost
        InetAddress destAddr = InetAddress.getByName(destHost);
        InetSocketAddress dest = new InetSocketAddress(destAddr, destPort);
        addAllowed(src, dest);
    }

    /** Communication allowed from srcHost to dstHost only on the given destPort 
     * @throws UnknownHostException */
    private void addAllowed(String srcHost, InetSocketAddress dest) throws UnknownHostException {

        if (this.allAllowed)
            throw new IllegalStateException("All communications are allowed");

        if (dest.isUnresolved())
            throw new IllegalArgumentException("This operation is not supported for unresolved addresses " +
                dest);

        // try to resolve srcHost 
        InetAddress src = InetAddress.getByName(srcHost);

        if (this.allowedCommunications.containsKey(src)) {
            List<InetSocketAddress> allowedEndpoints = this.allowedCommunications.get(src);
            if (!inAllowedList(dest, allowedEndpoints))
                allowedEndpoints.add(dest);
        } else {
            List<InetSocketAddress> allowedEndpoints = new ArrayList<InetSocketAddress>();
            allowedEndpoints.add(dest);
            allowedCommunications.put(src, allowedEndpoints);
        }
    }

    private boolean inAllowedList(InetSocketAddress dest, List<InetSocketAddress> allowedEndpoints) {

        if (allowedEndpoints.contains(dest))
            return true;

        for (InetSocketAddress allowedEndpoint : allowedEndpoints) {
            if (allowedEndpoint.isUnresolved())
                throw new IllegalStateException("The registry contains an unresolved address: " +
                    allowedEndpoint.toString());

            if (allowedEndpoint.getPort() == ANY_PORT &&
                allowedEndpoint.getAddress().equals(dest.getAddress())) {
                return true;
            }
        }
        return false;

    }

    public boolean isAllowed(InetAddress src, InetSocketAddress dest) {
        if (this.allAllowed)
            return true;

        if (!this.allowedCommunications.containsKey(src))
            return false;

        return inAllowedList(dest, this.allowedCommunications.get(src));
    }

}
