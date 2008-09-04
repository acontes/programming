/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.bridge.Bridge;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group.Group;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group.JavaGroup;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;


public class GCMDeploymentResources {
    private List<Group> groups = Collections.synchronizedList(new ArrayList<Group>());
    private List<JavaGroup> javaGroups = Collections.synchronizedList(new ArrayList<JavaGroup>());
    private List<Bridge> bridges = Collections.synchronizedList(new ArrayList<Bridge>());
    private HostInfo hostInfo;

    public List<Group> getGroups() {
        return groups;
    }

    public List<JavaGroup> getJavaGroups() {
        return javaGroups;
    }

    public List<Bridge> getBridges() {
        return bridges;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void addJavaGroup(JavaGroup javaGroup) {
        javaGroups.add(javaGroup);        
    }

    public void addBridge(Bridge bridge) {
        bridges.add(bridge);
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    protected void setHostInfo(HostInfo hostInfo) {
        assert (this.hostInfo == null);
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
