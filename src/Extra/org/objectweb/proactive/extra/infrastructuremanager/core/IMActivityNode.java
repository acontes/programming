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
package org.objectweb.proactive.extra.infrastructuremanager.core;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;


public class IMActivityNode implements Runnable {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.IM_ACTIVITY_NODES);
    private boolean actif;
    private long wait;
    private IMCore imCore;

    public IMActivityNode(IMCore imCore) {
        this.imCore = imCore;
        this.actif = true;
        this.wait = 30000;
    }

    public void stop() {
        this.actif = false;
    }

    public void run() {
        while (actif) {
            if (logger.isInfoEnabled()) {
                logger.info("search down nodes");
            }
            ArrayList<IMNode> imNodes = this.imCore.getListAllNodes();
            for (IMNode imNode : imNodes) {
//                String nodeURL;
                @SuppressWarnings("unused")
				Node node;
                try {
                    imNode.getNode().getNumberOfActiveObjects();
                    //node = NodeFactory.getNode(nodeURL);
                } catch (NodeException e) {
                    this.imCore.nodeIsDown(imNode);
                }
            }

            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
