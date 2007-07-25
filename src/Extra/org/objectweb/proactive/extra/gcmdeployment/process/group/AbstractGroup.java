package org.objectweb.proactive.extra.gcmdeployment.process.group;

import org.objectweb.proactive.extra.gcmdeployment.PathElement;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;


public abstract class AbstractGroup implements Group {
    private HostInfo hostInfo;
    private PathElement commandPath;
    private String env;
    private String id;

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

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public void check() throws IllegalStateException {
        // 1- hostInfo must be set
        synchronized (hostInfo) {
            if (hostInfo == null) {
                throw new IllegalStateException("hostInfo is not set in " +
                    this);
            }
            hostInfo.check();
        }

        if (id == null) {
            throw new IllegalStateException("id is not set in " + this);
        }
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        synchronized (hostInfo) {
            assert (hostInfo == null);
            this.hostInfo = hostInfo;
        }
    }

    @Override
    public abstract Object clone() throws CloneNotSupportedException;
}
