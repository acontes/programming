/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Job;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.mop.ConstructionOfProxyObjectFailedException;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.MOPException;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.core.runtime.VMInformation;


/**
 * <p>
 * A <code>Node</code> offers a set of services needed by ProActive to work with
 * remote JVM. Each JVM that is aimed to hold active objects should contains at least
 * one instance of the node class. That instance, when created, will be registered
 * to some registry where it is possible to perform a lookup (such as the RMI registry).
 * </p><p>
 * When ProActive needs to interact with a remote JVM, it will lookup for one node associated
 * with that JVM (using typically the RMI Registry) and use this node to perform the interaction.
 * </p><p>
 * We expect several concrete implementations of the Node to be wrtten such as a RMI node, a HTTP node ...
 * </p>
 *
 * @author The ProActive Team
 * @version 1.1,  2002/08/28
 * @since   ProActive 0.9
 *
 */

public class NodeImpl implements Node, Serializable {

    protected NodeInformation nodeInformation;
    protected ProActiveRuntime proActiveRuntime;
    protected String vnName;

    //
    // ----------Constructors--------------------
    //
    public NodeImpl() {
    }

    public NodeImpl(ProActiveRuntime proActiveRuntime, String nodeURL, String protocol, String jobID) {
        this.proActiveRuntime = proActiveRuntime;
        this.nodeInformation = new NodeInformationImpl(nodeURL, protocol, jobID);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodeInformation == null) ? 0 : nodeInformation.hashCode());
        result = prime * result + ((vnName == null) ? 0 : vnName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NodeImpl other = (NodeImpl) obj;
        if (nodeInformation == null) {
            if (other.nodeInformation != null)
                return false;
        } else if (!nodeInformation.equals(other.nodeInformation))
            return false;
        if (vnName == null) {
            if (other.vnName != null)
                return false;
        } else if (!vnName.equals(other.vnName))
            return false;
        return true;
    }

    //
    //--------------------------Implements Node-----------------------------

    /**
     * @see org.objectweb.proactive.core.node.Node#getNodeInformation()
     */
    public NodeInformation getNodeInformation() {
        return nodeInformation;
    }

    /**
     * @see org.objectweb.proactive.core.node.Node#getProActiveRuntime
     */
    public ProActiveRuntime getProActiveRuntime() {
        return proActiveRuntime;
    }

    /**
     * @see org.objectweb.proactive.core.node.Node#getActiveObjects()
     */
    public Object[] getActiveObjects() throws NodeException, ActiveObjectCreationException {
        List<UniversalBody> bodyArray;
        try {
            bodyArray = this.proActiveRuntime.getActiveObjects(this.nodeInformation.getName());
        } catch (ProActiveException e) {
            throw new NodeException("Cannot get Active Objects registered on this node: " +
                this.nodeInformation.getURL(), e);
        }
        if (bodyArray.size() == 0) {
            return new Object[0];
        } else {
            Object[] stubOnAO = new Object[bodyArray.size()];
            for (int i = 0; i < bodyArray.size(); i++) {
                UniversalBody body = bodyArray.get(i);
                String className = body.getReifiedClassName();
                try {
                    stubOnAO[i] = createStubObject(className, body);
                } catch (MOPException e) {
                    throw new ActiveObjectCreationException(
                        "Exception occured when trying to create stub-proxy", e);
                }
            }
            return stubOnAO;
        }
    }

    /**
     * @see org.objectweb.proactive.core.node.Node#getNumberOfActiveObjects()
     */
    public int getNumberOfActiveObjects() throws NodeException {
        List<UniversalBody> bodyArray;
        try {
            bodyArray = this.proActiveRuntime.getActiveObjects(this.nodeInformation.getName());
        } catch (ProActiveException e) {
            throw new NodeException("Cannot get Active Objects registered on this node: " +
                this.nodeInformation.getURL(), e);
        }
        return bodyArray.size();
    }

    /**
     * @see org.objectweb.proactive.core.node.Node#getActiveObjects(String)
     */
    public Object[] getActiveObjects(String className) throws NodeException, ActiveObjectCreationException {
        List<UniversalBody> bodyArray;
        try {
            bodyArray = this.proActiveRuntime.getActiveObjects(this.nodeInformation.getName(), className);
        } catch (ProActiveException e) {
            throw new NodeException("Cannot get Active Objects of type " + className +
                " registered on this node: " + this.nodeInformation.getURL(), e);
        }
        if (bodyArray.size() == 0) {
            throw new NodeException("no ActiveObjects of type " + className +
                " are registered for this node: " + this.nodeInformation.getURL());
        } else {
            Object[] stubOnAO = new Object[bodyArray.size()];
            for (int i = 0; i < bodyArray.size(); i++) {
                UniversalBody body = bodyArray.get(i);
                try {
                    stubOnAO[i] = createStubObject(className, body);
                } catch (MOPException e) {
                    throw new ActiveObjectCreationException(
                        "Exception occured when trying to create stub-proxy", e);
                }
            }
            return stubOnAO;
        }
    }

    private void readObject(ObjectInputStream in) throws java.io.IOException, ClassNotFoundException,
            ProActiveException {
        in.defaultReadObject();
        if (NodeFactory.isNodeLocal(this)) {
            this.proActiveRuntime = RuntimeFactory.getProtocolSpecificRuntime(nodeInformation.getProtocol());
        }
    }

    // -------------------------------------------------------------------------------------------
    //
    // STUB CREATION
    //
    // -------------------------------------------------------------------------------------------
    private static Object createStubObject(String className, UniversalBody body) throws MOPException {
        return createStubObject(className, null, new Object[] { body });
    }

    private static Object createStubObject(String className, Object[] constructorParameters,
            Object[] proxyParameters) throws MOPException {
        try {
            return MOP.newInstance(className, (Class[]) null, constructorParameters,
                    Constants.DEFAULT_BODY_PROXY_CLASS_NAME, proxyParameters);
        } catch (ClassNotFoundException e) {
            throw new ConstructionOfProxyObjectFailedException("Class can't be found e=" + e);
        }
    }

    //
    //------------------------INNER CLASS---------------------------------------
    //
    protected class NodeInformationImpl implements NodeInformation {

        private String nodeName;
        private String nodeURL;
        private String protocol;
        private String jobID;
        private VMInformation vmInformation;

        public NodeInformationImpl(String url, String protocol, String jobID) {
            this.nodeURL = url;
            this.protocol = protocol;
            this.nodeName = extractNameFromUrl(url);
            this.jobID = (jobID != null) ? jobID : Job.DEFAULT_JOBID;

            this.vmInformation = proActiveRuntime.getVMInformation();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
            result = prime * result + ((vmInformation == null) ? 0 : vmInformation.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof NodeInformationImpl))
                return false;
            NodeInformationImpl other = (NodeInformationImpl) obj;
            if (nodeName == null) {
                if (other.nodeName != null)
                    return false;
            } else if (!nodeName.equals(other.nodeName))
                return false;
            if (vmInformation == null) {
                if (other.vmInformation != null)
                    return false;
            } else if (!vmInformation.equals(other.vmInformation))
                return false;
            return true;
        }

        /**
         * @see org.objectweb.proactive.core.node.NodeInformation#getName()
         */
        public String getName() {
            return nodeName;
        }

        /**
         * @see org.objectweb.proactive.core.node.NodeInformation#getProtocol()
         */
        public String getProtocol() {
            return protocol;
        }

        /**
         * @see org.objectweb.proactive.core.node.NodeInformation#getURL()
         */
        public String getURL() {
            return nodeURL;
        }

        /**
         * Returns the name specified in the url
         * @param url. The url of the node
         * @return String. The name of the node
         */
        private String extractNameFromUrl(String url) {
            int n = url.lastIndexOf("/");
            String name = url.substring(n + 1);
            return name;
        }

        /**
         * @see org.objectweb.proactive.Job#getJobID()
         */
        public String getJobID() {
            return jobID;
        }

        /**
         * Change the Job ID of this node. 
         * @see org.objectweb.proactive.Job
         * @param jobId The new JobID
         */
        public void setJobID(String jobId) {
            this.jobID = jobId;
        }

        public VMInformation getVMInformation() {
            return vmInformation;
        }
    }

    // SECURITY

    /**
     *
     * @throws IOException
     * @see org.objectweb.proactive.core.node.Node#killAllActiveObjects()
     */
    public void killAllActiveObjects() throws NodeException, IOException {
        List<UniversalBody> bodyArray;
        try {
            bodyArray = this.proActiveRuntime.getActiveObjects(this.nodeInformation.getName());
        } catch (ProActiveException e) {
            throw new NodeException("Cannot get Active Objects registered on this node: " +
                this.nodeInformation.getURL(), e);
        }

        for (UniversalBody body : bodyArray) {
            try {
                // reify for remote terminate
                PAActiveObject
                        .terminateActiveObject(MOP.createStubObject(Object.class.getName(), body), true);
            } catch (MOPException e) {
                // Bad error handling but terminateActiveObject eat remote exceptions
                throw new IOException("Cannot contact Active Objects on this node: " +
                    this.nodeInformation.getURL() + " caused by " + e.getMessage());
            }
        }
    }

    /**
     * @see org.objectweb.proactive.core.node.Node#setProperty(java.lang.String, java.lang.String)
     */
    public Object setProperty(String key, String value) throws ProActiveException {
        return this.proActiveRuntime.setLocalNodeProperty(this.nodeInformation.getName(), key, value);
    }

    /**
     * @see org.objectweb.proactive.core.node.Node#getProperty(java.lang.String)
     */
    public String getProperty(String key) throws ProActiveException {
        return this.proActiveRuntime.getLocalNodeProperty(this.nodeInformation.getName(), key);
    }

    public VMInformation getVMInformation() {
        return proActiveRuntime.getVMInformation();
    }
}
