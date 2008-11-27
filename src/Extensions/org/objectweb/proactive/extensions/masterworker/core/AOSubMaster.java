package org.objectweb.proactive.extensions.masterworker.core;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.group.Group;
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
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.ResultIntern;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.TaskIntern;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.TaskRepository;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.Worker;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerDeadListener;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerManager;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerMaster;
import org.objectweb.proactive.extensions.masterworker.interfaces.internal.WorkerWatcher;

import org.objectweb.proactive.extensions.masterworker.util.TaskID;
import org.objectweb.proactive.extensions.masterworker.util.TaskQueue;
import org.objectweb.proactive.gcmdeployment.GCMApplication;

public class AOSubMaster implements WorkerMaster , InitActive, Serializable{

    /**
     * log4j logger for the worker manager
     */
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.MASTERWORKER_WORKERMANAGER);
    private static final boolean debug = logger.isDebugEnabled();

    /** stub on this active object */
    private AOSubMaster stubOnThis;

    /** Worker manager entity (deploy workers) */
    private WorkerManager smanager;
    
    /** Name of the subMaster */
    private String name;
    /**
     * Initial memory of the workers
     */
    private MemoryFactory memoryFactory;
    
    // Act as workers
    /** The entity which will provide tasks to the worker (i.e. the master) */
    protected WorkerMaster provider;

    /** The tasks waiting for solving */
    private Queue<TaskIntern<Serializable>> pendingTasks;

    /** tasks that are currently processing */
    private HashMap<Long, String> launchedTasks;

    private long taskidcounter = 0;

    
	
	public AOSubMaster(){
		
	}
	
	public AOSubMaster(final String name, final WorkerMaster provider, final MemoryFactory memoryFactory) {
		this.name = name;
		this.provider = provider;
		this.memoryFactory = memoryFactory;
		
		if (debug) {
            logger.debug("Creating submaster : " + name);
        }
    }
	
	public void addResources(final Collection<Node> nodes) {
		
		if (debug) {
            logger.debug("Call add resource success!");
        }
		
		smanager.addResources(nodes);
	}
	

	public void initActivity(Body body) {
		// TODO Auto-generated method stub
		
		if (debug) {
            logger.debug("Init the submaster!");
        }
		
		stubOnThis = (AOSubMaster) PAActiveObject.getStubOnThis();
		try {
			try {
	            // These two objects are initiated inside the initActivity because of the need to the stub on this
	            // The resource manager
			
				smanager = (AOSubWorkerManager) PAActiveObject.newActive(AOSubWorkerManager.class.getName(),
	                new Object[] { stubOnThis, memoryFactory, stubOnThis.name});
	        } catch (ActiveObjectCreationException e) {
	            e.printStackTrace();
	        }
		}
	    catch (NodeException e) {
	    	e.printStackTrace();
	    }    
	}

	public BooleanWrapper forwardedTask(Long taskId, String oldWorkerName,
			String newWorkerName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Queue<TaskIntern<Serializable>> getTasks(Worker worker,
			String workerName, boolean reflooding) {
		// TODO Auto-generated method stub
		return null;
	}

	public void isCleared(Worker worker) {
		// TODO Auto-generated method stub
		
	}

	public BooleanWrapper sendResult(ResultIntern<Serializable> result,
			String workerName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Queue<TaskIntern<Serializable>> sendResultAndGetTasks(
			ResultIntern<Serializable> result, String workerName,
			boolean reflooding) {
		// TODO Auto-generated method stub
		return null;
	}

	public BooleanWrapper sendResults(List<ResultIntern<Serializable>> results,
			String workerName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Queue<TaskIntern<Serializable>> sendResultsAndGetTasks(
			List<ResultIntern<Serializable>> results, String workerName,
			boolean reflooding) {
		// TODO Auto-generated method stub
		return null;
	}

	public int countAvailableResults(String originatorName)
			throws IsClearingError {
		// TODO Auto-generated method stub
		return 0;
	}

	public int countPending(String originatorName) throws IsClearingError {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEmpty(String originatorName) throws IsClearingError {
		// TODO Auto-generated method stub
		return false;
	}

	public void setResultReceptionOrder(String originatorName, OrderingMode mode)
			throws IsClearingError {
		// TODO Auto-generated method stub
		
	}

	public void solveIntern(String originatorName,
			List<? extends Task<? extends Serializable>> tasks)
			throws IsClearingError {
		// TODO Auto-generated method stub
		
	}

	public List<Serializable> waitAllResults(String originatorName)
			throws TaskException, IsClearingError {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Serializable> waitKResults(String originatorName, int k)
			throws TaskException, IsClearingError {
		// TODO Auto-generated method stub
		return null;
	}

	public Serializable waitOneResult(String originatorName)
			throws TaskException, IsClearingError {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Serializable> waitSomeResults(String originatorName)
			throws TaskException {
		// TODO Auto-generated method stub
		return null;
	}

}