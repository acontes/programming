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
package org.objectweb.proactive.extra.infrastructuremanager.nodesource;

import java.util.HashMap;

import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


/**
 * A node Source is an entity that can provide nodes
 * to the Infrastructure Manager.
 * But the given nodes may be available only for some short time.
 * This is specially designed to be used with a ProActive P2P Network.
 *
 * @author proactive
 */
public abstract class IMNodeSource implements IMNodeManager {

    /**
     * String identifying the NodeSource. This must be unique.
     * @return
     */
    public abstract String getSourceId();

    @Override
    public int hashCode() {
        return getSourceId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IMNodeSource) {
            IMNodeSource o2 = (IMNodeSource) o;
            return getSourceId().equals(o2.getSourceId());
        }
        return false;
    }

    /**
     * Update the node status.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#setVerifyingScript(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode, org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript)
     */
    public void setVerifyingScript(IMNode imnode, VerifyingScript script) {
        HashMap<VerifyingScript, Integer> verifs = imnode.getScriptStatus();
        if (verifs.containsKey(script)) {
            verifs.remove(script);
        }
        verifs.put(script, IMNode.VERIFIED_SCRIPT);
    }

    /**
     * Update the node status.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#setNotVerifyingScript(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode, org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript)
     */
    public void setNotVerifyingScript(IMNode imnode, VerifyingScript script) {
        HashMap<VerifyingScript, Integer> verifs = imnode.getScriptStatus();
        if (verifs.containsKey(script)) {
            int status = verifs.remove(script);
            if (status == IMNode.NOT_VERIFIED_SCRIPT) {
                verifs.put(script, IMNode.NOT_VERIFIED_SCRIPT);
            } else {
                verifs.put(script, IMNode.NO_LONGER_VERIFIED_SCRIPT);
            }
        } else {
            verifs.put(script, IMNode.NOT_VERIFIED_SCRIPT);
        }
    }
}
