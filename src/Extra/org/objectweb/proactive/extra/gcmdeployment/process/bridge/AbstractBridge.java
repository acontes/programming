package org.objectweb.proactive.extra.gcmdeployment.process.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.proactive.extra.gcmdeployment.PathElement;
import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;


public abstract class AbstractBridge implements Bridge {
    private PathElement commandPath;
    private String env;
    private String hostname;
    private String username;
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /* ------
     * Infrastructure tree operations & data
     */
    private List<Bridge> bridges = Collections.synchronizedList(new ArrayList<Bridge>());
    private List<Group> groups = Collections.synchronizedList(new ArrayList<Group>());
    private HostInfo hostInfo = null;

    public void addBridge(Bridge bridge) {
        bridges.add(bridge);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public List<Bridge> getBridges() {
        return bridges;
    }

    public List<Group> getGroups() {
        return groups;
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

    public void check() throws IllegalStateException {
        if (hostname == null) {
            throw new IllegalStateException("hostname is not set in " + this);
        }

        if (id == null) {
            throw new IllegalStateException("id is not set in " + this);
        }

        for (Bridge bridge : bridges)
            bridge.check();

        for (Group group : groups)
            group.check();

        if (hostInfo == null) {
            throw new IllegalStateException("hostInfo is not set in " + this);
        }
        hostInfo.check();
    }

    public abstract Object clone() throws CloneNotSupportedException;
}
