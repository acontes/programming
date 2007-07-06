package org.objectweb.proactive.extra.gcmdeployment.process.bridge;

import org.objectweb.proactive.extra.gcmdeployment.PathElement;
import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;


public abstract class AbstractBridge implements Bridge {
    private PathElement commandPath;
    private String env;
    private String hostname;
    private String username;

    public void setCommandPath(PathElement commandPath) {
        this.commandPath = commandPath;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEnvironment(String env) {
        this.env = env;
    }

    protected PathElement getCommandPath() {
        return commandPath;
    }

    protected String getHostname() {
        return hostname;
    }

    protected String getUsername() {
        return username;
    }

    protected String getEnv() {
        return env;
    }
}
