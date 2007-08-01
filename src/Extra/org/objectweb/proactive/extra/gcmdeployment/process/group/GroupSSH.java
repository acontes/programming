package org.objectweb.proactive.extra.gcmdeployment.process.group;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.objectweb.proactive.extra.gcmdeployment.process.ListGenerator;


public class GroupSSH extends AbstractGroup {
    public final static String DEFAULT_SSHPATH = "ssh";
    private String hostList;

    public GroupSSH() {
        hostList = "";
    }

    public GroupSSH(GroupSSH groupSSH) {
        super(groupSSH);
        this.hostList = groupSSH.hostList;
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
                String command = getCommandPath() + " " + hostname;
                commands.add(command);
            }
        }

        return commands;
    }
}
