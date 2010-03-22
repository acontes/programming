package org.objectweb.proactive.extensions.masterworker.core;

import java.io.Serializable;
import java.util.ArrayList;
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


public class AOSubWorker extends AOWorker implements WorkerPeer, WorkerDeadListener {

    /**
     * 
     */

    static final Logger logger = ProActiveLogger.getLogger(Loggers.MASTERWORKER_SUBWORKERS);
    static final boolean debug = logger.isDebugEnabled();

    /** used for create new submaster */
    private WorkerMaster superProvider;
    private MemoryFactory memoryFactory;
    private String subMasterName;
    private long workerNameCounter;
    private Node currentNode = null;

    /** election related */
    private HashMap<Long, Node> deployedNodes;
    private long peerid = 0;
    private WorkerWatcher pinger;
    private ExecutorService threadPool;
    private HashMap<Long, WorkerPeer> workerPeerList;
    private HashMap<Long, String> workerNameList;

    /** whether the worker is in election mode */
    private Boolean isInElection = false;
    /** iAmSubMaster musted by called after isDead */
    private Boolean isDeadCalled = false;

    public AOSubWorker() {

    }

    public AOSubWorker(final String name, final WorkerMaster provider,
            final Map<String, Serializable> initialMemory, long peerid, final WorkerMaster superProvider,
            final MemoryFactory memoryFactory, final String subMasterName, final Map<Long, Node> deployedNodes) {
        super(name, provider, initialMemory);

        this.peerid = peerid;
        this.memoryFactory = memoryFactory;
        this.superProvider = superProvider;
        this.subMasterName = subMasterName;

        this.workerPeerList = new HashMap<Long, WorkerPeer>();
        this.workerNameList = new HashMap<Long, String>();
        this.deployedNodes = new HashMap<Long, Node>();
        this.deployedNodes.putAll(deployedNodes);

        if (debug) {
            logger.debug("Creating subworker : " + name);
        }
    }

    /**
     * The additional operation of initActivity of AOSubWorker
     * 1) two additional immediate service isDead and areYouAlive
     * 2) add a pinger to monitor the SubMaster
     */
    public void initActivity(Body body) {

        terminated = false;
        stubOnThis = (AOWorker) PAActiveObject.getStubOnThis();
        PAActiveObject.setImmediateService("heartBeat");
        //PAActiveObject.setImmediateService("terminate");

        //for AOSubWorker begin
        PAActiveObject.setImmediateService("isDead", new Class<?>[] { String.class });
        PAActiveObject.setImmediateService("areYouAlive", new Class<?>[] { Long.class, String.class });
        //The worker pinger
        try {
            currentNode = PAActiveObject.getNode();
            pinger = (WorkerWatcher) PAActiveObject.newActive(AOPinger.class.getName(),
                    new Object[] { stubOnThis }, currentNode);
            pinger.addWorkerToWatch((Worker) provider, subMasterName);
            if (debug) {
                logger.debug("Workerpeer " + name + " add a pinger to wathch its submaster " + subMasterName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        threadPool = Executors.newCachedThreadPool();
        //for AOSubWorker end

        // Initial Task
        stubOnThis.getTaskAndSchedule();

    }

    /**
     * The additional operation of terminate of AOSubWorker
     * 1) stop the pinger before stop itself
     */
    public BooleanWrapper terminate() {
        PAFuture.waitFor(pinger.terminate());
        return super.terminate();
    }

    /**
     * The additional operation of scheduleTask of AOSubWorker
     * 1) when find itself enter the election mode, stop the
     * worker's behaviour i.e. clear and stop handling tasks
     */
    public void scheduleTask() {
        if (!terminated) {

            //for AOSubWorker begin
            synchronized (isInElection) {
                //stop the worker's behaviour
                if (isInElection) {
                    pendingTasks.clear();
                    pendingTasksFutures.clear();
                    return;
                }
            }
            //for AOSubWorker end

            while ((pendingTasks.size() == 0) && (pendingTasksFutures.size() > 0)) {
                pendingTasks.addAll(pendingTasksFutures.remove());
            }

            if (!suspended && (pendingTasks.size() > 0)) {

                TaskIntern<Serializable> newTask = pendingTasks.remove();
                // We handle the current Task
                stubOnThis.handleTask(newTask);

            }
            // if there is nothing to do or if we are suspended we sleep
        }

    }

    private void sleepForAWhile(long t) {
        try {
            Thread.sleep(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Internal class responsible for election algorithm thread
     * for one time election, each worker should run this thread
     * only one time
     */
    private class ElectionHandler implements Runnable {

        public ElectionHandler() {
        }

        public void run() {
            electNewSubMaster();
        }
    }

    /**
     * belowing part is election logic
     */

    /**
     * inherited from WorkerDeadListener, never used
     */
    public boolean isDead(Worker worker) {
        return false;
    }

    /**
     * called by the pinger when the pinger find SubMaster dead
     * it is the only way to enter the election mode
     */
    public boolean isDead(final String workerName) {

        if (debug) {
            logger.debug(this.name + " find submaster is dead");
        }

        isDeadCalled = true;
        //set this flag to stop the worker's behaviour
        synchronized (isInElection) {
            isInElection = true;
        }

        threadPool.execute(new ElectionHandler());
        return false;
    }

    /**
     * called by the SubMaster to broadcast the peer list knowledge
     * this is called during the deployment time
     */
    public BooleanWrapper updateWorkerPeerList(long workerNameCounter, Map<Long, WorkerPeer> workerPeerList,
            Map<Long, String> workerNameList) {

        if (debug) {
            logger.debug("updateWorkerPeerList called");
        }

        synchronized (workerPeerList) {
            synchronized (workerNameList) {
                if (workerPeerList.size() > this.workerNameList.size() &&
                    workerNameList.size() > this.workerNameList.size()) {
                    this.workerPeerList.clear();
                    this.workerNameList.clear();
                    this.workerPeerList.putAll(workerPeerList);
                    this.workerNameList.putAll(workerNameList);
                } else {
                    if (debug) {
                        logger.debug("old message");
                    }
                    return new BooleanWrapper(false);
                }
            }
        }
        if (workerNameCounter > this.workerNameCounter)
            this.workerNameCounter = workerNameCounter;
        if (debug) {
            String output = "updateWorkerPeerList: Peer list size is :" + workerPeerList.size() +
                " details is: ";
            for (long keyid : this.workerPeerList.keySet()) {
                output = output + "-" + keyid;
            }

            logger.debug(output);
        }

        return new BooleanWrapper(true);
    }

    /**
     * called by the worker peer with bigger id
     * if returned the bigger peer give up to be the new SubMaster
     * and wait for the new SubMaster's arrival
     * must be synchronous
     */
    public boolean areYouAlive(final Long peerId, final String peerName) {

        if (debug) {
            logger.debug("areYouAlive called caller: " + peerName + " caller's id: " + peerId + " self: " +
                this.name);
        }
        return true;
    }

    /**
     * the new SubMaster's arrival
     * asyn but the result should be waited by the new SubMaster
     * the only way out of election mode
     */
    public BooleanWrapper iAmSubMaster(WorkerMaster submaster, final String subMasterName,
            final Map<Long, String> workerNameList, final Map<Long, WorkerPeer> workerPeerList) {

        //must be called after isDead, wait for the pinger
        while (isDeadCalled == false) {
            sleepForAWhile(200);
        }

        //reset this flag for next election
        isDeadCalled = false;

        if (debug) {
            logger.debug("iAmSubMaster called caller: " + subMasterName + " self: " + name);
        }

        //update the peer list knowledge
        this.workerPeerList.clear();
        this.workerNameList.clear();
        this.workerPeerList.putAll(workerPeerList);
        this.workerNameList.putAll(workerNameList);
        this.provider = submaster;
        this.subMasterName = subMasterName;
        this.pinger.removeWorkerToWatch(subMasterName);
        this.pinger.addWorkerToWatch((Worker) submaster, subMasterName);

        //get out of election mode
        //and reschedule task
        //the only way out
        synchronized (isInElection) {
            isInElection = false;
        }
        stubOnThis.getTaskAndSchedule();

        //ack to the new SubMaster
        return new BooleanWrapper(true);

    }

    /**
     * election logic
     * in one election each worker should run this in a new thread
     * only once
     */
    private void electNewSubMaster() {

        Set<Long> peerids = new HashSet<Long>(workerPeerList.keySet());
        String workername = null;
        WorkerPeer workerpeer = null;

        if (debug) {
            logger.debug("election started on " + name);

            String output = "election1 Peer list size is : " + peerids.size() + " details is: ";
            for (long keyid : peerids) {
                output = output + "-" + keyid;
            }

            logger.debug(output);
        }

        //check the peers whose id is smaller then mine
        //if any of them is alive, I will give up and wait
        //for the arrival of the new SubMaster
        for (Long peerId : peerids) {

            if (this.peerid > peerId && workerNameList.containsKey(peerId)) {
                workerpeer = (WorkerPeer) workerPeerList.get(peerId);
                workername = workerNameList.get(peerId);
                try {
                    if (debug) {
                        logger.debug(this.name + " ask if " + workername + " is alive");
                    }

                    workerpeer.areYouAlive(this.peerid, this.name);

                    if (debug) {
                        logger.debug(this.name + "receive a reply from Worker " + workername + ", waiting");
                    }
                    //give up and wait
                    return;
                } catch (Exception e) {
                    if (debug) {
                        logger.debug(this.name + "in election Worker " + workername + " is missing");
                    }
                    e.printStackTrace();
                }
            }

        }

        //new SubMaster logic

        //step0 preparation remove himself from the peerlist
        workerPeerList.remove(this.peerid);
        workerNameList.remove(this.peerid);
        Collection<WorkerPeer> workerpeers = new ArrayList<WorkerPeer>(workerPeerList.values());

        if (debug) {

            logger.debug("new submaster entering");

            String output = "election2 Peer list size is :" + workerPeerList.size() + " Peerid is :" +
                this.peerid + " details is: ";
            for (long keyid : workerPeerList.keySet()) {
                output = output + "-" + keyid;
            }
            logger.debug(output);
        }

        try {
            //step1: AO creation
            AOSubMaster subMaster = (AOSubMaster) PAActiveObject.newActive(AOSubMaster.class.getName(),
                    new Object[] { subMasterName, (WorkerMaster) superProvider, memoryFactory }, currentNode);
            if (debug) {
                logger.debug("new submaster created1 AO creation done");
            }

            //step2: init the new SubMaster
            HashMap<String, Worker> workers = new HashMap<String, Worker>();
            for (Long peerid : workerPeerList.keySet()) {
                if (peerid > workerNameCounter)
                    workerNameCounter = peerid;
                workers.put(workerNameList.get(peerid), (AOSubWorker) workerPeerList.get(peerid));
            }
            BooleanWrapper wrap = subMaster.InitSubMaster(workerNameCounter, workerPeerList, workers);
            PAFuture.waitFor(wrap);

            if (debug) {
                logger.debug("new submaster created2 initialization done");
            }

            //step3 broadcasting
            ArrayList<BooleanWrapper> confirm = new ArrayList<BooleanWrapper>();
            for (WorkerPeer peer : workerpeers) {
                try {
                    BooleanWrapper rlt = peer.iAmSubMaster(subMaster, subMasterName, workerNameList,
                            workerPeerList);
                    confirm.add(rlt);
                } catch (Exception e) {
                    if (debug) {
                        logger.debug("broadcasting exception");
                    }
                    e.printStackTrace();
                }
            }

            if (debug) {
                logger.debug("new submaster created3 start waiting for result");
            }

            //must wait for the result
            for (int i = 0; i < confirm.size(); i++) {
                try {
                    PAFuture.waitFor(confirm.get(i));
                } catch (Exception e) {
                    if (debug) {
                        logger.debug("wait confirm exception");
                    }
                    e.printStackTrace();
                }
            }

            if (debug) {
                logger.debug("new submaster created4 broadcasting done");
            }

            //step4 terminate myself as a worker
            stubOnThis.terminate();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (debug) {
                logger.debug(this.name + " new submaster error");
            }
            e.printStackTrace();
        }

    }

}
