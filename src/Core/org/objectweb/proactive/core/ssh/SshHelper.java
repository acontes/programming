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
import org.objectweb.proactive.core.util.ProActiveInet;


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
        if (sshHelper == null) {
            sshHelper = new SshHelper();
        }
        return sshHelper;
    }

    /**
     * Private usage only
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
            ret = SubnetChecker.getInstance().getGatewayInformation(host, req);

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

        // host is here a fqdn or an ip address mapped as it (without cidr
        // definition)
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
     * Return the gateway where a proxy command can be used on to contact the
     * host hostname .
     * 
     * @param hostname
     *            The hostname to contact.
     * 
     * @return The gateway which is the relay to the host.
     */
    public String getGatewayName(String hostname) {
        return getGatewayInformation(hostname, "gateway");
    }

    /**
     * Return the port on the gateway where a proxy command can be used on to
     * contact the host hostname .
     * 
     * @param hostname
     *            The hostname to contact.
     * 
     * @return The port of the gateway where the ssh daemon can be contacted as
     *         a String.
     */
    public String getGatewayPort(String hostname) {
        String port = getGatewayInformation(hostname, "port");
        if (port != null && !port.equalsIgnoreCase("0"))
            return port;
        return "22";
    }

    /**
     * Return the username to use for access the hostname
     * 
     * @param hostname
     *            The hostname to contact.
     * 
     * @return The username as a String.
     */
    public String getGatewayUsername(String hostname) {
        return getGatewayInformation(hostname, "username");
    }

    /**
     * This method store in the Maptable the <code> information </code> (gateway name, port,
     * username, ...) to use with the Proxy Command to contact the host
     * (<code> information </code>)
     * 
     * @see SshProxy
     * 
     * @param hostname
     *            The host to contact
     * 
     * @param request
     *            The field to set (username,port,gateway, ...)
     * 
     * @param information
     *            The value of the field
     */
    private void setGatewayInformation(String hostname, String request, String information) {
        if (hostname.contains("*") && hostname.indexOf("*") == 0)
            hostname = hostname.substring(1);

        Map<String, String> gateway_infos = gatewaysTable.get(hostname);

        if (gateway_infos == null) {
            // No information already record for the host
            gateway_infos = new Hashtable<String, String>();
            gateway_infos.put(request, information);
            gatewaysTable.put(hostname, gateway_infos);
        } else {
            if (gateway_infos.get(request) != null) {
                logger.info("Gateways Infos : informations for " + hostname +
                    " are already declared, ignored : " + information);
                return;
            }
            gateway_infos.put(request, information);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Gateways Infos : " + information + " as " + request + " added for " + hostname);
        }
    }

    /**
     * Load the properties proactive.ssh.proxy.gateway and parse it
     * 
     */
    private void loadProperties() {
        // Retrieve the property
        String gatewayNames = PAProperties.PA_RMISSH_PROXY_GATEWAY.getValue();
        // And parse it
        if (gatewayNames != null)
            this.parseGateway(gatewayNames);
        // Then parse the Ssh config file
        this.parseSSHConfigFile();
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
                        this.setGatewayInformation(gateways[0], "gateway", gateways[1]);
                        this.setGatewayInformation(gateways[0], "port", gateways[2]);
                    }
                    break;
                case 2:
                    if (gateways[1].equalsIgnoreCase("none")) {
                        this.setGatewayInformation(gateways[0], "gateway", "none");
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
     * Try to get info on the hostname. First test the hostname, then try with
     * all wildcard.
     * 
     * @param hostname
     *            The hostname to retrieve.
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
    public void parseSSHConfigFile() {
        parseSSHConfigFile(null);
    }

    /**
     * Parse the SSH configuration file for finding some proxyCommand
     * declaration, and store it into the gatewaysTable
     * 
     * @param sshConfigPath
     *            the path to the SSH configuration path
     */
    public void parseSSHConfigFile(String sshConfigPath) {
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

            String host;
            String proxyCommand;
            String gateway = null;
            String proxyCommandTable[];

            // First pass for the mapping Host/Hostname
            while (line != null) {

                // Skip comment and SSH options
                if (!line.matches("Host .*")) {
                    line = br.readLine();
                    continue;
                }
                // Store the host declaration
                host = deleteWord("Host", line).trim();
                // And continue
                line = br.readLine();
                // Scan the information related to the Host declaration until
                // the next one
                while (line != null && !line.matches("Host .*")) {

                    // Check for a Nickname
                    if (line.matches("HostName .*")) {
                        realHostname.put(host, deleteWord("HostName", line).trim());
                    }
                    line = br.readLine();
                }
            }
            // reload the configuration file
            fr = new FileReader(sshConfig);
            br = new BufferedReader(fr);
            line = br.readLine();
            // Second pass
            // Iterize on all the Host declaration
            while (line != null) {

                // Skip comment and SSH options
                if (!line.matches("Host .*")) {
                    line = br.readLine();
                    continue;
                }

                // Store the host declaration
                host = deleteWord("Host", line).trim();
                // And continue
                line = br.readLine();

                // Scan the information related to the Host declaration until
                // the next one
                while (line != null && !line.matches("Host .*")) {
                    proxyCommand = null;
                    gateway = null;
                    // Port
                    if (line.matches("Port .*")) {
                        setGatewayInformation(host, "port", deleteWord("Port", line).trim());
                    }
                    // IdentifyFile
                    if (line.matches("IdentifyFile .*")) {
                        //(host, "IdentifyFile", deleteWord("IdentifyFile", line).trim());
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

                            gateway = realHostname.get(proxyCommandTable[1].trim());
                            if (gateway == null) {
                                gateway = proxyCommandTable[1].trim();
                            }
                            setGatewayInformation(host, "gateway", gateway);
                        }
                    }

                    if (line.matches("User .*")) {
                        this.setGatewayInformation(host, "username", deleteWord("User", line).trim());
                    }

                    // Try next line
                    line = br.readLine();
                }
            }
        } catch (IOException e) {
            logger.error("Can't open SSH configuration file" + path, e);
        }
    }

    private static String deleteWord(String word, String line) {
        if (line.contains(word)) {
            return line.substring(line.indexOf(word) + word.length());
        } else
            return line;
    }

    // Only for test
    public static void main(String[] args) {
        System.out.println(SshHelper.getInstance().getGatewayName("helios-50.grid5000.fr"));
        System.out.println(SshHelper.getInstance().getGatewayUsername("acces.sophia.grid5000.fr"));
        System.out.println(SshHelper.getInstance().getGatewayPort("138.96.20.214"));
        System.out.println(SshHelper.getInstance().getGatewayUsername("test.inria.fr"));
    }
}
