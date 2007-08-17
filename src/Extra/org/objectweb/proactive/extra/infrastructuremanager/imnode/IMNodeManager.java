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

import java.util.ArrayList;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend.NodeSourceInterface;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


/**
 * This Interface is specified to handle IMNodes in the Infrastructure Manager.
 * IMNodeManager isn't thread-safe, so be carefull not to access more that one at a time.
 *
 * @author ProActive Team
 * @version 1.0, Jul 11, 2007
 * @since ProActive 3.2
 */
public interface IMNodeManager extends NodeSourceInterface{

    // SETTING NODE STATE
    /**
     * Set the {@link IMNode} in a busy state.
     * There is nothing to do more than that, like expressly setting
     * busy state <i>a mano</i>.
     * @param imnode
     */
    public void setBusy(IMNode imnode);

    /**
     * Set the {@link IMNode} in a free state.
     * A free node can be used by IM.
     * @param imnode
     */
    public void setFree(IMNode imnode);

    /**
     * Set the {@link IMNode} in a down state.
     * A Node is down when it's no longer responding.
     * @param imnode
     */
    public void setDown(IMNode imnode);

    // SETTING SCRIPT VERIFICATION
    /**
     * That's the way to say to the NodeManager that a Node verifies a script.
     * This will help ordering nodes for future calls to {@link #getNodesByScript(VerifyingScript)}.
     * @param imnode
     * @param script
     */
    public void setVerifyingScript(IMNode imnode, VerifyingScript script);

    /**
     * That's the way to say to the NodeManager that a Node doesn't (or no longer) verifie a script.
     * This will help ordering nodes for future calls to {@link #getNodesByScript(VerifyingScript)}.
     * @param imnode
     * @param script
     */
    public void setNotVerifyingScript(IMNode imnode, VerifyingScript script);

    /**
     * The way to to get free nodes in the structure, ordered (or not) with the script.
     * The more a Node has chances to verify the script, the less it's far in the list.
     */
    public ArrayList<IMNode> getNodesByScript(VerifyingScript script, boolean ordered);
    
    /**
     * Shutting down Node Manager, and everything depending on it.
     */
    public BooleanWrapper shutdown();
}
