package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;


/**
 *
 * TODO (long term) Transform this class into a real tree.
 *         This implementation duplicates Bridge and Tree like in the previous
 *  ProActive deployment framework.
 *
 * TODO Allow to start a command on intermediate Bridges
 *
 */
public class GCMDeploymentInfrastructure {
    private List<Group> groups = Collections.synchronizedList(new ArrayList<Group>());
    private List<Bridge> bridges = Collections.synchronizedList(new ArrayList<Bridge>());
    private HostInfo hostInfo;

    public List<Group> getGroups() {
        return groups;
    }

    public List<Bridge> getBridges() {
        return bridges;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void addBrige(Bridge bridge) {
        bridges.add(bridge);
    }

    protected HostInfo getHostInfo() {
        return hostInfo;
    }

    protected void setHostInfo(HostInfo hostInfo) {
        assert (hostInfo == null);
        this.hostInfo = hostInfo;
    }

    public void check() throws IllegalStateException {
        for (Group group : groups)
            group.check();

        for (Bridge bridge : bridges)
            bridge.check();

        hostInfo.check();
    }
}
