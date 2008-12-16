package org.objectweb.proactive.extensions.masterworker.core;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.annotation.Cache;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAEventProgramming;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.core.body.exceptions.SendRequestCommunicationException;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extensions.masterworker.TaskException;

import org.objectweb.proactive.extensions.masterworker.interfaces.DivisibleTask;
import org.objectweb.proactive.extensions.masterworker.interfaces.Master;
import org.objectweb.proactive.extensions.masterworker.interfaces.MemoryFactory;
import org.objectweb.proactive.extensions.masterworker.interfaces.Task;
import org.objectweb.proactive.extensions.masterworker.interfaces.SubMaster.OrderingMode;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.MasterIntern;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.ResultIntern;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.TaskIntern;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Worker;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerDeadListener;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerManager;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerPeer;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerWatcher;

import org.objectweb.proactive.extensions.masterworker.util.TaskID;
import org.objectweb.proactive.extensions.masterworker.util.TaskQueue;


public class AOSubMaster implements Serializable, WorkerMaster, InitActive, RunActive, MasterIntern,
        WorkerDeadListener, Worker {

    /**
     * log4j logger for the worker manager
     */
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.MASTERWORKER_SUBMASTER);
    private static final boolean debug = logger.isDebugEnabled();

    /** How many tasks do we initially send to each worker */
    private int initial_task_flooding = Master.DEFAULT_TASK_FLOODING;

    private int groupSize = 0;

    /** stub on this active object */
    private AOSubMaster stubOnThis;

    /** Worker manager entity (deploy workers) */
    private WorkerManager smanager;

    /** Pinger (checks that workers are alive) */
    private WorkerWatcher pinger;

    /** is the submaster terminating */
    private Object terminationResourceManagerAnswer;

    /** is the submaster terminated */
    private boolean terminated;
    private boolean terminating;

    /** is the submaster in the process of clearing all activity ? * */
    private boolean isClearing;

    /** is the submaster in the process of doing some FT mechanism for spawned tasks * */
    private boolean isInFTmechanism;

    /** Name of the subMaster */
    private String name;

    // Workers resources :

    /** stub to access group of workers */
    private Worker workerGroupStub;

    /** Group of workers */
    private Group<Worker> workerGroup;
    private HashMap<String, Worker> activeWorkers;

    /** Initial memory of the workers */
    private MemoryFactory memoryFactory;

    /** Stub to group of sleeping workers */
    private Worker sleepingGroupStub;

    /** Group of sleeping workers */
    private Group<Worker> sleepingGroup;
    private HashMap<String, Worker> sleepingWorkers;

    /** Group of cleared workers */
    private Set<String> clearedWorkers;

    /** Names of workers which have been spawned * */
    private Set<String> spawnedWorkerNames;

    /** Associations of workers and workers names */
    private HashMap<String, Worker> workersByName;

    /** Reverse associations of workers and workers names */
    private HashMap<Worker, String> workersByNameRev;

    /** Activity of workers, which workers is doing which task */
    private HashMap<String, Set<Long>> workersActivity;

    /** Related to Fault Tolerance Mechanism with divisible tasks * */
    private HashMap<Long, String> divisibleTasksAssociationWithWorkers;
    private List<Request> requestsToServeImmediately;

    // Act as workers
    /** The entity which will provide tasks to the worker (i.e. the master) */
    protected WorkerMaster provider;

    /** main tasks that are completed */
    private ResultQueue<Serializable> resultQueue;

    /** The tasks waiting for solving */
    private Queue<TaskIntern<Serializable>> pendingTasks;

    /** tasks that are currently processing */
    private HashMap<Long, String> launchedTasks;
    private HashMap<Long, TaskIntern<Serializable>> launchedTaskList;

    /** if there is a pending request from the client */
    private Request pendingRequest;

    /** if there is a pending request from the sub clients (the workers) */
    private HashMap<String, Request> pendingSubRequests;

    /** sub result queue list */
    private HashMap<Long, ResultQueue<Serializable>> subResultQueues;
    private HashMap<Long, Long> taskIdCounters;

    /** Fault tolerance purpose */
    private int futureCounter = 0;
    
    /** Filters * */
    private final FindWorkersRequests workersRequestsFilter = new FindWorkersRequests();
    private final FindWaitFilter findWaitFilter = new FindWaitFilter();
    private final NotTerminateFilter notTerminateFilter = new NotTerminateFilter();
    private final FinalNotTerminateFilter finalNotTerminateFilter = new FinalNotTerminateFilter();
    private final IsClearingFilter clearingFilter = new IsClearingFilter();

    public AOSubMaster() {

    }

    public AOSubMaster(final String name, final WorkerMaster provider, final MemoryFactory memoryFactory) {
        this.name = name;
        this.provider = provider;
        this.memoryFactory = memoryFactory;

        if (debug) {
            logger.debug("Creating submaster : " + name);
        }
    }
    
    public BooleanWrapper InitSubMaster(final long workerNameCounter, final HashMap<Long, WorkerPeer> workerPeers, final HashMap<String, Worker> workers) {
    	BooleanWrapper wrap = ((AOSubWorkerManager)smanager).initSubWorkerManager(workerNameCounter, workerPeers, workers);

    	if (debug) {
            logger.debug("Initionlize the submaster!");
        }
    	sleepingGroup.addAll(workers.values());
    	sleepingWorkers.putAll(workers);
    	try {
            sleepingGroupStub.wakeup();
        } catch (Exception e) {
            // We ignore NFE pinger is responsible for that
        }
    	PAFuture.waitFor(wrap);
        return new BooleanWrapper(true);
    }

    public void addResources(final Collection<Node> nodes) {

        if (debug) {
            logger.debug("Call add resource success!");
        }

        smanager.addResources(nodes);
        getTasksIntern(nodes.size());
        groupSize = groupSize + nodes.size();
    }

    public void initActivity(Body body) {
        // TODO Auto-generated method stub

        if (debug) {
            logger.debug("Init the submaster!");
        }

        stubOnThis = (AOSubMaster) PAActiveObject.getStubOnThis();

        // General initializations
        terminated = false;
        terminating = false;
        isClearing = false;
        isInFTmechanism = false;
        // Queues
        pendingTasks = new LinkedList<TaskIntern<Serializable>>();
        launchedTasks = new HashMap<Long, String>();
        launchedTaskList = new HashMap<Long, TaskIntern<Serializable>>();
        resultQueue = new ResultQueue<Serializable>(Master.COMPLETION_ORDER);

        pendingSubRequests = new HashMap<String, Request>();
        subResultQueues = new HashMap<Long, ResultQueue<Serializable>>();
        taskIdCounters = new HashMap<Long, Long>();
        clearedWorkers = new HashSet<String>();
        spawnedWorkerNames = new HashSet<String>();
        divisibleTasksAssociationWithWorkers = new HashMap<Long, String>();
        requestsToServeImmediately = new ArrayList<Request>();

        // Workers
        try {
            String workerClassName = Worker.class.getName();
            // Worker Group
            workerGroupStub = (Worker) PAGroup.newGroup(workerClassName);
            workerGroup = PAGroup.getGroup(workerGroupStub);
            activeWorkers = new HashMap<String, Worker>();
            // Group of sleeping workers
            sleepingGroupStub = (Worker) PAGroup.newGroup(workerClassName);
            sleepingGroup = PAGroup.getGroup(sleepingGroupStub);
            sleepingWorkers = new HashMap<String, Worker>();
            workersActivity = new HashMap<String, Set<Long>>();
            workersByName = new HashMap<String, Worker>();
            workersByNameRev = new HashMap<Worker, String>();
        } catch (ClassNotReifiableException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // These two objects are initiated inside the initActivity because of the need to the stub on this
            // The resource manager
            smanager = (AOSubWorkerManager) PAActiveObject.newActive(AOSubWorkerManager.class.getName(),
                    new Object[] { stubOnThis, provider, memoryFactory, name }, PAActiveObject.getNode());

            // The worker pinger
            pinger = (WorkerWatcher) PAActiveObject.newActive(AOPinger.class.getName(),
                    new Object[] { stubOnThis }, PAActiveObject.getNode());
        } catch (ActiveObjectCreationException e1) {
            e1.printStackTrace();
        } catch (NodeException e1) {
            e1.printStackTrace();
        }

        PAActiveObject.setImmediateService("heartBeat");
        PAActiveObject.setImmediateService("initSubMaster");
        stubOnThis.getIntialTasks();
    }

    public BooleanWrapper forwardedTask(Long taskId, String oldWorkerName, String newWorkerName) {
        // TODO Auto-generated method stub
        // It is specially used for divisible tasks
        // Just like what the AOMaster do, change the link of the worker with the task
        if (debug) {
            logger.debug(oldWorkerName + " forwarded Task " + taskId + " to " + newWorkerName);
        }
        
        Set<Long> wact = workersActivity.get(oldWorkerName);
        wact.remove(taskId);
        if (wact.size() == 0) {
            workersActivity.remove(oldWorkerName);
        }
        HashSet<Long> newSet = new HashSet<Long>();
        newSet.add(taskId);
        workersActivity.put(newWorkerName, newSet);
        spawnedWorkerNames.add(newWorkerName);

        if (divisibleTasksAssociationWithWorkers.containsKey(taskId)) {
            divisibleTasksAssociationWithWorkers.remove(taskId);
        }

        divisibleTasksAssociationWithWorkers.put(taskId, newWorkerName);

        return new BooleanWrapper(true);
    }

    /**
     * Record the given worker in our system
     *
     * @param worker     the worker to record
     * @param workerName the name of the worker
     */
    public void recordWorker(final Worker worker, final String workerName) {
        if (!terminating) {
            // We record the worker in our system
            workersByName.put(workerName, worker);
            workersByNameRev.put(worker, workerName);
            workerGroup.add(worker);
            activeWorkers.put(workerName, worker);
            
            // We tell the pinger to watch for this new worker
            pinger.addWorkerToWatch(worker, workerName);
        }
    }

    /**
     * Tells if the master has some activity
     *
     * @return master activity
     */
    private boolean emptyPending() {
        return pendingTasks.isEmpty();
    }

    public void getIntialTasks() {
        if (debug) {
            logger.debug(name + " get initial tasks from the master...");
        }
        try {
            int flooding_value = initial_task_flooding;

            // Ask a task flooding for sleeping workers
            if (sleepingGroup.size() > 0)
                flooding_value = flooding_value + sleepingGroup.size();

            getTasksIntern(flooding_value);
        } catch (SendRequestCommunicationException exp) {
            if (debug) {
                logger.debug("Master has already been freed.");
            }
        }
    }

    public Queue<TaskIntern<Serializable>> getTasks(Worker worker, String workerName, int flooding) {
        if (debug) {
            logger.debug("Submaster " + name + " ask for new tasks from big master");
        }
        getTasksIntern(flooding);
        return getTasksInternal(worker, workerName, 1, flooding);
    }

    public Queue<TaskIntern<Serializable>> getTasksInternal(Worker worker, String workerName,
            final int resultSize, int flooding) {
        // TODO Auto-generated method stub
        // if we don't know him, we record the worker in our system
        if (!workersByName.containsKey(workerName)) {
            if (debug) {
                logger.debug("new worker " + workerName + " recorded by the master");
            }
            recordWorker(worker, workerName);
            if (isClearing) {
                // If the master is clearing we send the clearing message to this new worker
                try {
                    worker.clear();
                } catch (Exception e) {
                    if (debug) {
                        logger.debug("Clear worker " + workerName + " error");
                    }
                }
            }
        }

        if (emptyPending()) {
            // We say that the worker is sleeping if we don't know it yet or if it's not doing a task
            if (workersActivity.containsKey(workerName)) {
                // If the worker requests a flooding this means that its penqing queue is empty,
                // thus, it will sleep
                if (flooding > 0) {
                	if (!sleepingWorkers.containsKey(workerName)) {
                        if (debug) {
                            logger.debug("Add worker " + workerName + " to sleeping group");
                        }
                        sleepingGroup.add(worker);
                        sleepingWorkers.put(workerName, worker);
                    }

                }
            } else {
                workersActivity.put(workerName, new HashSet<Long>());
                if (!sleepingWorkers.containsKey(workerName)) {
                    if (debug) {
                        logger.debug("Add worker " + workerName + " to sleeping group");
                    }
                    sleepingGroup.add(worker);
                    sleepingWorkers.put(workerName, worker);
                }

            }
            if (debug) {
                logger.debug("No task given to " + workerName);
            }
            // we return an empty queue, this will cause the worker to sleep for a while
            return new LinkedList<TaskIntern<Serializable>>();
        } else {
        	if (sleepingWorkers.containsKey(workerName)) {
                sleepingGroup.remove(worker);
                sleepingWorkers.remove(workerName);
            }
            Queue<TaskIntern<Serializable>> tasksToDo = new LinkedList<TaskIntern<Serializable>>();

            // If we are in a flooding scenario, we send at most initial_task_flooding tasks
            int flooding_value = 0;
            if (flooding > 0)
                flooding_value = resultSize + flooding * initial_task_flooding - 1;
            else
                flooding_value = resultSize + flooding * initial_task_flooding;
            int i = 0;
            while (!pendingTasks.isEmpty() && i < flooding_value) {
                TaskIntern<Serializable> task = pendingTasks.poll();

                // We add the task inside the launched list
                long tid = task.getId();
                launchedTasks.put(tid, null);
                launchedTaskList.put(tid, task);
                // We record the worker activity
                if (workersActivity.containsKey(workerName)) {
                    Set<Long> wact = workersActivity.get(workerName);
                    wact.add(tid);
                } else {
                    Set<Long> wact = new HashSet<Long>();
                    wact.add(tid);
                    workersActivity.put(workerName, wact);
                }
                tasksToDo.offer(task);
                if (debug) {
                    logger.debug("Task " + tid + " given to " + workerName);
                }
                i++;

                // In case of a divisible task we don't want to do a flooding
                if (task.getTask() instanceof DivisibleTask) {
                    break;
                }
            }

            return tasksToDo;
        }

    }

    public void isCleared(Worker worker) {
        // TODO Auto-generated method stub
    	try {
    		String workerName = workersByNameRev.get(worker);
    		if(!clearedWorkers.contains(workerName)){
            	if (debug) {
                    logger.debug(workerName + " is cleared");
                }
            	clearedWorkers.add(workerName);
            }
        } catch (Exception e) {
            if (debug) {
                logger.debug("Cleared worker is dead");
            }
        }
    	
        
    }
    
    public void isCleared(String workerName) {
        // TODO Auto-generated method stub
    	
        if(!clearedWorkers.contains(workerName)){
        	if (debug) {
                logger.debug(workerName + " is cleared");
            }
        	clearedWorkers.add(workerName);
        }
    }

    public BooleanWrapper sendResultFromMaster(ResultIntern<Serializable> result, String workerName) {
    	long id = getDivisibleTaskIDByOriginatorName(workerName);
        if (subResultQueues.containsKey(id)) {
            if (debug) {
                logger.debug("Master sends result of task " + result.getId() + " of divisible task " + id);
            }
            subResultQueues.get(id).addCompletedTask(result);
            if (debug) {
                // logger.debug("The result of task " + result.getId() + " is " + result.getResult().toString());
            }
            return new BooleanWrapper(true);
        } else {
            // do nothing
            if (debug) {
                logger.debug("Master sends result of task " + result.getId() + " of worker " + workerName +
                    " but it's unknown.");
            }
            return new BooleanWrapper(false);
        }
    }

    public BooleanWrapper sendResult(ResultIntern<Serializable> result, String workerName) {
        // TODO Auto-generated method stub
        long id = result.getId();
        if (launchedTasks.containsKey(id)) {
            if (debug) {
                logger.debug(workerName + " sends result of task " + id);
            }
            String submitter = launchedTasks.remove(id);

            // We remove the task from the worker activity
            if (workersActivity.containsKey(workerName)) {
                Set<Long> wact = workersActivity.get(workerName);
                wact.remove(id);
                if (wact.size() == 0) {
                    workersActivity.remove(workerName);
                }
            }
            if (divisibleTasksAssociationWithWorkers.containsKey(id)) {
                if (spawnedWorkerNames.contains(workerName)) {
                    // We remove the spawned worker from our knowledge
                    spawnedWorkerNames.remove(workerName);
                }

                divisibleTasksAssociationWithWorkers.remove(id);
            }
            // We add the result in the result queue
            if (submitter == null) {
                resultQueue.addCompletedTask(result);
            } else {
                if (debug) {
                    logger.debug(workerName + " sends result of task " + id + " but the worker is unknown.");
                }
            }

            // Remove the task from the task list
            launchedTaskList.remove(id);
        } else {
            // do nothing
            if (debug) {
                logger.debug(workerName + " sends result of task " + id + " but it's unknown.");
            }
        }

        return new BooleanWrapper(true);
    }

    private void getTasksWithResults() {
        if (!terminating) {
            try {
                if (resultQueue.countAvailableResults() > 0) {
                    Queue<TaskIntern<Serializable>> tasksToAdd = null;

                    int flooding = 0;
                    if (pendingTasks.size() == 0)
                        flooding = 1;
                    tasksToAdd = provider.sendResultsAndGetTasks(resultQueue.getNextK(resultQueue
                            .countAvailableResults()), name, flooding);
                    PAEventProgramming.addActionOnFuture(tasksToAdd, "addTasksToPending");
                    futureCounter ++;
                }
            } catch (SendRequestCommunicationException exp) {
                if (debug) {
                    logger.debug("Master has already been freed.");
                }
            }
        }
    }

    private void getTasksIntern(int flooding) {
        if (!terminating) {
            Queue<TaskIntern<Serializable>> tasksToAdd = null;

            if (pendingTasks.size() == 0 && flooding < 1)
                flooding = 1;
            try {
                tasksToAdd = provider.getTasks(stubOnThis, name, flooding);
                PAEventProgramming.addActionOnFuture(tasksToAdd, "addTasksToPending");
                futureCounter++;
            } catch (SendRequestCommunicationException exp) {
                if (debug) {
                    logger.debug("Master has already been freed.");
                }
            }
        }
    }

    public void addTasksToPending(Future<Queue<TaskIntern<Serializable>>> future) {
    	futureCounter--;
        if (!terminating && !isClearing) {
            Queue<TaskIntern<Serializable>> tasksToAdd = null;

            try {
                tasksToAdd = future.get();
                pendingTasks.addAll(tasksToAdd);
                // wake up sleeping workers

                if ((sleepingGroup.size() > 0) && (tasksToAdd.size() > 0)) {
                    if (debug) {
                        logger.debug("Waking up sleeping workers...");
                    }

                    // We wake up the sleeping guys
                    try {
                        sleepingGroupStub.wakeup();
                    } catch (Exception e) {
                        // We ignore NFE pinger is responsible for that
                    }
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Queue<TaskIntern<Serializable>> sendResultAndGetTasks(ResultIntern<Serializable> result,
            String workerName, int flooding) {
        // TODO Auto-generated method stub
        sendResult(result, workerName);
        Worker worker = workersByName.get(workerName);
        // if the worker has already reported dead, we need to handle that it suddenly reappears
        if (!workersByNameRev.containsKey(worker)) {
            // We do this by removing the worker from our database, which will trigger that it will be recorded again
            if (debug) {
                logger.debug("The worker is a dead worker, wo remove it, and wait for a new recorder");
            }
            workersByName.remove(workerName);
        }
        return getTasksInternal(worker, workerName, 1, flooding);
    }

    public BooleanWrapper sendResults(List<ResultIntern<Serializable>> results, String workerName) {
        // TODO Auto-generated method stub
        if (null == workerName) {
            // it should be the main client to send back the result of a divisble task
        }
        for (ResultIntern<Serializable> res : results) {
            sendResult(res, workerName);
        }
        return new BooleanWrapper(true);
    }

    public Queue<TaskIntern<Serializable>> sendResultsAndGetTasks(List<ResultIntern<Serializable>> results,
            String workerName, int flooding) {
        // TODO Auto-generated method stub
        sendResults(results, workerName);
        // if the worker has already reported dead, we need to handle that it suddenly reappears
        Worker worker = workersByName.get(workerName);
        if (!workersByNameRev.containsKey(worker)) {
            // We do this by removing the worker from our database, which will trigger that it will be recorded again
            workersByName.remove(workerName);
        }
        return getTasksInternal(worker, workerName, results.size(), flooding);
    }

    public int countAvailableResults(String originatorName) throws IsClearingError {
        // TODO Auto-generated method stub
        if (originatorName == null) {
            return resultQueue.countAvailableResults();
        } else {
            if (isClearing) {
                clearingCallFromSpawnedWorker(originatorName);
            }
            if (subResultQueues.containsKey(getDivisibleTaskIDByOriginatorName(originatorName))) {
                return subResultQueues.get(getDivisibleTaskIDByOriginatorName(originatorName))
                        .countAvailableResults();
            } else {
                throw new IllegalArgumentException("Unknown originator " + originatorName);
            }
        }
    }

    /** get id of the divisible task by the name of the worker */
    private long getDivisibleTaskIDByOriginatorName(String originatorName) {
        Set<Long> divisibletaskids = divisibleTasksAssociationWithWorkers.keySet();

        for (Long divisibletaskid : divisibletaskids) {
            if (divisibleTasksAssociationWithWorkers.get(divisibletaskid).equals(originatorName)) {
//            	if(debug){
//            		logger.debug("Worker " + originatorName + " is running divisible task " + divisibletaskid);
//            	}
                return divisibletaskid;
            }
        }        
        return -1;
    }

    public int countPending(String originatorName) throws IsClearingError {
        // TODO Auto-generated method stub
        if (originatorName == null) {
            return resultQueue.countPendingResults();
        } else {
            if (isClearing) {
                clearingCallFromSpawnedWorker(originatorName);
            }
            if (subResultQueues.containsKey(getDivisibleTaskIDByOriginatorName(originatorName))) {
                return subResultQueues.get(getDivisibleTaskIDByOriginatorName(originatorName))
                        .countPendingResults();
            } else {
                throw new IllegalArgumentException("Unknown originator " + originatorName);
            }
        }
    }

    public boolean isEmpty(String originatorName) throws IsClearingError {
        // TODO Auto-generated method stub
        if (originatorName == null) {
            return (resultQueue.isEmpty() && pendingTasks.isEmpty());
        } else {
            if (isClearing) {
                clearingCallFromSpawnedWorker(originatorName);
            }
            if (subResultQueues.containsKey(getDivisibleTaskIDByOriginatorName(originatorName))) {
                return (subResultQueues.get(getDivisibleTaskIDByOriginatorName(originatorName)).isEmpty());
            } else {
                throw new IllegalArgumentException("Unsupported originator " + originatorName);
            }
        }
    }

    public void setResultReceptionOrder(String originatorName, OrderingMode mode) throws IsClearingError {
        // TODO Auto-generated method stub
        if (originatorName == null) {
            resultQueue.setMode(mode);
        } else {
            if (isClearing) {
                clearingCallFromSpawnedWorker(originatorName);
            }
            if (subResultQueues.containsKey(getDivisibleTaskIDByOriginatorName(originatorName))) {
                subResultQueues.get(getDivisibleTaskIDByOriginatorName(originatorName)).setMode(mode);
            } else {
                ResultQueue rq = new ResultQueue(mode);
                rq.setMode(mode);
                subResultQueues.put(getDivisibleTaskIDByOriginatorName(originatorName), rq);
            }
        }
    }

    public void solveIntern(String originatorName, long divisibleTaskId, long taskIdCounter,
            List<? extends Task<? extends Serializable>> tasks) throws IsClearingError {
        // TODO Auto-generated method stub
        if (debug) {
            logger.debug("Request for solving tasks from " + originatorName);
        }

        if (originatorName != null && isClearing) {
            clearingCallFromSpawnedWorker(originatorName);
        }
        
        divisibleTaskId = getDivisibleTaskIDByOriginatorName(originatorName);

        // If the tasks has already been submitted
        if (taskIdCounters.containsKey(divisibleTaskId)) {
            if (taskIdCounters.get(divisibleTaskId) >= taskIdCounter) {
                // Do nothing
            	if (debug) {
                    logger.debug("The task list from " +  originatorName + " has already been solved... ");
                }
                return;
            }
            taskIdCounters.remove(divisibleTaskId);
        }
        taskIdCounters.put(divisibleTaskId, taskIdCounter);
        
        
        // If one worker is sending the tasks
        if (subResultQueues.containsKey(divisibleTaskId)) {
        	if (debug) {
                logger.debug("The divisible task " + divisibleTaskId + "has already had a result queue");
            }
        	for (long id = taskIdCounter; id < taskIdCounter + tasks.size(); id++) {
        		subResultQueues.get(divisibleTaskId).addPendingTask(id);
                if (debug) {
                    logger.debug("Add task " + id + " to the result queue of divisible task " +
                        divisibleTaskId);
                }
            }
            // If the divisible task has already been solved and new is rescheduled
            // Because of the old worker is missing, we do nothing
        } else {
            if (debug) {
                logger.debug("Create a resultqueue for the solve of the divisible task " + divisibleTaskId);
            }

            ResultQueue<Serializable> rq = new ResultQueue<Serializable>(resultQueue.getMode());
            for (long id = taskIdCounter; id < taskIdCounter + tasks.size(); id++) {
                rq.addPendingTask(id);
                if (debug) {
                    logger.debug("Add task " + id + " to the result queue of divisible task " +
                        divisibleTaskId);
                }
            }

            subResultQueues.put(divisibleTaskId, rq);

            // we can use rq.countAvailableResults() to see if the results are available
            // the initioal value is 0
        }

        // Ask the provider to solve, the divisibleTaskIdObj should be passed as a argument
        try {
            provider.solveIntern(originatorName, divisibleTaskId, taskIdCounter, tasks);
        } catch (IsClearingError ex) {
            // It happened when the master is cleaning, he cleaned all the tasks
            clearingCallFromSpawnedWorker(originatorName);
        }
    }

    public List<Serializable> waitAllResults(String originatorName) throws TaskException, IsClearingError {
        // TODO Auto-generated method stub
        List<Serializable> results = null;
        List<ResultIntern<Serializable>> completed = null;
        if (isInFTmechanism) {
            throw new MWFTError();
        }
        if (isClearing) {
            clearingCallFromSpawnedWorker(originatorName);
        }
        if (debug) {
            logger.debug("All results received by " + originatorName);
        }

        if (subResultQueues.containsKey(getDivisibleTaskIDByOriginatorName(originatorName))) {
            completed = subResultQueues.get(getDivisibleTaskIDByOriginatorName(originatorName)).getAll();

        } else
            throw new IllegalArgumentException("Unknown originator: " + originatorName);
        results = new ArrayList<Serializable>(completed.size());
        for (ResultIntern<Serializable> res : completed) {
            if (res.threwException()) {
                throw new RuntimeException(new TaskException(res.getException()));
            }

            results.add(res.getResult());
        }
        return results;
    }

    public List<Serializable> waitKResults(String originatorName, int k) throws TaskException,
            IsClearingError {
        // TODO Auto-generated method stub
        List<Serializable> results = new ArrayList<Serializable>(k);
        List<ResultIntern<Serializable>> completed = null;
        if (isInFTmechanism) {
            throw new MWFTError();
        }
        if (isClearing) {
            clearingCallFromSpawnedWorker(originatorName);
        }
        if (subResultQueues.containsKey(getDivisibleTaskIDByOriginatorName(originatorName))) {
            ResultQueue rq = subResultQueues.get(getDivisibleTaskIDByOriginatorName(originatorName));
            if ((rq.countPendingResults() + rq.countAvailableResults()) < k) {
                throw new IllegalArgumentException("" + k + " is too big");
            } else if (k <= 0) {
                throw new IllegalArgumentException("Wrong value : " + k);
            }

            if (debug) {
                logger.debug("" + k + " results received by " + originatorName);
            }
            completed = rq.getNextK(k);
        } else
            throw new IllegalArgumentException("Unknown originator: " + originatorName);
        for (ResultIntern<Serializable> res : completed) {
            if (res.threwException()) {
                throw new RuntimeException(new TaskException(res.getException()));
            }

            results.add(res.getResult());
        }
        return results;
    }

    public Serializable waitOneResult(String originatorName) throws TaskException, IsClearingError {
        // TODO Auto-generated method stub
        ResultIntern<Serializable> res = null;
        if (isInFTmechanism) {
            throw new MWFTError();
        }
        if (isClearing) {
            clearingCallFromSpawnedWorker(originatorName);
        }
        if (subResultQueues.containsKey(getDivisibleTaskIDByOriginatorName(originatorName))) {
            res = subResultQueues.get(getDivisibleTaskIDByOriginatorName(originatorName)).getNext();

            if (debug) {
                logger.debug("Result of task " + res.getId() + " received by " + originatorName);
            }

        } else
            throw new IllegalArgumentException("Unknown originator: " + originatorName);

        if (res.threwException()) {
            throw new RuntimeException(new TaskException(res.getException()));
        }
        return res.getResult();
    }

    public List<Serializable> waitSomeResults(String originatorName) throws TaskException {
        // TODO Auto-generated method stub
        List<Serializable> results = new ArrayList<Serializable>();
        List<ResultIntern<Serializable>> completed = null;
        if (isInFTmechanism) {
            throw new MWFTError();
        }
        if (isClearing) {
            clearingCallFromSpawnedWorker(originatorName);
        }
        if (subResultQueues.containsKey(getDivisibleTaskIDByOriginatorName(originatorName))) {
            ResultQueue rq = subResultQueues.get(getDivisibleTaskIDByOriginatorName(originatorName));
            int k = rq.countAvailableResults();

            if (debug) {
                logger.debug("" + k + " results received by " + originatorName);
            }
            completed = rq.getNextK(k);
        } else
            throw new IllegalArgumentException("Unknown originator: " + originatorName);

        for (ResultIntern<Serializable> res : completed) {
            if (res.threwException()) {
                throw new RuntimeException(new TaskException(res.getException()));
            }

            results.add(res.getResult());
        }
        return results;
    }

    public void clear() {
        // TODO Auto-generated method stub
        // Clearing the master is a quite complicated mechanism
        // It is not possible to wait synchronously for every workers'reply because workers might be requesting something from the master at the same time
        // therefore the clearing process must be first initiated, a message sent to every workers, and then the master will enter a mode "clearing"
        // where every call from the workers will be served immediately by an exception, excepting the acknowledgement of the clear message.
        // When every workers have answered the master will be declared "cleared" and it can starts its normal serving 

        if (debug) {
            logger.debug("SubMaster " + name + " is clearing...");
        }
        
        // Clear the id counters
        taskIdCounters.clear();
        
        // We clear the queues
        resultQueue.clear();
        pendingTasks.clear();
        launchedTasks.clear();
        launchedTaskList.clear();
        for (ResultQueue<Serializable> queue : subResultQueues.values()) {
            queue.clear();
        }
        subResultQueues.clear();
        // We clear the workers activity memory
        try {
            workersActivity.clear();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (debug) {
                logger.debug("A exception happened in clearing workersActivity");
            }
        }

        // We clear every sleeping workers registered
//        try {
//            sleepingGroup.clear();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            if (debug) {
//                logger.debug("A exception happened in clearing sleepingGroup");
//            }
//        }
        
        // We tell all the worker to clear their pending tasks
        try {
            workerGroupStub.clear();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (debug) {
                logger.debug("A exception happened in clearing workerGroup");
            }
        }

        // We clear the repository
        taskIdCounters.clear();
        // We clear the taskIdCounters
        isClearing = true;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof AOSubMaster) && name.equals(((Worker) obj).getName());
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public BooleanWrapper heartBeat() {
        // TODO Auto-generated method stub
        return new BooleanWrapper(true);
    }

    public BooleanWrapper terminate() {
        // TODO Auto-generated method stub
        terminateIntern(false);

        return new BooleanWrapper(true);
    }

    public void wakeup() {
        // TODO Auto-generated method stub
        if (debug) {
            logger.debug(name + " receives a wake up message...");
        }

        if (pendingTasks.size() > 0) {
            if (debug) {
                logger.debug(name + " ignored wake up message ...");
            }
        } else {
            if (debug) {
                logger.debug(name + " wakes up...");
            }
            // Initial Task
            stubOnThis.getIntialTasks();
            
            // Wake up sleeping 
            if (debug) {
                logger.debug("Sleeping group size is:" + sleepingGroup.size());
            }
        }
    }

    public void runActivity(Body body) {
        // TODO Auto-generated method stub
        Service service = new Service(body);
        while (!terminated) {
            try {
                service.waitForRequest();
                // Sweep of wait requests
                sweepWaitRequests(service);
                maybeServePending(service);

                // Serving methods other than waitXXX
                while (!isClearing && service.hasRequestToServe()) {
                    Request oldest = service.getOldest();
                    while (!isClearing && (oldest != null) && !workersRequestsFilter.acceptRequest(oldest) &&
                        notTerminateFilter.acceptRequest(oldest)) {
                        // Sweep of wait requests
                        sweepWaitRequests(service);
                        // Serving quick requests
                        service.serveOldest();

                        // we maybe serve the pending waitXXX methods if there are some and if the necessary results are collected
                        maybeServePending(service);
                        oldest = service.getOldest();

                    }
                    if (!isClearing && (oldest != null) && notTerminateFilter.acceptRequest(oldest)) {
                        // Sweep of wait requests
                        sweepWaitRequests(service);
                        // Serving worker requests
                        service.serveOldest();
                        // we maybe serve the pending waitXXX methods if there are some and if the necessary results are collected
                        maybeServePending(service);
                    }
                    // Send results back to main master and get new tasks
                    getTasksWithResults();
                }

                // If a clear request is detected we enter a special mode
                if (isClearing) {
                    clearingRunActivity(service);
                }

                service.serveAll("secondTerminate");
                while (PAFuture.isAwaited(terminationResourceManagerAnswer)) {
                    service.serveAll(finalNotTerminateFilter);
                    // avoids devouring CPU cycles
                    Thread.sleep(100);
                }
                service.serveAll("finalTerminate");
                service.serveAll("awaitsTermination");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("SubMaster terminated...");
        }

        // we clear the service to avoid dirty pending requests 
        service.flushAll();
        // we block the communications because a getTask request might still be coming from a worker created just before the master termination
        body.blockCommunication();
        // we finally terminate the master
        body.terminate();
    }

    private void sweepWaitRequests(Service service) {
        while (service.hasRequestToServe(findWaitFilter)) {
            Request waitRequest = service.getOldest(findWaitFilter);
            String originatorName = (String) waitRequest.getParameter(0);
            // if there is one and there was none previously found we remove it and store it for later
            if (originatorName == null) {
                pendingRequest = waitRequest;
                if (debug) {
                    logger.debug("pending waitXXX from main client stored");
                }
            } else {
                pendingSubRequests.put(originatorName, waitRequest);
                if (debug) {
                    logger.debug("pending waitXXX from " + originatorName + " stored");
                }
            }
            service.blockingRemoveOldest(findWaitFilter);
        }

    }

    private void clearingRunActivity(Service service) {

        // To prevent concurrent modification exception, as the servePending method modifies the pendingSubRequests collection
        Set<String> newSet = new HashSet<String>(pendingSubRequests.keySet());
        // We first serve the pending sub requests
        for (String originator : newSet) {
            servePending(originator, service);
        }

        while (isClearing) {

            if (service.hasRequestToServe(clearingFilter)) {
                service.serveOldest(clearingFilter);
            }
            //            if (debug) {
            //                logger.debug("\nCleared worker size is:" + clearedWorkers.size() + "\nworkergroup size is:" + workerGroup.size() + "\nspawnedWorkers size is:" + spawnedWorkerNames.size());
            //            }
            
            // We go on in this clearing activity until every workers have either responded or were reported missing OR we still wait for futures from the big master 
            if ((clearedWorkers.size() == workerGroup.size() + spawnedWorkerNames.size()) && futureCounter == 0) {
            	// Add all cleaned workers to sleepingGroup
                for (String workerName : clearedWorkers) {
                    if (!sleepingWorkers.containsKey(workerName)) {
                    	sleepingGroup.add(workersByName.get(workerName));
                        sleepingWorkers.put(workerName, workersByName.get(workerName));
                    }
                }

                isClearing = false;
                clearedWorkers.clear();
                break;
            }
            // ugly sleep but the service.waitForRequest() would return immediately here provided there are other requests than those of the filter
            // Besides that, performance is not mandatory in this mode
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (debug) {
            logger.debug("SubMaster " + name + " cleared");
        }

        provider.isCleared(name);
    }

    /**
     * If there is a pending waitXXX method, we serve it if the necessary results are collected
     *
     * @param service
     */
    private void maybeServePending(Service service) {
        // We first serve the requests which MUST be served for FT purpose
        isInFTmechanism = true;
        if (!requestsToServeImmediately.isEmpty()) {
            for (Request req : requestsToServeImmediately) {
                if (debug) {
                    String originator = (String) req.getParameter(0);
                    logger.debug("forcefully serving waitXXX request from " + originator);
                }
                try {
                    service.serve(req);
                } catch (Throwable e) {
                    // ignore connection errors
                }
            }
            requestsToServeImmediately.clear();
        }
        isInFTmechanism = false;

        // To prevent concurrent modification exception, as the servePending method modifies the pendingSubRequests collection
        Set<Map.Entry<String, Request>> newSet = new HashSet<Map.Entry<String, Request>>(pendingSubRequests
                .entrySet());
        for (Map.Entry<String, Request> ent : newSet) {
            Request req = ent.getValue();
            String originator = ent.getKey();
            String methodName = req.getMethodName();
            ResultQueue rq = subResultQueues.get(getDivisibleTaskIDByOriginatorName(originator));
            if (rq != null) {
                if ((methodName.equals("waitOneResult") || methodName.equals("waitSomeResults")) &&
                    rq.isOneResultAvailable()) {
                    servePending(originator, service);
                } else if (methodName.equals("waitAllResults") && rq.areAllResultsAvailable()) {
                    servePending(originator, service);
                } else if (methodName.equals("waitKResults")) {
                    int k = (Integer) req.getParameter(1);
                    if (rq.countAvailableResults() >= k) {
                        servePending(originator, service);
                    }
                }
            }
        }
        newSet.clear();
        newSet = null;

        if (pendingRequest != null) {
            String methodName = pendingRequest.getMethodName();
            if ((methodName.equals("waitOneResult") || methodName.equals("waitSomeResults")) &&
                resultQueue.isOneResultAvailable()) {
                servePending(null, service);
            } else if (methodName.equals("waitAllResults") && resultQueue.areAllResultsAvailable()) {
                servePending(null, service);
            } else if (methodName.equals("waitKResults")) {
                int k = (Integer) pendingRequest.getParameter(1);
                if (resultQueue.countAvailableResults() >= k) {
                    servePending(null, service);
                }
            }
        }
    }

    /** Serve the pending waitXXX method */
    private void servePending(String originator, Service service) {
        if (originator == null) {
            if (debug) {
                logger.debug("serving pending waitXXX method from main client");
            }

            Request req = pendingRequest;
            pendingRequest = null;
            service.serve(req);
        } else {
            if (debug) {
                logger.debug("serving pending waitXXX method from " + originator);
            }
            Request req = pendingSubRequests.remove(originator);
            service.serve(req);
        }
    }

    /** {@inheritDoc} */
    public void setPingPeriod(long periodMillis) {
        pinger.setPingPeriod(periodMillis);
    }

    /** {@inheritDoc} */
    public void setInitialTaskFlooding(final int number_of_tasks) {
        initial_task_flooding = number_of_tasks;
    }

    /** {@inheritDoc} */
    public int workerpoolSize() {
        return workerGroup.size();
    }

    /**
     * When the master is clearing
     * Throws an exception to workers waiting for an answer from the master
     *
     * @param originator worker waiting
     * @throws IsClearingError to notify that it's clearing
     */
    private void clearingCallFromSpawnedWorker(String originator) throws IsClearingError {
        if (workersActivity.containsKey(originator))
            workersActivity.remove(originator);
        if (spawnedWorkerNames.contains(originator)) {
            if (debug) {
                logger.debug(originator + " is cleared");
            }
            spawnedWorkerNames.remove(originator);
        }

        throw new IsClearingError();
    }

    /**
     * Synchronous version of terminate
     *
     * @param freeResources do we free as well deployed resources
     * @return true if completed successfully
     */
    public BooleanWrapper terminateIntern(final boolean freeResources) {

        if (debug) {
            logger.debug("Terminating SubMaster " + name + "...");
        }

        // The cleaner way is to first clear the activity
        // clear();

        // then delay final termination
        stubOnThis.secondTerminate(freeResources);

        return new BooleanWrapper(true);

    }

    public boolean awaitsTermination() {
        return true;
    }

    protected BooleanWrapper secondTerminate(final boolean freeResources) {

        if (debug) {
            logger.debug("SencondTerminate of Subaster " + name + "...");
        }
        // We empty pending queues
        pendingTasks.clear();
        launchedTasks.clear();
        launchedTaskList.clear();
        workersActivity.clear();

        // We terminate the pinger
        PAFuture.waitFor(pinger.terminate());

        // We terminate the worker manager asynchronously to avoid a deadlock occuring
        // when a newly created worker asks for tasks during the termination algorithm
        terminationResourceManagerAnswer = smanager.terminate(freeResources);

        // Last terminate message
        stubOnThis.finalTerminate();

        terminating = true;

        if (debug) {
            logger.debug("SencondTerminate of Subaster " + name + " finished...");
        }
        return new BooleanWrapper(true);
    }

    protected BooleanWrapper finalTerminate() {

        workersByName.clear();
        workersByNameRev.clear();

        // we empty groups
        workerGroup.purgeExceptionAndNull();
        workerGroup.clear();
        workerGroupStub = null;
        activeWorkers.clear();
        sleepingGroup.purgeExceptionAndNull();
        sleepingGroup.clear();
        sleepingGroupStub = null;
        sleepingWorkers.clear();

        clearedWorkers.clear();
        pendingRequest = null;

        pinger = null;
        smanager = null;

        stubOnThis = null;

        terminated = true;
        return new BooleanWrapper(true);
    }

    /**
     * @author The ProActive Team
     *         Internal class for filtering requests in the queue
     */
    private class FindWaitFilter implements RequestFilter {

        /** Creates a filter */
        public FindWaitFilter() {
        }

        /** {@inheritDoc} */
        public boolean acceptRequest(final Request request) {
            // We find all the requests that are not servable yet
            String name = request.getMethodName();
            return name.startsWith("wait");
        }
    }

    /**
     * @author The ProActive Team
     *         Internal class for filtering requests in the queue
     */
    private class NotTerminateFilter implements RequestFilter {

        /** Creates a filter */
        public NotTerminateFilter() {
        }

        /** {@inheritDoc} */
        public boolean acceptRequest(final Request request) {
            // We find all the requests that are not servable yet
            String name = request.getMethodName();
            return !name.equals("secondTerminate") && !name.equals("awaitsTermination") &&
                !name.equals("secondTerminate") && !name.equals("finalTerminate");
        }
    }

    /**
    * @author The ProActive Team
    *         Internal class for filtering requests in the queue
    */
    private class FinalNotTerminateFilter implements RequestFilter {

        /** Creates a filter */
        public FinalNotTerminateFilter() {
        }

        /** {@inheritDoc} */
        public boolean acceptRequest(final Request request) {
            // We find all the requests that are not servable yet
            String name = request.getMethodName();
            return !name.equals("finalTerminate") && !name.equals("awaitsTermination");
        }
    }

    /**
     * @author The ProActive Team
     *         Internal class for filtering requests in the queue
     */
    private class FindWorkersRequests implements RequestFilter {

        /** Creates the filter */
        public FindWorkersRequests() {
        }

        /** {@inheritDoc} */
        public boolean acceptRequest(final Request request) {
            // We find all the requests which can't be served yet
            String name = request.getMethodName();
            return name.startsWith("sendResult") || name.startsWith("getTask") ||
                name.equals("forwardedTask");
        }
    }

    private class IsClearingFilter implements RequestFilter {

        public IsClearingFilter() {

        }

        public boolean acceptRequest(Request request) {
            // We serve with an exception every request coming from workers (task requesting, results sending, result waiting), we serve nicely the isCleared request, finally, we serve as well the isDead notification coming from the pinger
            String name = request.getMethodName();
            if (name.equals("solveIntern") || name.startsWith("wait") || name.equals("isEmpty") ||
                name.equals("setResultReceptionOrder") || name.equals("countPending") ||
                name.equals("countAvailableResults")) {
                return request.getParameter(0) != null;
            }
            return (name.equals("isCleared") || name.equals("isDead") || name.equals("sendResult") ||
                name.equals("sendResultAndGetTasks") || name.equals("getTasks")) ||
                name.equals("forwardedTask") || name.equals("addTasksToPending");

        }
    }

    public boolean isDead(Worker worker) {
        // TODO Auto-generated method stub
    	throw new UnsupportedOperationException("Error arguments when call isDead");

    }

    /**
     * Removes all the activity generated by one dead worker
     *
     * @param workerName
     */
    private void removeActivityOfWorker(String workerName) {
        // if the worker was handling tasks we put the tasks back to the pending queue
        if (workersActivity.containsKey(workerName)) {
            for (Long taskId : workersActivity.get(workerName)) {
                if (launchedTasks.containsKey(taskId)) {

                    if (debug) {
                        logger.debug("Rescheduling task " + taskId);
                    }

                    if (emptyPending()) {
                        // if the queue was empty before the task is rescheduled, we wake-up all sleeping workers
                        if (sleepingGroup.size() > 0) {
                            if (debug) {
                                logger.debug("Waking up sleeping workers...");
                            }

                            // We wake up the sleeping guys
                            try {
                                sleepingGroupStub.wakeup();
                            } catch (Exception e) {
                                // We ignore NFE pinger is responsible for that
                            }
                        }
                    }

                    // get the task and add to the pending tasks again
                    pendingTasks.add(launchedTaskList.remove(taskId));
                }
            }
            try {
            	workersActivity.get(workerName).clear();
            } catch (Exception e) {
                if (debug) {
                    logger.debug("Clear worker " + workerName + "error");
                }
            }
            workersActivity.remove(workerName);
        }
    }

    public boolean isDead(final String workerName) {
    	if(workersByName.containsKey(workerName)){
    		if (logger.isInfoEnabled()) {
                logger.info(workerName + " reported missing... removing it");
            }

            // we remove the worker from our lists
            if (activeWorkers.containsKey(workerName)) {
                workerGroup.remove(workersByName.get(workerName));
                activeWorkers.remove(workerName);
                if (sleepingWorkers.containsKey(workerName)) {
                    sleepingGroup.remove(workersByName.get(workerName));
                    sleepingWorkers.remove(workerName);
                }
                if (clearedWorkers.contains(workerName)) {
                    clearedWorkers.remove(workerName);
                }

                // Among our "dictionary of workers", we remove only entries in the reverse dictionary,
                // By doing that, if ever the worker appears not completely dead and reappears, we can handle it
                workersByName.remove(workerName);
                // We remove the activity of this worker and every children workers
                removeActivityOfWorker(workerName);

                smanager.isDead(workerName);
            }
            return true;
    	}
    	else{
    		if (debug) {
                logger.debug("Pinger reports " + workerName + " missing but it is unknown");
            }
    		return false;
    	}
    }

}