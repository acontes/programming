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
package org.objectweb.proactive.extensions.masterworker.core;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.body.exceptions.BodyTerminatedRequestException;
import org.objectweb.proactive.core.body.exceptions.SendRequestCommunicationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Worker;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerManager;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.MemoryFactory;
import org.objectweb.proactive.extensions.masterworker.core.AOWorker;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * The Worker Manager Active Object is responsible for the deployment of Workers :<br>
 * <ul>
 * <li> Through a ProActive deployment descriptor</li>
 * <li> Using an existing VirtualNode object</li>
 * <li> Using a collection of Nodes</li>
 * </ul>
 *
 * @author The ProActive Team
 */
public class AOWorkerManager implements WorkerManager, InitActive, Serializable {

    /**
     * log4j logger for the worker manager
     */
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.MASTERWORKER_WORKERMANAGER);
    private static final boolean debug = logger.isDebugEnabled();

    /**
     * stub on this active object
     */
    private org.objectweb.proactive.extensions.masterworker.core.AOWorkerManager stubOnThis;

    /**
     * how many workers have been created
     */
    private long workerNameCounter;

    /**
     * holds the virtual nodes, only used to kill the nodes when the worker manager is terminated
     */
    private Set<GCMVirtualNode> vnlist;

    /**
     * holds the deployed proactive descriptors, only used to kill the nodes when the worker manager is terminated
     */
    private Vector<GCMApplication> padlist;

    /**
     * a thread pool used for worker creation
     */
    private ExecutorService threadPool;

    /**
     * true when the worker manager is terminated
     */
    private boolean isTerminated;

    /**
     * the entity which will provide tasks to the workers
     */
    private WorkerMaster provider;

    /**
     * Initial memory of the workers
     */
    private MemoryFactory memoryFactory;

    /**
     * workers deployed so far
     */
    private Map<String, Worker> workers;

    /**
    * descriptor used to deploy the master (if any)
    */
    private URL masterDescriptorURL;

    /**
    * GCMapplication used to deploy the master (if any)
    */
    private GCMApplication applicationUsed;

    /**
     * VN Name of the master (if any)
     */
    private String masterVNNAme;

    /**
     * Hierarchical master worker
     */
    private int groupSize = 100;
    private HashMap<Long, String> subMasters;
    private HashMap<Long, Integer> subGroupSizes;
    private HashMap<Long, String> fullSubMasters;

    /**
     * ProActive no arg constructor
     */
    public AOWorkerManager() {
    }

    /**
     * Creates a task manager with the given task provider
     *
     * @param provider      the entity that will give tasks to the workers created
     * @param memoryFactory factory which will create memory for each new workers
     * @param masterDescriptorURL descriptor used to deploy the master (if any)
     * @param applicationUsed GCMapplication used to deploy the master (if any)
     * @param masterVNNAme VN Name of the master (if any)
     */
    public AOWorkerManager(final WorkerMaster provider, final MemoryFactory memoryFactory,
            final URL masterDescriptorURL, final GCMApplication applicationUsed, final String masterVNNAme) {
        this.provider = provider;
        this.memoryFactory = memoryFactory;
        this.masterDescriptorURL = masterDescriptorURL;
        this.applicationUsed = applicationUsed;
        this.masterVNNAme = masterVNNAme;
        if (null != PAProperties.PA_MASTERWORKER_SUBGROUPSIZE.getValue()) {
            groupSize = Integer.parseInt(PAProperties.PA_MASTERWORKER_SUBGROUPSIZE.getValue());
        }
        subMasters = new HashMap<Long, String>();
        fullSubMasters = new HashMap<Long, String>();
        subGroupSizes = new HashMap<Long, Integer>();
    }

    /**
     * {@inheritDoc}
     */
    public void addResources(final Collection<Node> nodes) {
        if (!isTerminated) {
            for (Node node : nodes) {
                createSubMaster(node);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addResources(final URL descriptorURL) throws ProActiveException {
        if (!isTerminated) {

            if (!descriptorURL.equals(masterDescriptorURL)) {
                // If the descriptor given is not the one already used to deploy the master, we start a deployment
                GCMApplication pad = PAGCMDeployment.loadApplicationDescriptor(descriptorURL);
                padlist.add(pad);
                for (Entry<String, GCMVirtualNode> ent : pad.getVirtualNodes().entrySet()) {
                    addResourcesInternal(ent.getValue());
                }
                pad.startDeployment();
            } else {
                // Otherwise, we reuse the previously started deployment
                for (Entry<String, GCMVirtualNode> ent : applicationUsed.getVirtualNodes().entrySet()) {
                    // But we won't use the VN already used for the master
                    if (!ent.getKey().equals(masterVNNAme)) {
                        addResourcesInternal(ent.getValue());
                    }
                }
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    public void addResources(final URL descriptorURL, final String virtualNodeName) throws ProActiveException {
        if (!isTerminated) {
            if (!descriptorURL.equals(masterDescriptorURL)) {
                // If the descriptor given is not the one already used to deploy the master, we start a deployment

                GCMApplication pad = PAGCMDeployment.loadApplicationDescriptor(descriptorURL);
                padlist.add(pad);
                addResourcesInternal(pad.getVirtualNode(virtualNodeName));
                pad.startDeployment();
            } else {
                // Otherwise, we reuse the previously started deployment
                addResourcesInternal(applicationUsed.getVirtualNode(virtualNodeName));
            }
        }
    }

    private void addResourcesInternal(final GCMVirtualNode virtualnode) throws ProActiveException {
        if (!isTerminated) {
            String vnname = virtualnode.getName();
            if (debug) {
                logger.debug("Adding Virtual Node " + vnname + " to worker manager");
            }

            vnlist.add(virtualnode);
            virtualnode.subscribeNodeAttachment(stubOnThis, "nodeCreated", true);

            //Don't use the following, subscribeNodeAttachment with history should send notifications for already acquired nodes
            //List<Node> nodes = virtualnode.getCurrentNodes();
            //addResources(nodes);

            if (debug) {
                logger.debug("Virtual Node " + vnname + " added to worker manager");
            }
        }
    }

    /**
     * Creates a submaster object inside the given node
     *
     * @param node the node on which a submaster will be created
     */
    private void createSubMaster(final Node node) {
        if (!isTerminated) {
            try {
                long topologyId = node.getVMInformation().getTopologyId();

                AOSubMaster subMaster = null;

                if (topologyId > 0) {
                    if (subMasters.containsKey(topologyId)) {
                    	// Make sure the subMaster is alive
                    	// Otherwise wait it to recover
                        try{
                        	(workers.get(subMasters.get(topologyId))).heartBeat();
                        } catch (Exception e) {
                        	if(debug){
                        		e.printStackTrace();
                        	}
                        	if (subGroupSizes.get(topologyId) > 0)
                        		stubOnThis.createSubMaster(node);
                        	else {
                        		subGroupSizes.remove(topologyId);
                        		
                        		workers.remove(subMasters.remove(topologyId));
                        		stubOnThis.createSubMaster(node);
                        	}
                        	
                        	return;
                        }
                        threadPool.execute(new WorkerCreationHandler(node));
                    }

                    // If no subMaster for the specified topologyID, create a new subMaster
                    else {
                        String workername = "SubMaster_" + node.getVMInformation().getHostName() + "_" +
                            workerNameCounter++;
                        String nodename = node.getNodeInformation().getName();
                        subMaster = (AOSubMaster) PAActiveObject.newActive(AOSubMaster.class.getName(),
                                new Object[] { workername, (WorkerMaster) provider, this.memoryFactory },
                                node);

                        subMasters.put(topologyId, workername);
                        // Add the submaster to worker group
                        workers.put(workername, subMaster);
                        subGroupSizes.put(topologyId, 0);

                        if (debug) {
                            logger.debug("Creating SubMaster on " + nodename);
                        }
                    }

                } else {
                    threadPool.execute(new WorkerCreationHandler(node));
                }

            } catch (ActiveObjectCreationException e) {
                e.printStackTrace(); // bad node
            } catch (NodeException e) {
                e.printStackTrace(); // bad node
            }
        }
        // If it is terminated, return true, and do nothing
    }

    /**
     * Creates a worker object inside the given node
     *
     * @param node the node on which a worker will be created
     */
    private void createWorker(final Node node) {
        if (!isTerminated) {
            try {
                long topologyId = node.getVMInformation().getTopologyId();
                Integer subGroupSize = 0;
                AOSubMaster subMaster = null;

                if (debug) {
                    logger.debug("TopologyID is: " + node.getVMInformation().getTopologyId());
                }
                if (topologyId > 0) {
                    if (subMasters.containsKey(topologyId)) {
                        // if the size of the group is bigger than the set size
                        // Remove it from the subMaster group and add it to the full group

                        subMaster = (AOSubMaster) workers.get(subMasters.get(topologyId));

                        

                        // get the submaster and give the node to the subMaster to create worker
                        Collection<Node> nodes = new Vector<Node>();
                        nodes.add(node);

                        try {
                            subMaster.addResources(nodes);
                            if (debug) {
                                logger.debug("Call add resource to subMaster success.");
                            }
                            subMaster.heartBeat();
                        } catch (Exception exp) {
                            if (debug) {
                                logger.debug("Error occurs when call addResource to subMaster.");
                                exp.printStackTrace();
                            }
                            // readd the resources
                            stubOnThis.addResources(nodes);
                            return;
                        }
                        
                        // If another one worker is added to the group, wait
                        // The group size if a critical variable

                        subGroupSize = subGroupSizes.get(topologyId);
                        synchronized (subGroupSize) {
                            subGroupSize = subGroupSizes.remove(topologyId);

                            if ((++subGroupSize) < this.groupSize)
                                subGroupSizes.put(topologyId, subGroupSize);
                            else {
                                subMasters.remove(topologyId);
                                fullSubMasters.put(topologyId, subMasters.get(topologyId));
                            }

                            if (debug) {
                                logger.debug("subGroupSize is: " + subGroupSize);
                            }
                        }
                            
                    }
                } else {
                    String workername = "Worker_" + node.getVMInformation().getHostName() + "_" +
                        workerNameCounter++;
                    String nodename = node.getNodeInformation().getName();

                    if (debug) {
                        logger.debug("Creating worker on " + nodename);
                    }

                    // Creates the worker which will automatically connect to the master
                    workers.put(workername, (Worker) PAActiveObject.newActive(AOWorker.class.getName(),
                            new Object[] { workername, (WorkerMaster) provider,
                                    memoryFactory.newMemoryInstance() }, node));
                    if (debug) {
                        logger.debug("Worker " + workername + " created on " + nodename);
                    }
                }
            } catch (ActiveObjectCreationException e) {
                e.printStackTrace(); // bad node
            } catch (NodeException e) {
                e.printStackTrace(); // bad node
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void initActivity(final Body body) {
        stubOnThis = (org.objectweb.proactive.extensions.masterworker.core.AOWorkerManager) PAActiveObject
                .getStubOnThis();
        workerNameCounter = 0;
        workers = new HashMap<String, Worker>();
        vnlist = new HashSet<GCMVirtualNode>();
        padlist = new Vector<GCMApplication>();

        isTerminated = false;
        if (debug) {
            logger.debug("Resource Manager Initialized");
        }

        threadPool = Executors.newCachedThreadPool();
    }

    /**
     * Callback function used when Nodes are created
     * @param node a node which just got registered
     * @param virtualNode name of the vn associated
     */
    public void nodeCreated(Node node, String virtualNode) {
        if (debug) {
            logger.debug("nodeCreated " + node);
        }

        // get the node
        try {
            createSubMaster(node);
        } catch (java.util.concurrent.RejectedExecutionException e) {
            if (debug) {
                logger.debug("Creation of the worker rejected, manager is shutting down...");
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public BooleanWrapper terminate(final boolean freeResources) {
        isTerminated = true;

        if (debug) {
            logger.debug("Terminating WorkerManager...");
        }

        try {
            // we shutdown the thread pool, no new thread will be accepted
            threadPool.shutdown();

            for (GCMVirtualNode vn : vnlist) {
                // we wait for every node creation, in case some nodes were not already deployed
                try {
                    vn.waitReady(10 * 1000);
                } catch (ProActiveTimeoutException e) {
                    e.printStackTrace();
                }
            }

            // we wait that all threads creating active objects finish
            threadPool.awaitTermination(120, TimeUnit.SECONDS);

            // we send the terminate message to every thread
            for (String workerName : workers.keySet()) {
                try {
                    if (debug) {
                        logger.debug("Ask for terminating worker " + workerName);
                    }

                    BooleanWrapper term = (workers.get(workerName)).terminate();
                    // as it is a termination algorithm we wait a bit, but not forever
                    PAFuture.waitFor(term);

                    if (debug) {
                        logger.debug(workerName + " freed.");
                    }
                } catch (SendRequestCommunicationException exp) {
                    if (debug) {
                        logger.debug(workerName + " is already freed.");
                    }
                } catch (BodyTerminatedRequestException exp1) {
                    if (debug) {
                        logger.debug("Master has already been terminaterd.");
                    }
                }
            }

            // if the user asked it, we also release the resources, by killing all JVMs
            if (freeResources) {

                // We terminate the deployed proactive descriptors
                for (GCMApplication pad : padlist) {
                    if (debug) {
                        logger.debug("Terminating Application Descriptor " +
                            pad.getDescriptorURL().toExternalForm());
                    }
                    pad.kill();
                }
            }

            workers.clear();
            workers = null;

            provider = null;
            stubOnThis = null;

            // finally we terminate this active object
            PAActiveObject.terminateActiveObject(false);
            // success
            if (debug) {
                logger.debug("WorkerManager terminated...");
            }

            return new BooleanWrapper(true);
        } catch (Exception e) {
            logger.error("Couldn't Terminate the Resource manager");
            e.printStackTrace();
            return new BooleanWrapper(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDead(Worker worker) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDead(String workerName) {
        if (workers.containsKey(workerName)) {
            if (!((workers.get(workerName)) instanceof AOSubMaster)) {
                workers.remove(workerName);
            }
        }

        return true;
    }

    public boolean addWorker(String workerName, Worker worker) {
        if (!workers.containsKey(workerName)) {
            workers.put(workerName, worker);
        } else {
            workers.remove(workerName);
            workers.put(workerName, worker);
            if (debug) {
                logger.debug("Update submaster " + workerName);
            }
        }
        return true;
    }

    /**
     * Internal class which creates workers on top of nodes
     *
     * @author The ProActive Team
     */
    private class WorkerCreationHandler implements Runnable {

        /**
         * node on which workers will be created
         */
        private Node node = null;

        /**
         * Creates a worker on a given node
         *
         * @param node node on which the worker will be created
         */
        public WorkerCreationHandler(final Node node) {
            this.node = node;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            createWorker(node);
        }
    }
}