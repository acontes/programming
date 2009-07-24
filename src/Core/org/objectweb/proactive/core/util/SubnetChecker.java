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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.util;

import static org.objectweb.proactive.core.ssh.SSH.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//the Unique instance of SubnetChecker

/**
 * This class store the mapping subnetwork definition/gateway:port specified with the parameter
 * proactive.ssh.proxy.gateway.
 * 
 */
public class SubnetChecker {
    private static SubnetChecker SingletonPattern = new SubnetChecker();
    private static List<SubnetDefinition> gatewaysTable;

    private SubnetChecker() {
        gatewaysTable = new ArrayList<SubnetDefinition>();
    }

    protected static void setGateway(String subnet, String gateway, String port) {
        if (logger.isDebugEnabled()) {
            logger.debug("Subnet Infos : " + gateway + " added for subnet " + subnet);
        }

        // separate network address and cidr
        String[] subnetDef = subnet.split("\\/");
        SubnetDefinition gateway_infos = new SubnetDefinition(subnetDef[0], Integer.parseInt(subnetDef[1]),
            gateway, Integer.parseInt(port));
        gatewaysTable.add(gateway_infos);
        // list is scan in inverse direction so sorting it, permit to
        // make definition of subnet with higher cidr more important than other
        Collections.sort(gatewaysTable);
    }

    /**
     * Return the gateway where a proxy command can be used on to contact the host's IP address.
     * 
     * @param ipAddress The host IP address.
     * 
     * @return The gateway which is the relay to the host.
     */
    public static String getGatewayName(String ipAddress) {
        SubnetDefinition sd = checkIP(ipAddress);
        if (sd != null)
            return sd.getGateway();
        return null;
    }

    /**
     * Return the port on the gateway where a proxy command can be used on to contact the host's IP
     * address .
     * 
     * @param ipAddress The host IP address.
     * 
     * @return The port of the gateway where the ssh daemon can be contacted.
     */
    public static int getGatewayPort(String ipAddress) {
        SubnetDefinition sd = checkIP(ipAddress);
        if (sd != null)
            return sd.getPort();
        return 0;
    }

    private static SubnetDefinition checkIP(String ip) {
        String[] ip_tab = ip.split("\\.");
        // Because Java's int are only signed and bit to bit shift behavior
        // can be undeterministic depending on implementation.
        long ip_int = 0;
        ip_int += (Long.parseLong(ip_tab[0]) & 0xFF) << 24;
        ip_int += (Long.parseLong(ip_tab[1]) & 0xFF) << 16;
        ip_int += (Long.parseLong(ip_tab[2]) & 0xFF) << 8;
        ip_int += (Long.parseLong(ip_tab[3]) & 0xFF);

        // Began by the end for CIDR classification
        for (int i = gatewaysTable.size() - 1; i >= 0; i--) {
            SubnetDefinition sd = gatewaysTable.get(i);
            String net = sd.getSubnet();
            int cidr = sd.getCidr();
            String[] net_tab = net.split("\\.");
            long net_int = 0;
            net_int += (Long.parseLong(net_tab[0]) & 0xFF) << 24;
            net_int += (Long.parseLong(net_tab[1]) & 0xFF) << 16;
            net_int += (Long.parseLong(net_tab[2]) & 0xFF) << 8;
            net_int += (Long.parseLong(net_tab[3]) & 0xFF);

            // Check if networks are the same
            if (ip_int >>> (32 - cidr) == net_int >>> (32 - cidr))
                return sd;
        }
        return null;
    }

    // Only for test
    public static void main(String[] args) {
        SubnetChecker.setGateway("138.96.20.0/24", "acces.sop", "22");
        String ip = "138.96.20.33";
        System.out.println(getGatewayName(ip));
        System.out.println(getGatewayPort(ip));
    }

}