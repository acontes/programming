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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;

import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.ssh.SshProxy;


//the Unique instance of GatewaysInfos
public class GatewaysInfos {
    private static GatewaysInfos gatewaysInfos = new GatewaysInfos();
    private static Map<String, Map<String, String>> gatewaysTable;

    private GatewaysInfos() {
        ProActiveConfiguration.load();
        gatewaysTable = new Hashtable<String, Map<String, String>>();
        loadProperties();
        if (logger.isDebugEnabled()) {
            logger.debug("Gateways Infos loaded");
        }
    }

    /**
     * Return the gateway where a proxy command can be used on to contact the host hostname .
     * 
     * @param hostname The hostname to contact.
     * 
     * @return The gateway which is the relay to the host.
     */
    public static String getGatewayName(String hostname) {
        String host = hostname;
        String ret = "";

        if (host.matches("^.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
            // local host case (by ip address)
            if (host.equalsIgnoreCase(ProActiveInet.getInstance().getInetAddress().getHostAddress())) {
                return null;
            }

            // Try with ip address mapping
            ret = SubnetChecker.getGatewayName(hostname);

            if (ret != null)
                return ret;

            // remote host case
            InetAddress addr;
            try {
                addr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                addr = null;
            }

            if (addr != null) {
                host = addr.getHostName();
                if (host
                        .equalsIgnoreCase(ProActiveInet.getInstance().getInetAddress().getCanonicalHostName())) {
                    // local host case (by name)
                    return null;
                }
            }
        }

        // Try with hostname (or single ip address definition) mapping
        Map<String, String> gateway_infos = getGatewayInfos(host);

        if (gateway_infos == null)
            return null;

        ret = gateway_infos.get("gateway");

        // Case of the gateway
        if (ret == null || ret.equals("none")) {
            return null;
        }

        return ret;
    }

    /**
     * Return the port on the gateway where a proxy command can be used on to contact the host
     * hostname .
     * 
     * @param hostname The hostname to contact.
     * 
     * @return The port of the gateway where the ssh daemon can be contacted.
     */
    public static int getGatewayPort(String hostname) {
        String host = hostname;
        int ret = 0;

        if (host.matches("^.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
            // local host case (by ip address)
            if (host.equalsIgnoreCase(ProActiveInet.getInstance().getInetAddress().getHostAddress())) {
                return 0;
            }

            // Try with ip address mapping
            ret = SubnetChecker.getGatewayPort(hostname);

            if (ret != 0)
                return ret;

            // remote host case
            InetAddress addr;
            try {
                addr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                addr = null;
            }

            if (addr != null) {
                host = addr.getHostName();
                if (host
                        .equalsIgnoreCase(ProActiveInet.getInstance().getInetAddress().getCanonicalHostName())) {
                    // local host case (by name)
                    return 0;
                }
            }
        }

        // Try with hostname (or single ip address definition) mapping
        Map<String, String> gateway_infos = getGatewayInfos(host);

        if (gateway_infos == null)
            return 0;

        return Integer.parseInt(gateway_infos.get("port"));
    }

    /**
     * This method store in the Maptable the gateway and the port to use with the Proxy Command to
     * contact the host (hostname)
     * 
     * @see SshProxy
     * 
     * @param hostname The host to contact
     * 
     * @param gateway The gateway to used for contacting the hostname
     * 
     * @param port The port on the gateway to contact the ssh daemon
     */
    private static void setGateway(String hostname, String gateway, String port) {
        if (logger.isDebugEnabled()) {
            logger.debug("Gateways Infos : " + gateway + " added for " + hostname);
        }

        if (hostname.contains("*") && hostname.indexOf("*") == 0)
            hostname = hostname.substring(1);

        Map<String, String> gateway_infos = gatewaysTable.get(hostname);

        if (gateway_infos == null) {
            // The gateway isn't already record
            gateway_infos = new Hashtable<String, String>();

            gateway_infos.put("gateway", gateway);
            gateway_infos.put("port", port);
            gatewaysTable.put(hostname, gateway_infos);
        } else {
            logger.info("Gateways Infos : informations for " + hostname + " are already declared, ignored");
        }
    }

    /**
     * Load the properties proactive.ssh.proxy.gateway and parse it
     * 
     */
    private void loadProperties() {
        // Retrieve the property
        String gatewayNames = PAProperties.PA_SSH_PROXY_GATEWAY.getValue();
        // And parse it
        if (gatewayNames != null)
            parseGateway(gatewayNames);
    }

    /**
     * Split the hosts and it's gateway/port and store it in the map table
     * 
     */
    private void parseGateway(String gateway) {
        String[] proxies = gateway.split(";");
        for (int i = 0; i < proxies.length; i++) {
            String[] gateways = proxies[i].split(":");
            switch (gateways.length) {
                case 3:
                    if (gateways[0]
                            .matches("^.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\/[1-3]?[0-9]")) {
                        SubnetChecker.setGateway(gateways[0], gateways[1], gateways[2]);
                    } else {
                        setGateway(gateways[0], gateways[1], gateways[2]);
                    }
                    break;
                case 2:
                    if (gateways[1].equalsIgnoreCase("none")) {
                        setGateway(gateways[0], gateways[1], "0");
                        break;
                    }
                default:
                    logger
                            .error("ERROR: malformed gateway declaration. Should be host1.domain1:gateway1:port;*.domain2:host2:port2");
                    continue;
            }
        }
    }

    /**
     * Try to get info on the hostname. First test the hostname, then try with all wildcard.
     * 
     * @param hostname The hostname to retrieve.
     * @return A map of (username, port) of the gateway for accessing the host.
     */
    private static Map<String, String> getGatewayInfos(String hostname) {
        Map<String, String> host_infos;
        int index;
        while (hostname.contains(".")) {
            host_infos = gatewaysTable.get(hostname);
            if (host_infos != null) {
                return host_infos;
            } else {
                // in case that hostname begin by a point
                hostname = hostname.substring(1);
                index = hostname.indexOf('.');
                if (index < 0)
                    break;
                hostname = hostname.substring(hostname.indexOf('.'));
            }
        }
        host_infos = gatewaysTable.get(hostname);
        return host_infos;
    }

    // Only for test
    public static void main(String[] args) {
        PAProperties.PA_SSH_PROXY_GATEWAY
                .setValue("*.grid5000.fr:acces.sophia.grid5000.fr:22;*.grid5000.fr:toto:33");
        System.out.println(PAProperties.PA_SSH_PROXY_GATEWAY);
        System.out.println(GatewaysInfos.getGatewayName("azur-9.sophia.grid5000.fr"));
    }
}
