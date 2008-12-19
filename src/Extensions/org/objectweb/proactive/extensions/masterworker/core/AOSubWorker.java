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
import java.util.concurrent.Executors;

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
    private BooleanWrapper subMasterFailed = new BooleanWrapper(false);
    private boolean electedSubMaster = false;
    private boolean isElecting = false;
    
    private Node currentNode = null;
    
    
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
            final String subMasterName) {
        super(name, provider, initialMemory);

        this.peerid = peerid;
        this.memoryFactory = memoryFactory;
        this.superProvider = superProvider;
        this.subMasterName = subMasterName;
        
        this.workerPeerList = new HashMap<Long, WorkerPeer>();
        this.workerNameList = new HashMap<Long, String>();
        
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

    public BooleanWrapper updateWorkerPeerList(long workerNameCounter, Map<Long, WorkerPeer> workerPeerList, Map<Long, String> workerNameList) {
        // TODO Auto-generated method stub


    	synchronized(workerPeerList){
    		synchronized(workerNameList){
    			if(workerPeerList.size() > this.workerNameList.size() && workerNameList.size() > this.workerNameList.size()) {
	    			this.workerPeerList.clear();
	    			this.workerNameList.clear();
	    			this.workerPeerList.putAll(workerPeerList);
	    			this.workerNameList.putAll(workerNameList);	
    			}
    			else {
    				if (debug) {
	    	        	logger.debug("The peerlist that the peer already have is new than the given one..");
	    			}
	    			return new BooleanWrapper(false);
    			}
    		}
    	}
    	if(workerNameCounter > this.workerNameCounter)
    		this.workerNameCounter = workerNameCounter;
        if (debug) {
        	logger.debug("Worker Manager update peerlist " + peerid);
        	
        	String output = "Peer list size is :" + workerPeerList.size() + " details is: ";
        	for(long keyid : this.workerPeerList.keySet()){
        		output = output + keyid ;
        	}
        	
        	logger.debug(output);
        }
        
        
        return new BooleanWrapper(true);
    }

    /**
     * Send a message to all the workers whose peerid is smaller than this
     * If one of them give back a message, then, go out
     * We create a new thread to ask other workers and the main thread return a true
     */
    public BooleanWrapper canBeSubMaster() {
        // TODO Auto-generated method stub
    	if(!subMasterFailed.booleanValue()){
    		if(isDead().booleanValue()){
    			threadPool.execute(new HeartBeatHandler());
    			return new BooleanWrapper(true);
    		}
    		return new BooleanWrapper(false);
    	}
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
        this.subMasterFailed = new BooleanWrapper(false);
        return new BooleanWrapper(true);
    }
    
    public boolean isDead(Worker worker) {
		// TODO Auto-generated method stub
    	if(isDead().booleanValue()){
    		threadPool.execute(new HeartBeatHandler());
    	}    	
    	return false;
	}
    
    /**
     * if isDead is called, that's to say a peer whose peerId is smaller than him 
     * has detected the missing of the subMaster, so he just clear himself and waiting.
     */
    public BooleanWrapper isDead() {
		// TODO Auto-generated method stub
    	synchronized (subMasterFailed){
	    	if(!subMasterFailed.booleanValue()){
	    		if (debug) {
	            	logger.debug("SubMaster " + subMasterName + " is reported missing!");
	            }
	    		try{
	    			((Worker) provider).heartBeat();
	    			// The subMaster is alive, you made a mistake
	    			return new BooleanWrapper(false);
	    		} catch (Exception e) {
	    			if(!subMasterFailed.booleanValue()){
	    				
	    				subMasterFailed = new BooleanWrapper(true);
	        			try{
	        				pinger.removeWorkerToWatch(subMasterName);
	        			} catch (Exception e1){
	        				if (debug) {
	        	            	logger.debug("Error happens when do the clear for submaster missing!");
	        	            }
	        				e1.printStackTrace();
	        			}
	    			}
	    		}
	    	}
    	}
    	if (debug) {
        	logger.debug("SubMaster " + subMasterName + " has missed!");
        }
    	return new BooleanWrapper(true);
	}

    public boolean isDead(final String workerName) {
    	if(isDead().booleanValue()){
    		threadPool.execute(new HeartBeatHandler());
    	}    	
    	return false;
    }

    private void electNewSubMaster() {
    	Set<Long> peerids = new HashSet<Long>(workerPeerList.keySet());
        Collection<WorkerPeer> workerpeers = workerPeerList.values();
        String workername = null;
        WorkerPeer workerpeer = null;
        
        if (debug) {
        	logger.debug("Start the election of a new submaster");
        }
        isElecting = true;

        // do a loop, if get no response, elect himself as a submaster
        // otherwise the one who give him a response will take charge in the election
        for (Long peerid : peerids) {
        	if(workerPeerList.containsKey(peerid) && workerNameList.containsKey(peerid)) {
        		workerpeer = (WorkerPeer) workerPeerList.get(peerid);
        		workername = workerNameList.get(peerid);
	            if (this.peerid > peerid) {
	                try {
	                    if (debug) {
	                        logger.debug("Ask if " + workername + " can be a submaster");
	                    }
	                    // Send a message to ask those peers whose peerids are smaller than this 
	                    // If any of the workers has heartbeat, then go out and waiting
	                    BooleanWrapper warp = workerpeer.canBeSubMaster();
//	                    if(!warp.booleanValue()) {
//                    	// If it return that the subMaster is alive
//                    	subMasterFailed = true;
//							                    
//                    	return;
//                    }
	                    return;
	                } catch (Exception e) {
	                    if (debug) {
	                        logger.debug("Worker" + workername + " is missing");
	                    }
	                }                	
	            }
//	            else{
//	            	
//	            	try {
//	                    if (debug) {
//	                        logger.debug("Tell the worker " + workername + " that the submaster is reported missing");
//	                    }
//	                    // Send a message to inform the subMaster has dead
//	                    // If any of the workers has heartbeat, then go out and waiting
//	                    BooleanWrapper warp = workerPeerList.get(peerid).isDead();
////	                    PAFuture.waitFor(warp, 100);
////	                    if(!warp.booleanValue()) {
////	                    	// If it return that the subMaster is alive
////	                    	subMasterFailed = true;
////	                    	return;
////	                    }
//	                } catch (Exception e) {
//	                    if (debug) {
//	                        logger.debug("Worker" + workername + " is missing");
//	                    }
//	                } 
//	            	
//	            }
        	}
        }

        // The workerpeer remove himself from the peerlist
        workerPeerList.remove(this.peerid);

        // If no worker is alive, we then determined himself to be the submaster
        // Create a new active object submaster on this node 
        // by calling the special construction funciton of the AOSubMaster

		try {
			AOSubMaster subMaster = (AOSubMaster) PAActiveObject.newActive(AOSubMaster.class.getName(),
			        new Object[] { subMasterName, (WorkerMaster) superProvider, memoryFactory }, currentNode);
		

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
        this.subMasterFailed = new BooleanWrapper(false);
        this.electedSubMaster = true;
        
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

    }

    public void initActivity(Body body) {
        // TODO Auto-generated method stub
        terminated = false;

        stubOnThis = (AOSubWorker) PAActiveObject.getStubOnThis();

        PAActiveObject.setImmediateService("canBeSubMaster");
        PAActiveObject.setImmediateService("addWorkerPeer", new Class<?> [] {long.class , String.class,  WorkerPeer.class});
        // PAActiveObject.setImmediateService("iAmSubmaster", new Class<?> [] {WorkerMaster.class, String.class, HashMap.class});
        PAActiveObject.setImmediateService("isDead");
        PAActiveObject.setImmediateService("isDead", new Class<?> [] {String.class});
        PAActiveObject.setImmediateService("heartBeat");

        //PAActiveObject.setImmediateService("terminate");
        
        // The worker pinger
        try {
        	currentNode = PAActiveObject.getNode();
			pinger = (WorkerWatcher) PAActiveObject.newActive(AOPinger.class.getName(),
					new Object[] { stubOnThis }, currentNode);
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
		
		// get a thread for the peer
		threadPool = Executors.newCachedThreadPool();
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

    /** gets the initial task to solve */
    @SuppressWarnings("unchecked")
    public void getTaskAndSchedule() {
        // We get some tasks
        getTasks();

        if (!terminated) {
            // We schedule the execution
        	stubOnThis.scheduleTask();
        }
    }
    
    /** ScheduleTask : find a new task to run */
    public void scheduleTask() {
        if (!terminated) {
        	
            while ((pendingTasks.size() == 0) && (pendingTasksFutures.size() > 0) && !subMasterFailed.booleanValue()) {
                pendingTasks.addAll(pendingTasksFutures.remove());
            }

            if(subMasterFailed.booleanValue()) {
            	clear();
            }
            
            while(subMasterFailed.booleanValue()){
            	try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if(electedSubMaster) {
            	electedSubMaster = false;
            	stubOnThis.terminate();
            }
            else if (isElecting){
            	isElecting = false;
            	stubOnThis.getTaskAndSchedule();
            }
            
            if (!suspended && (pendingTasks.size() > 0)) {

                TaskIntern<Serializable> newTask = pendingTasks.remove();
                // We handle the current Task
                stubOnThis.handleTask(newTask);

            }

            // if there is nothing to do or if we are suspended we sleep
        }

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