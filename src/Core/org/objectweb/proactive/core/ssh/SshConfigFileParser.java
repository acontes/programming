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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.objectweb.proactive.core.config.PAProperties;


public class SshConfigFileParser {

    public enum SshToken {
        HOST("Host"), HOSTNAME("HostName"), GATEWAY("ProxyCommand"), USERNAME("User"), PRIVATEKEY(
                "IdentifyFile"), PORT("Port"), UNKNOW("");

        private String key;
        private boolean wanted;

        SshToken(String key) {
            this.key = key;
            this.wanted = false;
        }

        String getValue() {
            return key;
        }

        boolean isWanted() {
            return wanted;
        }

        void wanted() {
            this.wanted = true;
        }
    }

    private Map<String, String> realHostName;

    public SshConfigFileParser() {
        this.realHostName = new Hashtable<String, String>();
    }

    public SshConfigFileParser(SshConfigStorer storer) {
        this(storer, "");
    }

    public SshConfigFileParser(SshConfigStorer storer, String path) {
        this();
        parse(path, storer);
        parseProperties(PAProperties.PA_SSH_PROXY_GATEWAY.getValue(), storer);
    }

    /**
     * Parse the SSH configuration
     * 
     * @param config       
     * 
     * @param path
     *            the path to the SSH configuration path
     */
    public void parse(String path, SshConfigStorer storer) {
        if (path.isEmpty())
            path = System.getProperty("user.home") + "/.ssh/config";

        SshToken[] capabilities = storer.getCapabilities();
        for (int i = 0; i < capabilities.length; i++) {
            capabilities[i].wanted();
        }

        File file = new File(path);
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String line = null;
            line = br.readLine();

            // For each Host declaration
            while (line != null) {
                // Comments
                if (line.startsWith("#")) {
                    line = br.readLine();
                    continue;
                }

                String host = null;
                List<String> definitions = new ArrayList<String>();

                // Host declaration find, save the Host name 
                if (line.matches(SshToken.HOST.getValue() + " .*")) {
                    host = cutAt(SshToken.HOST.getValue(), line);
                    host = getRealHostname(host);
                    line = br.readLine();

                    // Get all information about the Host                    
                    while (line != null && !line.matches(SshToken.HOST.getValue() + " .*")) {
                        if (line.startsWith("#")) {
                            line = br.readLine();
                            continue;
                        }
                        definitions.add(line);
                        line = br.readLine();
                    }
                    // Then process parsing for a specific Host declaration and 
                    // all related options 
                    processHostDefinition(host, definitions, storer);
                } else {
                    // Skip ssh configuration's line and blank line
                    line = br.readLine();
                }
            }
        } catch (IOException e) {
            logger.error("Can't open SSH configuration file" + path, e);
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Split the hosts and it's gateway/port and store it in the <code>storer</code>'s map table
     * @return 
     */
    public void parseProperties(String properties, SshConfigStorer storer) {
        String[] proxies = properties.split(";");
        for (int i = 0; i < proxies.length; i++) {
            String[] gateways = proxies[i].split(":");
            switch (gateways.length) {
                case 3:
                    if (gateways[0]
                            .matches("^.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\/[1-3]?[0-9]")) {
                        storer.addSubnetInformation(gateways[0], gateways[1]);
                        storer.addHostInformation(gateways[1], SshToken.PORT, gateways[2]);
                    } else {
                        storer.addHostInformation(gateways[0], SshToken.GATEWAY, gateways[1]);
                        storer.addHostInformation(gateways[1], SshToken.PORT, gateways[2]);
                    }
                    break;
                case 2:
                    if (gateways[1].equalsIgnoreCase("none")) {
                        storer.addHostInformation(gateways[0], SshToken.GATEWAY, "none");
                        break;
                    }
                default:
                    logger
                            .error("ERROR: malformed gateway declaration. Should be host1.domain1:gateway1:port;*.domain2:host2:port2");
                    continue;
            }
        }
    }

    private String getRealHostname(String host) {
        String real = realHostName.get(host);
        if (real != null)
            return real;
        return host;
    }

    private void processHostDefinition(String host, List<String> definitions, SshConfigStorer storer) {
        String line = null;
        SshToken tok;
        for (int i = 0; i < definitions.size(); i++) {
            line = definitions.get(i);

            switch (tok = getToken(line)) {
                case HOSTNAME:
                    storeHostname(host, cutAt(SshToken.HOSTNAME.getValue(), line), storer);
                    break;

                case GATEWAY:
                    storeProxyCommand(host, cutAt(SshToken.GATEWAY.getValue(), line), storer);
                    break;

                case UNKNOW:
                    // Do nothing except debug notification                
                    if (logger.isDebugEnabled() && !line.isEmpty()) {
                        logger.debug("Ssh parser : unhandled field at line : " + line);
                    }
                    break;

                default:
                    defaultStore(host, line, tok, storer);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Processing the storage of information
    ////////////////////////////////////////////////////////////////////////////////

    // Default

    // This method is the default one to use when no extra processing is needed
    // this will simply store the value in the right place
    private void defaultStore(String host, String line, SshToken tok, SshConfigStorer storer) {
        storer.addHostInformation(host, tok, cutAt(tok.getValue(), line));
    }

    // Special case

    // Store the real host name for <code> host </code>
    // And change the old one if stored 
    private void storeHostname(String host, String hostname, SshConfigStorer storer) {
        this.realHostName.put(host, hostname);
        storer.changeHostname(host, hostname);
    }

    private void storeProxyCommand(String host, String proxyCommand, SshConfigStorer storer) {

        // Skip SSH command  
        proxyCommand = cutAt("ssh", proxyCommand);
        // Skip SSH specific options
        while (proxyCommand.indexOf("${") >= 0) {
            proxyCommand = cutAt("}", proxyCommand);
        }

        if (proxyCommand.equalsIgnoreCase("none")) {
            storer.addHostInformation(host, SshToken.GATEWAY, proxyCommand);
        } else {
            // Only pick the gateway name
            String[] proxyCommandTokenized = proxyCommand.split(" ");
            storer.addHostInformation(getRealHostname(host), SshToken.GATEWAY,
                    getRealHostname(proxyCommandTokenized[0].trim()));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////
    // Utility for String parsing
    ////////////////////////////////////////////////////////////

    /**
     * Return <code> line </code>, cut after<code> word </code>.
     * This method doesn't take care about the case
     */
    private static String cutAt(String word, String line) {
        String lowerLine = line.toLowerCase();
        String lowerWord = word.toLowerCase();
        if (lowerLine.contains(lowerWord)) {
            return line.substring(lowerLine.indexOf(lowerWord) + word.length()).trim();
        } else
            return line;
    }

    /**
     * Return a token representing the field of the ssh configuration file only 
     * if set as wanted in constructor
     * 
     */
    private static SshToken getToken(String line) {
        SshToken[] tokens = SshToken.values();
        String firstWord = line.trim().split(" ")[0];
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].getValue().equalsIgnoreCase(firstWord) && tokens[i].isWanted())
                return tokens[i];
        }
        return SshToken.UNKNOW;
    }

    /////////////////////////////////////////////////////////////

    // Only for test
    public static void main(String[] args) {
        SshConfigStorer storer = new SshConfigStorer();
        SshConfigFileParser parser = new SshConfigFileParser(storer);

        System.out.println(storer.getGateway("azur-7.sophia.grid5000.fr"));
        System.out.println(storer.getUsername("foo.grid5000.fr"));
        System.out.println(storer.getGateway("bar.inria.fr"));
    }
}
