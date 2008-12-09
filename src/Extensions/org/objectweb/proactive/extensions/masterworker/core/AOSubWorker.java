package org.objectweb.proactive.extensions.masterworker.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.TaskIntern;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Worker;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerPeer;


public class AOSubWorker extends AOWorker implements WorkerPeer {

    /**
     * 
     */
    private static final long serialVersionUID = 4461125099993797764L;

    static final Logger logger = ProActiveLogger.getLogger(Loggers.MASTERWORKER_SUBWORKERS);
    static final boolean debug = false;

    private long peerid = 0;

    /**
     * a thread pool used for submaster election
     */
    private ExecutorService threadPool;

    /**
     * workerpeers deployed so far
     */
    private HashMap<Long, WorkerPeer> workerpeerlist;
    private HashMap<Long, String> workernamelist;

    public AOSubWorker() {

    }

    public AOSubWorker(final String name, final WorkerMaster provider,
            final Map<String, Serializable> initialMemory, long peerid) {
        super(name, provider, initialMemory);

        this.peerid = peerid;

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

        workernamelist.put(peerid, workername);
        workerpeerlist.put(peerid, workerpeer);
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

    public BooleanWrapper iAmSubmaster(WorkerMaster submaster) {
        // TODO Auto-generated method stub

        return new BooleanWrapper(true);
    }

    public BooleanWrapper updatePeerList(WorkerMaster submaster, final Map<Long, String> workernamelist,
            final Map<Long, WorkerPeer> workerpeerlist) {
        // TODO Auto-generated method stub
        // Remove all workerpeers from the peerlist 
        this.clear();
        this.provider = submaster;
        // Add all the workerpeers of the given list to the list of the workerpeer
        this.workerpeerlist.putAll(workerpeerlist);
        this.workernamelist.putAll(workernamelist);

        return new BooleanWrapper(true);
    }

    private void electNewSubMaster() {
        Set<Long> peerids = workerpeerlist.keySet();
        Collection<WorkerPeer> workerpeers = workerpeerlist.values();
        String workername = null;
        WorkerPeer workerpeer = null;

        for (Long peerid : peerids) {
            // Send a message to ask those peers whose peerids are smaller than this 
            if (this.peerid > peerid) {
                workerpeer = (WorkerPeer) workerpeerlist.get(peerid);

                if (debug) {
                    workername = workernamelist.get(peerid);
                }
                try {
                    if (debug) {
                        logger.debug("Ask if " + workername + " can be a submaster");
                    }
                    workerpeer.canBeSubMaster();
                } catch (Exception e) {
                    if (debug) {
                        logger.debug("Worker" + workername + " is missing");
                    }
                }

                // If any of the workers has heartbeat, then go out and waiting
                return;
            }
        }

        // The workerpeer remove himself from the peerlist
        workerpeerlist.remove(this.peerid);

        // If no worker is alive, we then determined himself to be the submaster
        // Create a new active object submaster on this node 
        // by calling the special construction funciton of the AOSubMaster
        AOSubMaster submaster = new AOSubMaster();

        // Inform all the workerpeers in the list that he is the new submaster
        // Here we pass the argument like submaster stub or just submaster
        for (WorkerPeer peer : workerpeers) {
            peer.iAmSubmaster(submaster);
        }

        // The worker then terminate himself
        this.terminate();

    }

    public void initActivity(Body body) {
        // TODO Auto-generated method stub
        terminated = false;

        stubOnThis = (AOSubWorker) PAActiveObject.getStubOnThis();

        workerpeerlist = new HashMap<Long, WorkerPeer>();
        workernamelist = new HashMap<Long, String>();

        body.setImmediateService("addWorkerPeer");
        body.setImmediateService("canBeSubMaster");
        body.setImmediateService("iAmSubmaster");
        body.setImmediateService("updatePeerList");
        PAActiveObject.setImmediateService("heartBeat");
        //PAActiveObject.setImmediateService("terminate");

        // Initial Task
        stubOnThis.getTaskAndSchedule();
    }

    public BooleanWrapper terminate() {
        if (debug) {
            logger.debug("Terminating " + name + "...");
        }
        provider = null;
        stubOnThis = null;
        ((WorkerMemoryImpl) memory).clear();
        initialMemory.clear();

        if (debug) {
            logger.debug(name + " terminated...");
        }

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