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
package org.objectweb.proactive.core.ssh;

import static org.objectweb.proactive.core.ssh.SSH.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


//the Unique instance of SubnetChecker

/**
 * This class store the mapping subnetwork definition/gateway:port specified with the parameter
 * proactive.ssh.proxy.gateway.
 * 
 */
public class SubnetChecker {
    private List<IPMatcher> gatewaysTable;

    public SubnetChecker() {
        gatewaysTable = new ArrayList<IPMatcher>();
    }

    protected void setGateway(String subnet, String gateway) {
        if (logger.isDebugEnabled()) {
            logger.debug("Subnet Infos : " + gateway + " as " + "gateway" + " added for subnet " + subnet);
        }
      
        // separate network address and cidr
        String[] subnetDef = subnet.split("\\/");
        try {
            IPMatcher gateway_infos = new IPMatcher(subnetDef[0], Integer.parseInt(subnetDef[1]), gateway);
            gatewaysTable.add(gateway_infos);
            // list is scan in inverse direction so sorting it, permit to
            // make definition of subnet with higher cidr more important than other
            Collections.sort(gatewaysTable);

        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Return the gateway where a proxy command can be used on to contact the host's IP address.
     * 
     * @param ipAddress The host IP address.
     * 
     * @return The gateway which is the relay to the host.
     * @throws Exception 
     */
    public String getGateway(String ipAddress) {
        IPMatcher sd = checkIP(ipAddress);
        if (sd != null)
            return sd.getGateway();
        return null;
    }

    private IPMatcher checkIP(String ip) {
        // Began by the end for CIDR classification
        IPMatcher matcher;
        for (int i = gatewaysTable.size() - 1; i >= 0; i--) {
            matcher = gatewaysTable.get(i);
            if (matcher.match(ip)) {
                return matcher;
            }
        }
        return null;
    }

    /**
     * This class store the gateway for a sub network 
     * define by network/cidr : xxx.xxx.xxx.xxx/xx   
     */
    static private class IPMatcher implements Comparable<IPMatcher> {

        // Store for getRule method
        final private int cidr;
        final private String network;       
        
        final private int networkPortion;
        final private int mask;
        
        private String gateway = null;

        public IPMatcher(String network, int cidr, String gateway) {
            this.gateway = gateway;
            this.mask = computeMask(cidr);
            this.networkPortion = stringToInt(network) & this.mask;
            this.cidr = cidr;
            this.network = network;
        }

        public String getGateway() {
            return this.gateway;
        }

        private int computeMask(int mask) {
            int shift = Integer.SIZE - mask;
            return (~0 >> shift) << shift;
        }

        public boolean match(String ip) {
            return match(stringToInt(ip));
        }

        public boolean match(int ip) {
            return (ip & mask) == networkPortion;
        }

        private int stringToInt(String ip) throws IllegalArgumentException {
            String[] parts = ip.split("\\.");
            int tmp = 0;
            if (parts.length != 4) {
                throw new IllegalArgumentException("An IPv4 address must like xxx.xxx.xxx.xxx : " + ip);
            }
            for (int i = 0; i < 4; i++) {
                int parsedInt = Integer.parseInt(parts[i]);
                if (parsedInt > 255 || parsedInt < 0) {
                    throw new IllegalArgumentException("An octet must be a number between 0 and 255.");
                }
                tmp |= parsedInt << ((3 - i) * 8);
            }
            return tmp;
        }

        /**
         * IPMatcher are ordered by CIDR
         * 
         * The higher CIDR is the more specific so the prefered one
         * 
         *  The higher the cidr is, the higher the mask will be 
         */
        public int compareTo(IPMatcher other) {
            return other.mask - this.mask;
        }

        public String getRule() {            
            return this.network+"/"+this.cidr;
        }
    }

    public String getRule(String gateway) {
        StringBuilder sb = new StringBuilder("");  
        for (IPMatcher ipm : gatewaysTable){
              if (ipm.getGateway().equalsIgnoreCase(gateway)){
                  sb.append(ipm.getRule());                  
                  sb.append(";");
              }
          }
        return sb.toString();
    }
}