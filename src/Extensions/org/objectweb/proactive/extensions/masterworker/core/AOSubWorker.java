package org.objectweb.proactive.extensions.masterworker.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extensions.masterworker.interfaces.MemoryFactory;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.TaskIntern;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Worker;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerDeadListener;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerPeer;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerWatcher;


public class AOSubWorker extends AOWorker implements WorkerPeer {

    /**
     * 
     */
    private static final long serialVersionUID = 4461125099993797764L;

    static final Logger logger = ProActiveLogger.getLogger(Loggers.MASTERWORKER_SUBWORKERS);
    static final boolean debug = logger.isDebugEnabled();

    private long peerid = 0;
    /** Pinger (checks that workers are alive) */
    private WorkerWatcher pinger;
    
    /** used for create new submaster */
    private WorkerMaster superProvider;
    private MemoryFactory memoryFactory;
    private String subMasterName;
    private long workerNameCounter;
    
    
    /**
     * a thread pool used for submaster election
     */
    private ExecutorService threadPool;

    /**
     * workerpeers deployed so far
     */
    private HashMap<Long, WorkerPeer> workerPeerList;
    private HashMap<Long, String> workerNameList;
    private HashMap<String, Worker> workers;

    public AOSubWorker() {

    }

    public AOSubWorker(final String name, final WorkerMaster provider,
            final Map<String, Serializable> initialMemory, long peerid, 
            final WorkerMaster superProvider, final MemoryFactory memoryFactory, 
            final String subMasterName, final HashMap<Long, WorkerPeer> workerPeerList, HashMap<Long, String> workerNameList) {
        super(name, provider, initialMemory);

        this.peerid = peerid;
        this.memoryFactory = memoryFactory;
        this.superProvider = superProvider;
        this.subMasterName = subMasterName;
        
        this.workerPeerList = new HashMap<Long, WorkerPeer>();
        this.workerNameList = new HashMap<Long, String>();
        this.workerPeerList.putAll(workerPeerList); 
        this.workerNameList.putAll(workerNameList);
        
        if (debug) {
            logger.debug("Creating subworker : " + name);
        }
    }

    /**
     * Getter of the property <tt>peerId</tt>
     * 
     * @return Returns the peerid.
     * @uml.property name="peerid"
     */
    public long getPeerId() {
        // TODO Auto-generated method stub
        return peerid;
    }

    /**
     * Setter of the property <tt>peerId</tt>
     * 
     * @uml.property name="peerid"
     */
    public void setPeerId(long peerid) {
        // TODO Auto-generated method stub
        this.peerid = peerid;
    }

    public BooleanWrapper addWorkerPeer(long peerid, String workername, WorkerPeer workerpeer) {
        // TODO Auto-generated method stub

        workerNameList.put(peerid, workername);
        workerPeerList.put(peerid, workerpeer);
        this.workerNameCounter = peerid;
        return new BooleanWrapper(true);
    }

    /**
     * Send a message to all the workers whose peerid is smaller than this
     * If one of them give back a message, then, go out
     * We create a new thread to ask other workers and the main thread return a true
     */
    public BooleanWrapper canBeSubMaster() {
        // TODO Auto-generated method stub
        threadPool.execute(new HeartBeatHandler());
        return new BooleanWrapper(true);

    }

    public BooleanWrapper iAmSubmaster(WorkerMaster submaster, final String subMasterName, final Map<Long, String> workerNameList,
            final Map<Long, WorkerPeer> workerPeerList) {
        // TODO Auto-generated method stub
    	workerPeerList.clear();
    	workerNameList.clear();
    	
    	if (debug) {
        	logger.debug("" + subMasterName + " has been elected as a new submaster");
        }
    	
    	
    	
        this.provider = submaster;
        this.subMasterName = subMasterName;
        // Add all the workerpeers of the given list to the list of the workerpeer
        this.workerPeerList.putAll(workerPeerList);
        this.workerNameList.putAll(workerNameList);
        pinger.addWorkerToWatch((Worker) submaster, subMasterName);
        if (debug) {
        	logger.debug("Workerpeer " + name + " add a pinger to wathch its submaster " + subMasterName);
        }
        return new BooleanWrapper(true);
    }
    
    public boolean isDead(Worker worker) {
		// TODO Auto-generated method stub
    	threadPool.execute(new HeartBeatHandler());
    	return true;
	}

    public boolean isDead(final String workerName) {
    	threadPool.execute(new HeartBeatHandler());
    	return true;
    }

    private void electNewSubMaster() {
    	clear();
        Set<Long> peerids = new HashSet<Long>(workerPeerList.keySet());
        Collection<WorkerPeer> workerpeers = workerPeerList.values();
        String workername = null;
        WorkerPeer workerpeer = null;
        boolean response = false;
        
        // remove the old submaster from pinger
        pinger.removeWorkerToWatch(subMasterName);
        
        if (debug) {
        	logger.debug("Start the election of a new submaster");
        }

        // do a loop, if get no response, elect himself as a submaster
        // otherwise the one who give him a response will take charge in the election
        for (Long peerid : peerids) {
            // Send a message to ask those peers whose peerids are smaller than this 
            if (this.peerid > peerid) {
                workerpeer = (WorkerPeer) workerPeerList.get(peerid);

                if (debug) {
                    workername = workerNameList.get(peerid);
                }
                try {
                    if (debug) {
                        logger.debug("Ask if " + workername + " can be a submaster");
                    }
                    workerpeer.canBeSubMaster();
                    response = true;
                } catch (Exception e) {
                    if (debug) {
                        logger.debug("Worker" + workername + " is missing");
                    }
                }

                // If any of the workers has heartbeat, then go out and waiting
                if(response)
                	return;
            }
        }

        // The workerpeer remove himself from the peerlist
        workerPeerList.remove(this.peerid);

        // If no worker is alive, we then determined himself to be the submaster
        // Create a new active object submaster on this node 
        // by calling the special construction funciton of the AOSubMaster

		try {
			AOSubMaster subMaster = (AOSubMaster) PAActiveObject.newActive(AOSubMaster.class.getName(),
			        new Object[] { subMasterName, (WorkerMaster) superProvider, memoryFactory },
			        PAActiveObject.getNode());
		

        // Inform all the workerpeers in the list that he is the new submaster
        // Here we pass the argument like submaster stub or just submaster
        for (WorkerPeer peer : workerpeers) {
            peer.iAmSubmaster(subMaster, subMasterName, workerNameList, workerPeerList);
        }
        
        // Get workers
        workers = new HashMap<String, Worker>();
        for (Long peerid : workerPeerList.keySet()) {
        	if(peerid > workerNameCounter)
        		workerNameCounter = peerid;
        	workers.put(workerNameList.get(peerid), (AOSubWorker) workerPeerList.get(peerid));
        }
        // Call the initSubMaster to init the subMaster
        BooleanWrapper wrap = subMaster.InitSubMaster(workerNameCounter, workerPeerList, workers);
        PAFuture.waitFor(wrap);
        
        
        // The worker then terminate himself
        this.terminate();
        
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    public void initActivity(Body body) {
        // TODO Auto-generated method stub
        terminated = false;

        stubOnThis = (AOSubWorker) PAActiveObject.getStubOnThis();

        PAActiveObject.setImmediateService("addWorkerPeer");
        PAActiveObject.setImmediateService("canBeSubMaster");
        PAActiveObject.setImmediateService("iAmSubmaster");
        PAActiveObject.setImmediateService("updatePeerList");
        PAActiveObject.setImmediateService("isDead");
        PAActiveObject.setImmediateService("heartBeat");     

        //PAActiveObject.setImmediateService("terminate");
        
        // The worker pinger
        try {
			pinger = (WorkerWatcher) PAActiveObject.newActive(AOPinger.class.getName(),
					new Object[] { stubOnThis });
			pinger.addWorkerToWatch((Worker) provider, subMasterName);
			if (debug) {
	        	logger.debug("Workerpeer " + name + " add a pinger to wathch its submaster " + subMasterName);
	        }
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Initial Task
        stubOnThis.getTaskAndSchedule();
    }

    public BooleanWrapper terminate() {
        if (debug) {
            logger.debug("Terminating " + name + "...");
        }
        PAFuture.waitFor(pinger.terminate());
        
        ((WorkerMemoryImpl) memory).clear();
        initialMemory.clear();

        // We terminate the pinger
        
        if (debug) {
            logger.debug(name + " terminated...");
        }

        provider = null;
        stubOnThis = null;
        PAActiveObject.terminateActiveObject(false);
        return new BooleanWrapper(true);
    }

    /**
     * Internal class which deal with test if the workers whose peerid is smaller than this are live or not
     *
     * @author The ProActive Team
     */
    private class HeartBeatHandler implements Runnable {

        /**
         * Creates a worker on a given node
         *
         * @param node node on which the worker will be created
         */
        public HeartBeatHandler() {
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            electNewSubMaster();
        }
    }	
}