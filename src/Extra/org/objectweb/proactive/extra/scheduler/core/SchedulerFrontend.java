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
import org.objectweb.proactive.extra.scheduler.job.LightJob;
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
		//make the light job
		job.setLightJob(new LightJob(job));
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
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id)){
			logger.warn(ACCESS_DENIED);
			return new BooleanWrapper(false);
		}
		if (!identifications.get(id).isAdmin()){
			logger.warn("You do not have permission to stop the scheduler !");
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
			logger.warn("You do not have permission to pause the scheduler !");
			return new BooleanWrapper(false);
		}
		//stats
		stats.pauseTime();
		return scheduler.corePause();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#corePauseImmediate()
	 */
	public BooleanWrapper coreImmediatePause() {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id)){
			logger.warn(ACCESS_DENIED);
			return new BooleanWrapper(false);
		}
		if (!identifications.get(id).isAdmin()){
			logger.warn("You do not have permission to pause the scheduler !");
			return new BooleanWrapper(false);
		}
		//stats
		stats.pauseTime();
		return scheduler.coreImmediatePause();
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
			logger.warn("You do not have permission to resume the scheduler !");
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
			logger.warn("You do not have permission to shutdown the scheduler !");
			return new BooleanWrapper(false);
		}
		return scheduler.coreShutdown();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#coreKill()
	 */
	public BooleanWrapper coreKill() {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id)){
			logger.warn(ACCESS_DENIED);
			return new BooleanWrapper(false);
		}
		if (!identifications.get(id).isAdmin()){
			logger.warn("You do not have permission to kill the scheduler !");
			return new BooleanWrapper(false);
		}
		return scheduler.coreKill();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#disconnect()
	 */
	public void disconnect() throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		String user = identifications.get(id).getUsername();
		schedulerListeners.remove(id);
		identifications.remove(id);
		logger.info("User "+user+" has left the scheduler !");
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#pause(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean pause(JobId jobId) throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		IdentifyJob ij = jobs.get(jobId);
		if (ij == null)
			throw new SchedulerException("The job represented by this ID is unknow !");
		if (!ij.hasRight(identifications.get(id)))
			throw new SchedulerException("You do not have permission to pause  this job !");
		return scheduler.pause(jobId).booleanValue();
	}
	

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#resume(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean resume(JobId jobId) throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		IdentifyJob ij = jobs.get(jobId);
		if (ij == null)
			throw new SchedulerException("The job represented by this ID is unknow !");
		if (!ij.hasRight(identifications.get(id)))
			throw new SchedulerException("You do not have permission to resume this job !");
		return scheduler.resume(jobId).booleanValue();
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#kill(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public boolean kill(JobId jobId) throws SchedulerException {
		UniqueID id = ProActive.getContext().getCurrentRequest().getSourceBodyID();
		if (!identifications.containsKey(id))
			throw new SchedulerException(ACCESS_DENIED);
		IdentifyJob ij = jobs.get(jobId);
		if (ij == null)
			throw new SchedulerException("The job represented by this ID is unknow !");
		if (!ij.hasRight(identifications.get(id)))
			throw new SchedulerException("You do not have permission to kill this job !");
		return scheduler.kill(jobId).booleanValue();
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
	
	
	/* ########################################################################################### */
	/*                                                                                             */
	/* ################################## LISTENER DISPATCHER #################################### */
	/*                                                                                             */
	/* ########################################################################################### */
	

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#SchedulerImmediatePausedEvent()
	 */
	public void SchedulerImmediatePausedEvent() {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).SchedulerImmediatePausedEvent();
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#SchedulerPausedEvent()
	 */
	public void SchedulerPausedEvent() {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).SchedulerPausedEvent();
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#SchedulerResumedEvent()
	 */
	public void SchedulerResumedEvent() {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).SchedulerResumedEvent();
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#SchedulerShutDownEvent()
	 */
	public void SchedulerShutDownEvent() {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).SchedulerShutDownEvent();
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#SchedulerShuttingDownEvent()
	 */
	public void SchedulerShuttingDownEvent() {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).SchedulerShuttingDownEvent();
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#SchedulerStartedEvent()
	 */
	public void SchedulerStartedEvent() {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).SchedulerStartedEvent();
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#SchedulerStoppedEvent()
	 */
	public void SchedulerStoppedEvent() {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).SchedulerStoppedEvent();
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#SchedulerkilledEvent()
	 */
	public void SchedulerkilledEvent() {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).SchedulerkilledEvent();
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#jobKilledEvent(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public void jobKilledEvent(JobId jobId) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).jobKilledEvent(jobId);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
		jobs.remove(jobId);
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#jobPausedEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	public void jobPausedEvent(JobEvent event) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).jobPausedEvent(event);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#jobResumedEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	public void jobResumedEvent(JobEvent event) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).jobResumedEvent(event);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener#newPendingJobEvent(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	public void newPendingJobEvent(Job job) {
		Iterator<UniqueID> iter = schedulerListeners.keySet().iterator();
		while (iter.hasNext()){
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).newPendingJobEvent(job);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
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
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).pendingToRunningJobEvent(event);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
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
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).runningToFinishedJobEvent(event);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
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
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).removeFinishedJobEvent(event);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
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
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).pendingToRunningTaskEvent(event);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
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
			UniqueID id = iter.next();
			try{
				schedulerListeners.get(id).runningToFinishedTaskEvent(event);
			} catch(Exception e){
				iter.remove();
				identifications.remove(id);
				logger.error(LISTENER_WARNING);
			}
		}
	}


	/**
	 * Terminate the schedulerConnexion active object and then this object.
	 */
	public void terminate() {
		//TODO supprimer l'objet actif de connection
		ProActive.terminateActiveObject(false);
	}

}
