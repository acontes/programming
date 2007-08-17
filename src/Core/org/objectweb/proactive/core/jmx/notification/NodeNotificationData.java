/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.jmx.notification;

import java.io.Serializable;


/**
 * Used in the JMX notifications
 * @author ProActive Team
 */
public class NodeNotificationData implements Serializable {

    /** The url of the node */
    private String nodeUrl;

    /** The protocol of the node */
    private String protocol;

    /** The host where the node has been created */
    private String hostName;

    /** The url of the runtime which contains the node */
    private String runtimeUrl;

    /** The jobID associated with this node */
    private String jobId;

    public NodeNotificationData() {
        // No args constructor
    }

    /**
     * Creates a new NodeNotificationData
     * @param nodeUrl The url of the node
     * @param protocol The protocol used to crear
     * @param hostName
     * @param runtimeUrl
     * @param jobId
     */
    public NodeNotificationData(String nodeUrl, String protocol,
        String hostName, String runtimeUrl, String jobId) {
        this.nodeUrl = nodeUrl;
        this.protocol = protocol;
        this.hostName = hostName;
        this.runtimeUrl = runtimeUrl;
        this.jobId = jobId;
    }

    /**
    * Returns the host where the node has been created
    * @return the host where the node has been created
    */
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Returns the jobID associated with this node
     * @return the jobID associated with this node
     */
    public String getJobId() {
        return this.jobId;
    }

    /**
     * Returns the url of the node.
     * @return the url of the node.
     */
    public String getNodeUrl() {
        return this.nodeUrl;
    }

    /**
     * Returns the url of the runtime which contains this node.
     * @return the url of the runtime which contains this node.
     */
    public String getRuntimeUrl() {
        return runtimeUrl;
    }

    /**
     * Returns the protocol of the node
     * @return the protocol of the node
     */
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public String toString() {
        return "node on the host: " + this.hostName + ", runtime: " +
        this.runtimeUrl + ", node: " + this.nodeUrl + ", protocol: " +
        this.protocol + ", jobID: " + this.jobId;
    }
}
