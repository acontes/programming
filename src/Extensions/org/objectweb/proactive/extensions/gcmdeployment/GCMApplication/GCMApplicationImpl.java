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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.DeploymentMap;
import org.objectweb.proactive.extensions.gcmdeployment.core.DeploymentTreeBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyRootImpl;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.gcmdeployment.Topology;


public class GCMApplicationImpl<Profile extends Application> implements GCMApplicationInternal<Profile> {
    //    static private Map<Long, GCMApplication> localDeployments = new HashMap<Long, GCMApplication>();

    /** An unique identifier for this deployment */
    final private long deploymentId;

    /** Location of the application descriptor file */
    final private URL descriptor;

    /** GCM Application parser (statefull) */
    final private GCMApplicationParser<Profile> parser;

    /** The embedded application 
     * 
     * All application specific operation are done by this application by using
     * a proxy pattern 
     */
    final private Profile application;

    /** All Node Providers referenced by the Application descriptor */
    private Map<String, NodeProvider> nodeProviders = null;

    /** The deployment tree of this application*/
    final private TopologyRootImpl deploymentTree;

    /** A map <topologyId, NodeProvider> to enable quick lookups when only the topologyId is known */
    final private DeploymentMap deploymentMap;

    /** The variable contract between the application and the descriptors */
    private VariableContractImpl vContract;

    public GCMApplicationImpl(String filename) throws ProActiveException, MalformedURLException {
        this(new URL("file", null, filename), null);
    }

    public GCMApplicationImpl(String filename, VariableContractImpl vContract) throws ProActiveException,
            MalformedURLException {
        this(new URL("file", null, filename), vContract);
    }

    public GCMApplicationImpl(URL file) throws ProActiveException {
        this(file, null);
    }

    public GCMApplicationImpl(URL file, VariableContractImpl vContract) throws ProActiveException {
        if (file == null) {
            throw new ProActiveException("Failed to create GCM Application: URL cannot be null !");
        }

        try {
            file.openStream();
        } catch (IOException e) {
            throw new ProActiveException("Failed to create GCM Application: URL " + file.toString() +
                " cannot be opened", e);
        }

        try {
            if (vContract == null) {
                vContract = new VariableContractImpl();
            }
            this.vContract = vContract;
            this.descriptor = file;

            this.deploymentId = ProActiveRandom.nextPosLong();

            // vContract will be modified by the Parser to include variable defined in the descriptor
            this.parser = new GCMApplicationParserImpl<Profile>(descriptor, this.vContract);
            this.vContract.close();

            this.application = parser.getApplication();
            this.nodeProviders = parser.getNodeProviders();
            this.deploymentTree = new DeploymentTreeBuilder(this.nodeProviders.values(), this.descriptor)
                    .getDeploymentTree();
            this.deploymentMap = new DeploymentMap(this.nodeProviders, this.deploymentTree);

            // Export this GCMApplication as a remote object
            RemoteObjectExposer<GCMApplication> roe = new RemoteObjectExposer<GCMApplication>(
                GCMApplication.class.getName(), this, GCMApplicationRemoteObjectAdapter.class);
            URI uri = RemoteObjectHelper.generateUrl(deploymentId + "/GCMApplication");
            roe.createRemoteObject(uri);

            this.application.configure(this);

        } catch (Throwable e) {
            throw new ProActiveException("Failed to create GCMApplication: " + e.getMessage() +
                ", see embded message for more details", e);
        }
    }

    /*
     * ----------------------------- GCMApplication interface
     */

    public Profile getProfile() {
        return application;
    }

    public void startDeployment() {
        application.startDeployment();
    }

    public boolean isStarted() {
        return application.isStarted();
    }

    public void kill() {
        application.kill();
    }

    public VariableContractImpl getVariableContract() {
        return this.vContract;
    }

    public URL getDescriptorURL() {
        return descriptor;
    }

    /*
     * ----------------------------- GCMApplicationInternal interface
     */
    public long getDeploymentId() {
        return deploymentId;
    }

    public void addDeployedRuntime(ProActiveRuntime part) {
        // TODO Auto-generated method stub

    }

    public void addNode(Node node) {
        // TODO Auto-generated method stub

    }

    public DeploymentMap getDeploymentMap() {
        return deploymentMap;
    }

    public List<Node> getAllNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDebugInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    public ProActiveSecurityManager getProActiveApplicationSecurityManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public Topology getTopology() throws ProActiveException {
        // TODO Auto-generated method stub
        return null;
    }

    public GCMVirtualNode getVirtualNode(String vnName) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getVirtualNodeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, GCMVirtualNode> getVirtualNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setProActiveApplicationSecurityManager(
            ProActiveSecurityManager proactiveApplicationSecurityManager) {
        // TODO Auto-generated method stub

    }

    public void updateTopology(Topology topology) throws ProActiveException {
        // TODO Auto-generated method stub

    }

    public void waitReady() {
        this.application.waitReady();
    }
}
