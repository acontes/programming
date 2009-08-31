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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.ssh.SshProxy;


public class SshHelper {
    // the Unique instance of SshHelper      
    private static SshHelper sshHelper;
    private Map<String, Map<String, String>> gatewaysTable;

    private SshHelper() {
        ProActiveConfiguration.load();
        gatewaysTable = new Hashtable<String, Map<String, String>>();
        loadProperties();        
        if (logger.isDebugEnabled()) {
            logger.debug("Gateways Infos loaded");
        }
    }

    // Pattern Singleton
    public static SshHelper getInstance() {
        if (sshHelper == null){
            sshHelper = new SshHelper();
            parseSSHConfigFile();
        }
        return sshHelper;
    }

    /**
     *  Private usage only  
     */
    private String getGatewayInformation(String host, String req) {
        String ret = "";
        
        // First try, the given host is an IPv4 address
        if (host.matches("^.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
            // local host case (by ip address)
            if (host.equalsIgnoreCase(ProActiveInet.getInstance().getInetAddress().getHostAddress())) {
                return null;
            }

            // Try with ip address mapping
            ret = SubnetChecker.getInstance().getGatewayName(host);
            
            if (ret != null)
                return ret;

            // Get the fqdn from the IP address
            InetAddress addr;
            try {
                addr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                addr = null;
            }
            if (addr != null) {
                host = addr.getHostName();
            }
        }

        // host is here a fqdn or an ip address mapped as it (without cidr definition)
        if (host.equalsIgnoreCase(ProActiveInet.getInstance().getInetAddress().getCanonicalHostName())) {
            // local host case (by name)
            return null;
        }

        // Try with hostname (or single ip address definition) mapping
        Map<String, String> gateway_infos = this.getGatewayInfos(host);

        if (gateway_infos == null)
            return null;

        // Get from the information map, the requested information
        ret = gateway_infos.get(req);

        // Specific case
        if (ret == null || ret.equals("none") || ret.equals("0")) {
            return null;
        }

        return ret;
    }

    /**
     * Return the gateway where a proxy command can be used on to contact the host hostname .
     * 
     * @param hostname The hostname to contact.
     * 
     * @return The gateway which is the relay to the host.
     */
    public String getGatewayName(String hostname) {
        return getGatewayInformation(hostname, "gateway");
    }

    /**
     * Return the port on the gateway where a proxy command can be used on to contact the host
     * hostname .
     * 
     * @param hostname The hostname to contact.
     * 
     * @return The port of the gateway where the ssh daemon can be contacted as a String.
     */
    public String getGatewayPort(String hostname) {
        return getGatewayInformation(hostname, "port");
    }

    /**
     * Return the username to use for access the hostname 
     *     
     * @param hostname The hostname to contact.
     * 
     * @return The username as a String.
     */
    public String getGatewayUsername(String hostname) {
        return getGatewayInformation(hostname, "username");
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
    private void setGateway(String hostname, String gateway, String port) {
        if (hostname.contains("*") && hostname.indexOf("*") == 0)
            hostname = hostname.substring(1);

        Map<String, String> gateway_infos = gatewaysTable.get(hostname);

        if (gateway_infos == null) {
            // No information already record for the hostname
            gateway_infos = new Hashtable<String, String>();
            gateway_infos.put("gateway", gateway);
            gateway_infos.put("port", port);
            gatewaysTable.put(hostname, gateway_infos);
        }

        if (gateway_infos.get("gateway") != null) {
            logger.info("Gateways Infos : informations for " + hostname + " are already declared, ignored");
            return;
        }
        gateway_infos.put("gateway", gateway);
        gateway_infos.put("port", port);

        if (logger.isDebugEnabled()) {
            logger.debug("Gateways Infos : " + gateway + " added for " + hostname);
        }
    }

    private void setGateway(String host, String username) {
        if (logger.isDebugEnabled()) {
            logger.debug("Ssh Username : " + username + " added for " + host);
        }

        if (host.contains("*") && host.indexOf("*") == 0)
            host = host.substring(1);

        Map<String, String> gateway_infos = gatewaysTable.get(host);

        if (gateway_infos == null) {
            // The gateway isn't already record
            gateway_infos = new Hashtable<String, String>();
            gateway_infos.put("username", username);
            gatewaysTable.put(host, gateway_infos);
        }

        if (gateway_infos.get("username") != null) {
            logger.info("Gateways Infos : informations for " + host + " are already declared, ignored");
            return;
        }

        // Only update
        gateway_infos.put("username", username);
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
            this.parseGateway(gatewayNames);
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
                        SubnetChecker.getInstance().setGateway(gateways[0], gateways[1], gateways[2]);
                    } else {
                        this.setGateway(gateways[0], gateways[1], gateways[2]);
                    }
                    break;
                case 2:
                    if (gateways[1].equalsIgnoreCase("none")) {
                        this.setGateway(gateways[0], gateways[1], "0");
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
    private Map<String, String> getGatewayInfos(String hostname) {
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

    public void addProperties(String properties) {
        if (properties == null || properties.equals(""))
            return;
        parseGateway(properties);
    }

    /** 
     * @see SshHelper#parseSSHConfigFile(String)
     */
    public static void parseSSHConfigFile() {
        parseSSHConfigFile(null);
    }

    /**
     * Parse the SSH configuration file for finding some proxyCommand declaration, and
     * store it into the gatewaysTable
     * 
     * @param sshConfigPath the path to the SSH configuration path 
     */
    public static void parseSSHConfigFile(String sshConfigPath) {
        String path;
        Map<String, String> realHostname = new HashMap<String, String>();

        if (sshConfigPath != null) {
            path = sshConfigPath;
        } else {
            if (!System.getProperty("os.name").equals("Linux"))
                return;
            path = System.getProperty("user.home") + "/.ssh/config";
        }
        File sshConfig = new File(path);

        FileReader fr;
        BufferedReader br;

        try {
            fr = new FileReader(sshConfig);
            br = new BufferedReader(fr);

            String line = null;
            line = br.readLine();
            // EOF
            if (line == null)
                return;

            String host;
            String proxyCommand;
            String gateway = null;
            String proxyCommandTable[];
            Boolean wildcard = false;
            // Iterize on all the Host declaration
            while (true) {

                // Skip comment and SSH options 
                if (!line.matches("[Hh]ost .*")) {
                    line = br.readLine();
                    continue;
                }

                // Store the host declaration 
                host = deleteWord("Host", line);
                // And continue
                line = br.readLine();

                // EOF
                if (line == null)
                    return;

                // Scan the information related to the Host declaration until the
                // next one
                while (!line.matches("Host .*")) {
                    proxyCommand = null;
                    gateway = null;

                    // Don't search for a Nickname, if there is a wildcarded host definition
                    if (line.contains("*")) {
                        wildcard = true;
                    }

                    // Check for a Nickname
                    if (line.matches("HostName .*")) {
                        realHostname.put(host.trim(), deleteWord("HostName", line).trim());
                    }

                    // Here is the proxyCommand declaration
                    if (line.matches("ProxyCommand .*")) {

                        proxyCommand = deleteWord("ProxyCommand", line);
                        proxyCommand = deleteWord("ssh", proxyCommand);

                        // Skip SSH specific options
                        if (proxyCommand.indexOf("${") > 0) {
                            proxyCommand = deleteWord("}", proxyCommand);
                        }

                        if (proxyCommand != null && !proxyCommand.trim().isEmpty() &&
                            !proxyCommand.trim().equalsIgnoreCase("none")) {

                            proxyCommandTable = proxyCommand.split(" ");

                            // Search for a Nickname/Hostname mapping
                            if (!wildcard) {
                                gateway = realHostname.get(proxyCommandTable[1].trim());
                            }

                            if (gateway == null) {
                                gateway = proxyCommandTable[1];
                            }

                            sshHelper.addProperties(host.trim() + ":" + gateway.trim() + ":" +
                                PAProperties.PA_SSH_PORT.getValue());
                        }
                    }

                    if (line.matches("User .*")) {
                        line = deleteWord("User", line);
                        sshHelper.setGateway(host.trim(), line.trim());
                    }

                    // Try next line
                    line = br.readLine();
                    if (line == null)
                        return;
                }
            }
        } catch (IOException e) {
            logger.error("Can't open SSH configuration file" + path, e);
        }
    }

    private static String deleteWord(String word, String line) {
        return line.substring(line.indexOf(word) + word.length());
    }

    // Only for test
    public static void main(String[] args) {
        PAProperties.PA_SSH_PROXY_GATEWAY
                .setValue("*.grid5000.fr:acces.sophia.grid5000.fr:22;*.grid5000.fr:toto:33");
        //   System.out.println(PAProperties.PA_SSH_PROXY_GATEWAY);
        System.out.println(SshHelper.getInstance().getGatewayName("azur-9.sophia.grid5000.fr"));
        System.out.println(SshHelper.getInstance().getGatewayName("test.unice.fr"));
        System.out.println(SshHelper.getInstance().getGatewayUsername("test.unice.fr"));        
    }
}
