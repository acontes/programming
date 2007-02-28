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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.benchmarks.timit.util.TimItManager;
import org.objectweb.proactive.branchnbound.user.BnBManager;
import org.objectweb.proactive.branchnbound.user.BnBTask;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.descriptor.data.VirtualNodeImpl;
import org.objectweb.proactive.core.event.NodeCreationEvent;
import org.objectweb.proactive.core.event.NodeCreationEventListener;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class BnBManagerImpl implements BnBManager, Serializable {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.BNB_MANAGER);
    private ExecutorService deploymentThreadPool = null;
    private ArrayList<VirtualNode> vnList = new ArrayList<VirtualNode>();
    private TimItManager timitManager = TimItManager.getInstance();
    
    public BnBManagerImpl() {
        // empty for activating
    }

    public void deployAndAddResources(VirtualNode virtualNode) {
        assert virtualNode != null : virtualNode;
        this.deployAndAddResources(new VirtualNode[] { virtualNode });
    }

    public void deployAndAddResources(VirtualNode[] virtualNodes) {
        assert (virtualNodes != null) && (virtualNodes.length > 0): virtualNodes;
        if (this.deploymentThreadPool == null) {
            this.deploymentThreadPool = Executors.newCachedThreadPool();
            if (logger.isDebugEnabled()) {
                logger.debug("Treads pool for deployments instantiated");
            }
        }
        for (VirtualNode vn : virtualNodes) {
        	assert vn.isActivated() == false : vn;
            this.deploymentThreadPool.execute(new VnThread(vn));
            this.vnList.add(vn);
        }
    }

    public <Value extends Comparable<Value>> Value start(BnBTask<Value> task) {
        // TODO Auto-generated method stub
        return null;
    }

    public void terminate() {
        if (this.deploymentThreadPool != null) {
            this.deploymentThreadPool.shutdown();
            try {
                this.deploymentThreadPool.awaitTermination(3 * 60,
                    TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("Cannot shutdown the deployment thread pool:" +
                    " still some deployments running", e);
            }
        }
        for (VirtualNode vn : this.vnList) {
            vn.killAll(false);
        }
        // TODO termib=nate on the queue
        ProActive.terminateActiveObject(ProActive.getStubOnThis(), true);
    }

    private class VnThread implements Runnable, NodeCreationEventListener {
        private VirtualNode vn = null;

        public VnThread(VirtualNode vn) {
            this.vn = vn;
        }

        // FIXME CANNOT WORK....
        public void run() {
            assert this.vn != null : this.vn;

            ((VirtualNodeImpl) this.vn).addNodeCreationEventListener(this);
            this.vn.activate();
            try {
                ((VirtualNodeImpl) this.vn).waitForAllNodesCreation();
            } catch (NodeException e) {
                logger.warn("Something wronf while waiting nodes creation", e);
            }
        }

        public void nodeCreated(NodeCreationEvent event) {
            Node node = event.getNode();
            try {
                // TODO set constructor params: Queue
                ProActive.newActive(BnBWorker.class.getName(), null, node);
            } catch (ActiveObjectCreationException e) {
                logger.warn("Cannot activate a BnBWorker", e);
            } catch (NodeException e) {
                logger.warn("Cannot activate a BnBWorker: Node fault", e);
            }
        }
    }
}
