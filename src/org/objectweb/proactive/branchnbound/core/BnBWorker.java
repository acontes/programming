/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
package org.objectweb.proactive.branchnbound.core;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.runtime.DeployerTag;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


/**
 * @author Alexandre di Costanzo
 *
 * Created on Sep 12, 2006
 */
public class BnBWorker implements InitActive, Serializable {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.BNB_WORKER);
   // private Node localNode;

    /**
     * Empty no args constructor for activing object.
     */
    public BnBWorker() {
        // newActive requires empty no args constructor
    }

    /**
     * @see org.objectweb.proactive.InitActive#initActivity(org.objectweb.proactive.Body)
     */
    public void initActivity(Body body) {
//        try {
//            this.localNode = ProActive.getNode();
//        } catch (NodeException e) {
//            logger.fatal("Cannot get the local node", e);
//            throw new IllegalStateException("No local node");
//        }
    }

    /**
     * @param nodes
     */
    public void reproduction(Node [] nodes) {
    	// TODO make deployment group
    	// TODO new active in parallel
    }
    
    /**
     * @return the node URL of the Worker.
     */
    public StringWrapper hostname() {
       // return new StringWrapper(this.localNode.getNodeInformation().getURL());
    	return null;
    }

    /**
     * @return the deployer tag of the Worker.
     */
    public DeployerTag deployerTag() {
     //   return this.localNode.getNodeInformation().getDeployerTag();
    	return null;
    }
}
