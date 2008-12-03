package org.objectweb.proactive.extensions.masterworker.core;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.exceptions.SendRequestCommunicationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extensions.masterworker.interfaces.MemoryFactory;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Worker;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerManager;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerPeer;


public class AOSubWorkerManager implements WorkerManager, InitActive, Serializable {

    private String submasterName = "subname";

    /**
     * log4j logger for the worker manager
     */
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.MASTERWORKER_SUBWORKERMANAGER);
    private static final boolean debug = logger.isDebugEnabled();

    /**
     * Stub of this active object
     */
    private AOSubWorkerManager stubOnThis;

    /**
     * how many workers have been created
     */
    private long workerNameCounter;

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
     * workerpeers deployed so far
     */
    private HashMap<Long, WorkerPeer> workerpeers;

    public AOSubWorkerManager() {

    }

    /**
     * Creates a task manager with the given task provider
     *
     * @param provider      the entity that will give tasks to the workers created
     * @param memoryFactory factory which will create memory for each new workers
     */
    public AOSubWorkerManager(final WorkerMaster provider, final MemoryFactory memoryFactory,
            final String submasterName) {
        this.provider = provider;
        this.memoryFactory = memoryFactory;
        this.submasterName = submasterName;

    }

    /**
     * Broadcast the new added node to all the workers
     * For each peer, the addpeer operation should be used as immediate service
     * Problem is how to make it synchronize?
     * Maybe we use booleanwrapper to wait all the results?
     */
    private void broadcastNewPeer(final long peerid, String workername, final WorkerPeer workerpeer) {
        for (WorkerPeer subworker : workerpeers.values()) {
            BooleanWrapper wrap = subworker.addWorkerPeer(peerid, workername, workerpeer);
            PAFuture.waitFor(wrap);
        }
    }

    /**
     * Add workers to the SubMaster
     */
    private void createWorker(final Node node) {
        if (!isTerminated) {
            try {
                String nodename = node.getNodeInformation().getName();
                if (debug) {
                    logger.debug("Creating worker on " + nodename);
                }

                String workername = node.getVMInformation().getHostName() + "_" + workerNameCounter++ + "@" +
                    submasterName;

                AOSubWorker subworker = (AOSubWorker) PAActiveObject.newActive(AOSubWorker.class.getName(),
                        new Object[] { workername, (WorkerMaster) provider,
                                memoryFactory.newMemoryInstance(), workerNameCounter }, node);

                PAFuture.waitFor(subworker);

                // Creates the worker which will automatically connect to the master
                workers.put(workername, (Worker) subworker);
                workerpeers.put(workerNameCounter, (WorkerPeer) subworker);

                // Broadcast the new peer to all the workers of the submaster
                broadcastNewPeer(workerNameCounter, workername, subworker);

                if (debug) {
                    logger.debug("Worker " + workername + " created on " + nodename);
                }
            } catch (ActiveObjectCreationException e) {
                e.printStackTrace(); // bad node
            } catch (NodeException e) {
                e.printStackTrace(); // bad node
            }
        }
    }

    public void addResources(URL descriptorURL) throws ProActiveException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void addResources(URL descriptorURL, String virtualNodeName) throws ProActiveException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void addResources(final Collection<Node> nodes) {
        // TODO Auto-generated method stub
        if (!isTerminated) {
            for (Node node : nodes) {
                threadPool.execute(new WorkerCreationHandler(node));
            }
        }
    }

    public BooleanWrapper terminate(boolean freeResources) {
        // TODO Auto-generated method stub
        isTerminated = true;

        if (debug) {
            logger.debug("Terminating SubWorkerManager...");
        }

        try {
            // we shutdown the thread pool, no new thread will be accepted
            threadPool.shutdown();

            // we wait that all threads creating active objects finish
            threadPool.awaitTermination(120, TimeUnit.SECONDS);

            // we send the terminate message to every thread
            for (Entry<String, Worker> worker : workers.entrySet()) {
                String workerName = worker.getKey();
                try {
                    BooleanWrapper term = worker.getValue().terminate();
                    // as it is a termination algorithm we wait a bit, but not forever
                    PAFuture.waitFor(term);

                    if (debug) {
                        logger.debug(workerName + " freed.");
                    }
                } catch (SendRequestCommunicationException exp) {
                    if (debug) {
                        logger.debug(workerName + " is already freed.");
                    }
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
                logger.debug("SubWorkerManager terminated...");
            }

            return new BooleanWrapper(true);
        } catch (Exception e) {
            logger.error("Couldn't Terminate the Resource manager");
            e.printStackTrace();
            return new BooleanWrapper(false);
        }
    }

    public boolean isDead(Worker worker) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean isDead(String workerName) {
        // TODO Auto-generated method stub
        workers.remove(workerName);
        return true;
    }

    public void initActivity(Body body) {
        // TODO Auto-generated method stub
        stubOnThis = (AOSubWorkerManager) PAActiveObject.getStubOnThis();
        workerNameCounter = 0;
        workers = new HashMap<String, Worker>();
        workerpeers = new HashMap<Long, WorkerPeer>();

        isTerminated = false;
        if (debug) {
            logger.debug("Subresource Manager Initialized");
        }

        threadPool = Executors.newCachedThreadPool();
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
