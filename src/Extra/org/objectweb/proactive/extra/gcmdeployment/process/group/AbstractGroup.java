package org.objectweb.proactive.extra.gcmdeployment.process.group;

import org.objectweb.proactive.extra.gcmdeployment.PathElement;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;


public abstract class AbstractGroup implements Group {
    private PathElement commandPath;
    private String env;

    public void setCommandPath(PathElement commandPath) {
        this.commandPath = commandPath;
    }

    public void setEnvironment(String env) {
        this.env = env;
    }

    protected PathElement getCommandPath() {
        return commandPath;
    }

    protected String getEnv() {
        return env;
    }
}
