package org.objectweb.proactive.extra.scheduler.core;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extra.scheduler.exception.NFEHandler;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.exception.UserException;
import org.objectweb.proactive.extra.scheduler.job.IdentifyJob;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.job.UserIdentification;
import org.objectweb.proactive.extra.scheduler.resourcemanager.InfrastructureManagerProxy;
import org.objectweb.proactive.extra.scheduler.task.TaskDescriptor;
import org.objectweb.proactive.extra.scheduler.task.TaskEvent;
import org.objectweb.proactive.extra.scheduler.task.TaskId;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerConnection;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerState;
import org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface;

/**
 * Scheduler Frontend. This is the API to talk to when you want to managed a scheduler core.
 * Creating this class can only be done by using <code>AdminScheduler</code>.
 * You can join this front-end by using the <code>join()</code> method
 * in {@link SchedulerConnection} .
 * 
 * @author ProActive Team
 * @version 1.0, Jun 28, 2007
 * @since ProActive 3.2
 */
public class SchedulerFrontend implements InitActive, SchedulerEventListener, UserSchedulerInterface, SchedulerCoreInterface {
	
	/** Serial Version UID */
	private static final long serialVersionUID = -7843011649407086298L;
	/** Scheduler logger */
	public static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);
	/** A repeated  warning message */
	private static final String LISTENER_WARNING = "!!!!!!!!!!!!!! Scheduler has detected that a listener is not connected anymore !";
	private static final String ACCESS_DENIED = "Access denied !";
	/** Multiplicatif factor for job id (taskId will be : this_factor*jobID+taskID) */
	public static final int JOB_FACTOR = 1000;
	/** Global count for jobs IDs generation */
	private static int jobGlobalCount = 1;
	/** Mapping on the UniqueId of the sender and the user/admin identifications */
	private HashMap<UniqueID,UserIdentification> identifications = new HashMap<UniqueID,UserIdentification>();
	/** Implementation of Resource Manager */
	private InfrastructureManagerProxy resourceManager;
	/** Full name of the policy class */
	private String policyFullName;
	/** Implementation of scheduler main structure */
	private SchedulerCore scheduler;
	/** Job identification management */
	private HashMap<JobId,IdentifyJob> jobs;
	/** scheduler listeners */
	private HashMap<UniqueID,SchedulerEventListener> schedulerListeners = new HashMap<UniqueID,SchedulerEventListener>();
	/** Scheduler's statistics */
	private Stats stats = new Stats();
	
	/**
	 * ProActive empty constructor
	 */
	public SchedulerFrontend(){}
	
	
	/**
	 * Scheduler Proxy constructor.
	 * 
	 * @param imp a resource manager which
	 * 				be able to managed the resource used by scheduler.
	 * @throws NodeException
	 * @throws ActiveObjectCreationException 
	 */
	public SchedulerFrontend(InfrastructureManagerProxy imp, String policyFullClassName) throws ActiveObjectCreationException, NodeException {
		logger.info("Creating scheduler core...");
		resourceManager = imp;
		policyFullName = policyFullClassName;
		jobs = new HashMap<JobId,IdentifyJob>();
	}

	
	/**
	 * @see org.objectweb.proactive.InitActive#initActivity(org.objectweb.proactive.Body)
	 */
	public void initActivity(Body body) {
		try {
			scheduler = (SchedulerCore) ProActive.newActive(
					SchedulerCore.class.getName(),
					new Object[] { resourceManager, ProActive.getStubOnThis(), policyFullName });
			ProActive.addNFEListenerOnAO(scheduler, new NFEHandler("Scheduler Core"));
			logger.info("Scheduler successfully created on "+ProActive.getNode().getNodeInformation().getHostName());
		} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
		} catch (NodeException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Connect a new user on the scheduler.
	 * This user can interact with the scheduler according to his right.
	 * 
	 * @param sourceBodyID the source ID of the connected object representing a user
	 * @param identification the identification of the connected user
	 */
	public void connect(UniqueID sourceBodyID, UserIdentification identification) throws SchedulerException {
		if (identifications.containsKey(sourceBodyID)){
			//TODO essayer de pinger et si ping on envoie l'exception sinon on le reconnect
			logger.warn("Active object already connected !");
			throw new SchedulerException("This active object is already connected to the scheduler !");
		}
		logger.info(identification.getUsername()+" successfully connected !");
		identifications.put(sourceBodyID, identification);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#getResult(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public JobResult getResult(JobId jobId) throws SchedulerException {
		//checking permissions
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		IdentifyJob ij = jobs.get(jobId);
		if (ij == null)
			throw new SchedulerException("The job represented by this ID is unknow !");
		if (!ij.hasRight(identifications.get(id)))
			throw new SchedulerException("You do not have permission to access this job !");
		//asking the scheduler for the result
		JobResult result = scheduler.getResults(jobId);
		if (result == null)
			throw new SchedulerException("The job represented by this ID is not finished or unknow !");
		//removing jobs from the global list : this job is no more managed
		jobs.remove(jobId);
		return result;
	}
	
	
	/**
	 * Submit a new job to the scheduler core.
	 * 
	 * @param job the new job to schedule.
	 * @return the job id of the given job.
	 * @throws UserException an exception containing explicit error message.
	 */
	public JobId submit(Job job) throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		//setting job informations
		if (job.getTasks().size() == 0)
			throw new SchedulerException("This job does not contain Tasks !! Insert tasks before submitting job.");
		//setting the job id and owner
		job.setId(new JobId(jobGlobalCount++));
		job.setOwner(identifications.get(id).getUsername());
		//setting the unique task IDs and jobId for each task
		int taskId = 1;
		for (TaskDescriptor td : job.getTasks()){
			job.setTaskId(td,new TaskId(job.getId().value()*JOB_FACTOR+(taskId++)));
			td.setJobInfo(job.getJobInfo());
		}
		jobs.put(job.getId(), new IdentifyJob(job.getId(),identifications.get(id)));
		scheduler.submit(job);
		//stats
		stats.increaseSubmittedJobCount(job.getType());
		return job.getId();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#listenLog(org.objectweb.proactive.extra.scheduler.job.JobId, java.lang.String, int)
	 */
	public void listenLog(JobId jobId, String hostname, int port) throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		IdentifyJob ij = jobs.get(jobId);
		if (ij == null)
			throw new SchedulerException("The job represented by this ID is unknow !");
		if (!ij.hasRight(identifications.get(id)))
			throw new SchedulerException("You do not have permission to listen the log of this job !");
		scheduler.listenLog(jobId, hostname, port);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#addSchedulerEventListener(org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener)
	 */
	public SchedulerState addSchedulerEventListener(SchedulerEventListener sel) throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		schedulerListeners.put(id,sel);
		return scheduler.getSchedulerState();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#start()
	 */
	public BooleanWrapper coreStart() {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id)){
			logger.warn(ACCESS_DENIED);
			return new BooleanWrapper(false);
		}
		if (!identifications.get(id).isAdmin()){
			logger.warn("You do not have permission to start the scheduler !");
			return new BooleanWrapper(false);
		}
		//stats
		stats.startTime();
		return scheduler.coreStart();
	}

	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#stop()
	 */
	public BooleanWrapper coreStop() {
		//TODO définir ce que fait le stop, le pause ...
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id)){
			logger.warn(ACCESS_DENIED);
			return new BooleanWrapper(false);
		}
		if (!identifications.get(id).isAdmin()){
			logger.warn("You do not have permission to start the scheduler !");
			return new BooleanWrapper(false);
		}
		//stats
		stats.stopTime();
		return scheduler.coreStop();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#pause()
	 */
	public BooleanWrapper corePause() {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id)){
			logger.warn(ACCESS_DENIED);
			return new BooleanWrapper(false);
		}
		if (!identifications.get(id).isAdmin()){
			logger.warn("You do not have permission to start the scheduler !");
			return new BooleanWrapper(false);
		}
		return scheduler.corePause();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#resume()
	 */
	public BooleanWrapper coreResume() {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id)){
			logger.warn(ACCESS_DENIED);
			return new BooleanWrapper(false);
		}
		if (!identifications.get(id).isAdmin()){
			logger.warn("You do not have permission to start the scheduler !");
			return new BooleanWrapper(false);
		}
		return scheduler.coreResume();
	}
	
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#shutdown()
	 */
	public BooleanWrapper coreShutdown() {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id)){
			logger.warn(ACCESS_DENIED);
			return new BooleanWrapper(false);
		}
		if (!identifications.get(id).isAdmin()){
			logger.warn("You do not have permission to start the scheduler !");
			return new BooleanWrapper(false);
		}
		return scheduler.coreShutdown();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#disconnect()
	 */
	public void disconnect() throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		schedulerListeners.remove(id);
		identifications.remove(id);
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#pause(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean pause(JobId jobId) {
		return scheduler.pause(jobId).booleanValue();
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#resume(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean resume(JobId jobId) {
		return scheduler.resume(jobId).booleanValue();
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#stop(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean stop(JobId jobId) {
		return scheduler.stop(jobId).booleanValue();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#newPendingJobEvent(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	public void newPendingJobEvent(Job job) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			try{
				schedulerListeners.get(iter.next()).newPendingJobEvent(job);
			} catch(Exception e){
				schedulerListeners.remove(iter);
				identifications.remove(iter);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#pendingToRunningJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	public void pendingToRunningJobEvent(JobEvent event) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			try{
				schedulerListeners.get(iter.next()).pendingToRunningJobEvent(event);
			} catch(Exception e){
				schedulerListeners.remove(iter);
				identifications.remove(iter);
				logger.error(LISTENER_WARNING);
			}
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#runningToFinishedJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	public void runningToFinishedJobEvent(JobEvent event) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			try{
				schedulerListeners.get(iter.next()).runningToFinishedJobEvent(event);
			} catch(Exception e){
				schedulerListeners.remove(iter);
				identifications.remove(iter);
				logger.error(LISTENER_WARNING);
			}
		}
		//stats
		stats.increaseFinishedJobCount(event.getNumberOfFinishedTasks());
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#removeFinishedJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	public void removeFinishedJobEvent(JobEvent event) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			try{
				schedulerListeners.get(iter.next()).removeFinishedJobEvent(event);
			} catch(Exception e){
				schedulerListeners.remove(iter);
				identifications.remove(iter);
				logger.error(LISTENER_WARNING);
			}
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#pendingToRunningTaskEvent(org.objectweb.proactive.extra.scheduler.task.TaskEvent)
	 */
	public void pendingToRunningTaskEvent(TaskEvent event) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			try{
				schedulerListeners.get(iter.next()).pendingToRunningTaskEvent(event);
			} catch(Exception e){
				schedulerListeners.remove(iter);
				identifications.remove(iter);
				logger.error(LISTENER_WARNING);
			}
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#runningToFinishedTaskEvent(org.objectweb.proactive.extra.scheduler.task.TaskEvent)
	 */
	public void runningToFinishedTaskEvent(TaskEvent event) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			try{
				schedulerListeners.get(iter.next()).runningToFinishedTaskEvent(event);
			} catch(Exception e){
				schedulerListeners.remove(iter);
				identifications.remove(iter);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#getStats()
	 */
	public Stats getStats() throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		return stats;
	}

}
