package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.util.Map;

import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;


public class GCMDeploymentInfrastructure {
    private Map<String, Group> groups;
    private Map<String, Bridge> bridges;
    private Map<String, HostInfo> hosts;

    public Map<String, Group> getGroups() {
        return groups;
    }

    public Map<String, Bridge> getBridges() {
        return bridges;
    }

    public Map<String, HostInfo> getHosts() {
        return hosts;
    }

    public void addGroup(Group group) {
        groups.put(group.getId(), group);
    }

    public void addBrige(Bridge bridge) {
        bridges.put(bridge.getId(), bridge);
    }

    public void addHost(HostInfo host) {
        hosts.put(host.getId(), host);
    }
}
