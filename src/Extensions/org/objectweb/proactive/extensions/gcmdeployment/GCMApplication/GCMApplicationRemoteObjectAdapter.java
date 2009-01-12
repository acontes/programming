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
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMA_LOGGER;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.remoteobject.adapter.Adapter;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.core.xml.VariableContract;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.gcmdeployment.Topology;


public class GCMApplicationRemoteObjectAdapter extends Adapter<GCMApplication> implements GCMApplication {
    long deploymentId;
    Set<String> virtualNodeNames;
    URI baseUri;

    @Override
    protected void construct() {
        deploymentId = target.getDeploymentId();
        virtualNodeNames = target.getVirtualNodeNames();
        baseUri = URIBuilder.buildURI(ProActiveInet.getInstance().getHostname(), "",
                PAProperties.PA_COMMUNICATION_PROTOCOL.getValue());
    }

    public VariableContract getVariableContract() {
        return target.getVariableContract();
    }

    public GCMVirtualNode getVirtualNode(String vnName) {
        GCMVirtualNode vn = null;
        long deploymentId = target.getDeploymentId();
        String name = deploymentId + "/VirtualNode/" + vnName;

        URI uri = URIBuilder.buildURI(baseUri.getHost(), name, baseUri.getScheme(), baseUri.getPort());
        try {
            RemoteObject ro = RemoteObjectHelper.lookup(uri);
            vn = (GCMVirtualNode) RemoteObjectHelper.generatedObjectStub(ro);
        } catch (ProActiveException e) {
            GCMA_LOGGER.error("Virtual Node \"" + vnName + "\" is not exported as " + uri);
        }
        return vn;
    }

    public Map<String, GCMVirtualNode> getVirtualNodes() {
        Map<String, GCMVirtualNode> map = new HashMap<String, GCMVirtualNode>();

        for (String vnName : virtualNodeNames) {
            map.put(vnName, this.getVirtualNode(vnName));
        }

        return map;
    }

    public boolean isStarted() {
        return target.isStarted();
    }

    public void kill() {
        target.kill();
    }

    public void startDeployment() {
        target.startDeployment();
    }

    public void updateTopology(Topology topology) throws ProActiveException {
        target.updateTopology(topology);
    }

    public void waitReady() {
        target.waitReady();
    }

    public long getDeploymentId() {
        return target.getDeploymentId();
    }

    public Set<String> getVirtualNodeNames() {
        return target.getVirtualNodeNames();
    }

    public URL getDescriptorURL() {
        return target.getDescriptorURL();
    }

    public List<Node> getAllNodes() {
        return target.getAllNodes();
    }

    public String getDebugInformation() {
        return target.getDebugInformation();
    }

    public Topology getTopology() throws ProActiveException {
        return target.getTopology();
    }

    public ProActiveSecurityManager getProActiveApplicationSecurityManager() {
        return target.getProActiveApplicationSecurityManager();
    }

    public void setProActiveApplicationSecurityManager(
            ProActiveSecurityManager proactiveApplicationSecurityManager) {
        target.setProActiveApplicationSecurityManager(proactiveApplicationSecurityManager);

    }

    public void waitReady(long timeout) throws ProActiveTimeoutException {
        target.waitReady(timeout);
    }

}
