package org.objectweb.proactive.extra.gcmdeployment.process.bridge;

public class BridgeSSH extends AbstractBridge {
    public final static String DEFAULT_SSHPATH = "ssh";

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String internalBuildCommand() {
        StringBuilder command = new StringBuilder();
        command.append(getCommandPath());
        // append username
        if (getUsername() != null) {
            command.append(" -l ");
            command.append(getUsername());
        }

        // append host
        command.append(" ");
        command.append(getHostname());
        command.append(" ");

        return command.toString();
    }
}
