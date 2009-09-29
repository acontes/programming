package org.objectweb.proactive.core.ssh;

import static org.objectweb.proactive.core.ssh.SSH.logger;

import java.util.Hashtable;
import java.util.Map;

import org.objectweb.proactive.core.ssh.SshConfigFileParser.SshToken;


/**
 * This class store all information read both in ssh configuration file 
 * and in the PA_SSH_PROXY_GATEWAY PAProperties
 *
 */
public class SshConfigStorer extends SshConfig {
    /**
     * A Map of String and a Map of Token,String is used to represent the ssh configuration file,
     * information are stored for host (key of the global map table) and for each host,
     * each field of the Ssh configuration file are represent by a SshToken in an inner map table
     * 
     * easily extensible, only add a new token the SshToken, and fill the switch/case with the right 
     * method.         
     */
    private Map<String, Map<SshToken, String>> sshInfos;
    private SshToken[] capabilities = { SshToken.HOSTNAME, SshToken.USERNAME, SshToken.PORT,
            SshToken.PRIVATEKEY, SshToken.GATEWAY };
    private SubnetChecker subnetChecker = new SubnetChecker();

    public SshConfigStorer() {
        super();
        this.sshInfos = new Hashtable<String, Map<SshToken, String>>();
        // TODO Set properties in an elegant way
        this.setTryPlainSocket(true);
        this.setTryProxyCommand(true);
    }

    public SshToken[] getCapabilities() {
        return capabilities;
    }

    /**
     * This method store in the Maptable the <code> information </code> (gateway name, port,
     * username, ...) to use for contacting the host (<code> hostname </code>)
     * 
     * @see SshProxy
     * 
     * @param hostname
     *            The host to contact
     * 
     * @param request
     *            The field to set (username,port,gateway, ...) declare with an enum SshToken
     * 
     * @param information
     *            The value of the field
     *                       
     */
    public void addHostInformation(String hostname, SshToken request, String information) {
        if (hostname.charAt(0) == '*') {
            hostname = hostname.substring(1);
        }
        Map<SshToken, String> hostsInfos = sshInfos.get(hostname);

        if (hostsInfos == null) {
            // No information already record for the host
            hostsInfos = new Hashtable<SshToken, String>();
            hostsInfos.put(request, information);
            sshInfos.put(hostname, hostsInfos);
        } else {
            if (hostsInfos.get(request) != null) {
                if (logger.isDebugEnabled()) {
                    logger.info("Ssh configuration : information " + information + " as " +
                        request.toString().toLowerCase() + " for " + hostname +
                        " is already declared, ignored");
                }
                return;
            }
            hostsInfos.put(request, information);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Ssh configuration : " + information + " as " + request.toString().toLowerCase() +
                " stored for " + hostname);
        }
    }

    /**
     * Do a wildcarded search in the maptable and return all the information
     * related to the machine <code> hostname </code>
     * 
     * @param hostname
     *          the name of the machine on which information are requested   
     * @return 
     *          all information stored as a Map table of SshToken and String
     *           
     */
    public Map<SshToken, String> getHostInformation(String hostname) {
        Map<SshToken, String> hostInfos;
        int index;
        while (hostname.contains(".")) {
            hostInfos = sshInfos.get(hostname);
            if (hostInfos != null) {
                return hostInfos;
            } else {
                // eat the first point
                hostname = hostname.substring(1);
                index = hostname.indexOf('.');
                if (index < 0)
                    break;
                hostname = hostname.substring(hostname.indexOf('.'));
            }
        }
        hostInfos = sshInfos.get(hostname);
        return hostInfos;
    }

    /**
     * Replace host by hostname in the map table because 
     * ssh allow to use nickname in it configuration file 
     * 
     * @param host
     *           nickname
     *              
     * @param hostname
     *          real host name
     *  
     */
    public void changeHostname(String host, String hostname) {
        Map<SshToken, String> infos = sshInfos.get(host);
        if (infos != null) {
            sshInfos.remove(host);
            sshInfos.put(hostname, infos);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    // ACCESS METHOD 
    //////////////////////////////////////////////////////////////////////////////////

    // Default method    
    public String getInformation(String host, SshToken tok) {
        Map<SshToken, String> hostsInfos = getHostInformation(host);
        if (hostsInfos != null) {
            return hostsInfos.get(tok);
        }
        return null;
    }

    /**
     * Never return null, if no information stored, return system username
     */
    public String getUsername(String host) {
        String user = getInformation(host, SshToken.USERNAME);
        if (user == null)
            return System.getProperty("user.name");
        return user;
    }

    public String getGateway(String host) {
        String hostname = host;

        // Special case for protocol like RMI, which use ip address for answer
        if (host.matches("^.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
            String subnetTest = subnetChecker.getGateway(host);
            if (subnetTest != null)
                return subnetTest;
        }

        String gateway = getInformation(hostname, SshToken.GATEWAY);
        if (gateway == null || gateway.equalsIgnoreCase("none"))
            return null;
        return gateway;
    }

    /**
     * Never return null, if no information stored, return ssh default port : 22
     */
    @Override
    public int getPort(String host) {
        String port = getInformation(host, SshToken.PORT);
        if (port != null)
            return Integer.parseInt(port);
        else
            return 22;
    }

    public String getPrivateKeyPath(String host) {
        return getInformation(host, SshToken.PRIVATEKEY);
    }

    public void addSubnetInformation(String subnet, String gateway) {
        this.subnetChecker.setGateway(subnet, gateway);
    }
}
