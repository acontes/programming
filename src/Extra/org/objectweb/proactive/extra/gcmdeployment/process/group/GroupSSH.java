package org.objectweb.proactive.extra.gcmdeployment.process.group;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.objectweb.proactive.extra.gcmdeployment.process.ListGenerator;


public class GroupSSH extends AbstractGroup {
    public final static String DEFAULT_SSHPATH = "ssh";
    private String hostList;
    private String domain;
    private String username;

    public GroupSSH() {
        hostList = "";
    }

    public GroupSSH(GroupSSH groupSSH) {
        super(groupSSH);
        this.hostList = groupSSH.hostList;
        this.domain = groupSSH.domain;
        this.username = groupSSH.username;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new GroupSSH(this);
    }

    public void setHostList(String hostList) {
        this.hostList = hostList;
    }

    @Override
    public List<String> internalBuildCommands() {
        StringTokenizer tokenizer = new StringTokenizer(hostList);

        List<String> commands = new ArrayList<String>();

        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();

            List<String> names = ListGenerator.generateNames(nextToken);
            for (String hostname : names) {
                String command = makeSingleCommand(hostname);
                commands.add(command);
            }
        }

        return commands;
    }

    /**
     * return something like
     *
     * ssh -l username hostname.domain
     *
     * @param hostname
     * @return
     */
    private String makeSingleCommand(String hostname) {
        StringBuilder res = new StringBuilder(getCommandPath());

        if (username != null) {
            res.append("-l ").append(username);
        }

        res.append(" ").append(hostname);

        if (domain != null) {
            res.append(".").append(domain);
        }

        return res.toString();
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public String getUsername() {
        return username;
    }
}
