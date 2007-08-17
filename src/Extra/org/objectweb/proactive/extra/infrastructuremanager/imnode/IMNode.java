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
package org.objectweb.proactive.extra.infrastructuremanager.imnode;

import java.util.HashMap;

import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeInformation;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.IMNodeSource;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptResult;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


/**
 * The <I>IMNode</I> is a object containing a node and its description.<BR/>
 */
public interface IMNode {
	// STATES
	public static final int NOT_VERIFIED_SCRIPT = 0;
	public static final int NO_LONGER_VERIFIED_SCRIPT = 1;
	public static final int NEVER_TESTED = 2;
	public static final int ALREADY_VERIFIED_SCRIPT = 3;
	public static final int VERIFIED_SCRIPT = 4;
	
	// SCRIPTING

	public ScriptResult<Boolean> executeScript(VerifyingScript script) ;
	
    // ----------------------------------------------------------------------//
    // GET

    /**
     * @return the name of the node
     */
    public String getNodeName();

    /**
     * @return Node
     */
    public Node getNode() throws NodeException;

    /**
     * @return the node information, describing this node
     * @see org.objectweb.proactive.core.node.NodeInformation
     */
    public NodeInformation getNodeInformation() throws NodeException;

    /**
     * @return the name of the virtual node
     */
    public String getVNodeName();

    /**
     * @return the name of the proactive descriptor
     */
    public String getPADName();

    /**
     * This method call nodeInformation.getHostName();
     *
     * @return the name of the host machine
     */
    public String getHostName();

    /**
     * This method call nodeInformation.getDescriptorVMName();
     *
     * @return the name of the virtual machine
     */
    public String getDescriptorVMName();

    // ----------------------------------------------------------------------//
    // STATE ?
    public boolean isFree() throws NodeException;

    public boolean isDown();

    // ----------------------------------------------------------------------//
    // SET
    public void setFree() throws NodeException;

    public void setBusy() throws NodeException;

    public void setDown(boolean down);

    // OTHER SET in the case of the node can migrate.
    // For exemple if the node migrate from other jvm, you must change
    // the attribute Jvm, VNode, ...

    // ----------------------------------------------------------------------//
    // TOSTRING

    /**
     * @return a string describing the IMNode (status, vnode, host, pad, ...)
     */
    public String toString();

    /**
     * Cleaning method : remove all active objects on this node.
     */
	public void clean();
	
	/**
	 * Get a map of all Verifying script allready tested on this node,
	 * and the responses given.
	 * @return the map of Script and status
	 */
	public HashMap<VerifyingScript, Integer> getScriptStatus();
	
	public IMNodeSource getNodeSource();
	
}
