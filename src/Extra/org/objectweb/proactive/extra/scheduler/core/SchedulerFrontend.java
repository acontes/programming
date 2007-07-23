package org.objectweb.proactive.extra.scheduler.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
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

/**
 * Scheduler Frontend. This is the API to talk to when you want to managed a scheduler core.
 * Creating this class can only be done by using <code>AdminScheduler</code>.
 * You can join this front-end by using the <code>connectTo</code> method
 * in <code>UserScheduler</code> or <code>AdminScheduler</code>.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 28, 2007
 * @since ProActive 3.2
 */
public class SchedulerFrontend implements InitActive, SchedulerEventListener, UserSchedulerInterface, SchedulerInterface, SchedulerCoreInterface {
	
	/** Serial Version UID */
	private static final long serialVersionUID = -7843011649407086298L;
	/** Scheduler logger */
	public static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);
	/** default scheduler node name */
	public static final String SCHEDULER_DEFAULT_NAME = "SCHEDULER";
	public static final String SCHEDULER_LISTENER_WARNING = "!!!!!!!!!!!!!! Scheduler has detected that a listener is not connected anymore !";
	/** Multiplicatif factor for job id (taskId will be this_factor*jobID+taskID) */
	public static final int JOB_FACTOR = 1000;
	/** Global count for jobs IDs generation */
	private static int jobGlobalCount = 1;
	/** Implementation of Resource Manager */
	private InfrastructureManagerProxy resourceManager;
	/** Full name of the policy class */
	private String policyFullName;
	/** Implementation of scheduler Main structure */
	private SchedulerCore scheduler;
	/** Job identification management */
	private HashMap<JobId,IdentifyJob> jobs;
	/** scheduler listeners */
	private HashSet<SchedulerEventListener> schedulerListeners = new HashSet<SchedulerEventListener>();
	
	
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
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerInterface#getResult(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public JobResult getResult(JobId jobId) throws SchedulerException {
		JobResult result = scheduler.getResults(jobId);
		if (result == null)
			throw new SchedulerException("The job represented by this ID is not finished or unknow !");
		//remove jobs from the global list : this job is no more managed
		jobs.remove(jobId);
		return result;
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#getResult(org.objectweb.proactive.extra.scheduler.job.JobId, org.objectweb.proactive.extra.scheduler.job.UserIdentification)
	 */
	public JobResult getResult(JobId jobId, UserIdentification userId) throws SchedulerException {
		IdentifyJob ij = jobs.get(jobId);
		if (ij == null)
			throw new SchedulerException("The job represented by this ID is unknow !");
		if (!ij.hasRight(userId))
			throw new SchedulerException("You do not have right to access this job !");
		return getResult(jobId);
	}
	
	
	/**
	 * Submit a new job to the scheduler core.
	 * 
	 * @param job the new job to schedule.
	 * @return the job id of the given job.
	 * @throws UserException an exception containing explicit error message.
	 */
	public JobId submit(Job job) throws SchedulerException {
		job = setInformations(job);
		jobs.put(job.getId(), new IdentifyJob(job.getId()));
		scheduler.submit(job);
		return job.getId();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#submit(org.objectweb.proactive.extra.scheduler.job.Job, org.objectweb.proactive.extra.scheduler.job.UserIdentification)
	 */
	public JobId submit(Job job, UserIdentification userId) throws SchedulerException {
		//TODO user/admin identification
		//ProActive.getContext().getCurrentRequest().getSourceBodyID();
		job = setInformations(job);
		jobs.put(job.getId(), new IdentifyJob(job.getId(),userId));
		scheduler.submit(job);
		return job.getId();
	}

	
	/**
	 * Set Every job and tasks identifications in the current job.
	 * 
	 * @param job the job on which to set the id.
	 * @return the modified job.
	 * @throws SchedulerException
	 */
	private Job setInformations(Job job) throws SchedulerException {
		if (job.getTasks().size() == 0)
			throw new SchedulerException("This job does not contain Tasks !! Insert tasks before submitting job.");
		//setting the job id
		job.setId(new JobId(jobGlobalCount++));
		//setting the unique task IDs and jobId for each task
		int taskId = 1;
		for (TaskDescriptor td : job.getTasks()){
			job.setTaskId(td,new TaskId(job.getId().value()*JOB_FACTOR+(taskId++)));
			td.setJobInfo(job.getJobInfo());
		}
		return job;
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#start()
	 */
	public BooleanWrapper start() {
		return scheduler.start();
	}

	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#stop()
	 */
	public BooleanWrapper stop() {
		return scheduler.stop();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#pause()
	 */
	public BooleanWrapper pause() {
		return scheduler.pause();
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerInterface#resume()
	 */
	public BooleanWrapper resume() {
		return scheduler.resume();
	}
	
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#shutdown()
	 */
	public BooleanWrapper shutdown() {
		return scheduler.shutdown();
	}

	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerInterface#listenLog(org.objectweb.proactive.extra.scheduler.job.JobId, java.lang.String, int)
	 */
	public void listenLog(JobId jobId, String hostname, int port) {
		scheduler.listenLog(jobId, hostname, port);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#listenLog(org.objectweb.proactive.extra.scheduler.job.JobId, java.lang.String, int, org.objectweb.proactive.extra.scheduler.job.UserIdentification)
	 */
	public void listenLog(JobId jobId, String hostname, int port, UserIdentification userId) throws SchedulerException {
		IdentifyJob ij = jobs.get(jobId);
		if (ij == null)
			throw new SchedulerException("The job represented by this ID is unknow !");
		if (!ij.hasRight(userId))
			throw new SchedulerException("You do not have right to listen the log of this job !");
		listenLog(jobId, hostname, port);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#addSchedulerEventListener(org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener)
	 */
	public SchedulerState addSchedulerEventListener(SchedulerEventListener sel) {
		schedulerListeners.add(sel);
		return scheduler.getSchedulerState();
	}

	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#newPendingJobEvent(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	public void newPendingJobEvent(Job job) {
		Iterator<SchedulerEventListener> iter = schedulerListeners.iterator();
		while (iter.hasNext()){
			try{
				iter.next().newPendingJobEvent(job);
			} catch(Exception e){
				iter.remove();
				logger.error(SCHEDULER_LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#pendingToRunningJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	public void pendingToRunningJobEvent(JobEvent event) {
		Iterator<SchedulerEventListener> iter = schedulerListeners.iterator();
		while (iter.hasNext()){
			try {
				iter.next().pendingToRunningJobEvent(event);
			} catch (Exception e) {
				iter.remove();
				logger.error(SCHEDULER_LISTENER_WARNING);
			}
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#runningToFinishedJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	public void runningToFinishedJobEvent(JobEvent event) {
		Iterator<SchedulerEventListener> iter = schedulerListeners.iterator();
		while (iter.hasNext()){
			try {
				iter.next().runningToFinishedJobEvent(event);
			} catch (Exception e) {
				iter.remove();
				logger.error(SCHEDULER_LISTENER_WARNING);
			}
		}
	}


	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#removeFinishedJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	public void removeFinishedJobEvent(JobEvent event) {
		Iterator<SchedulerEventListener> iter = schedulerListeners.iterator();
		while (iter.hasNext()){
			try {
				iter.next().removeFinishedJobEvent(event);
			} catch (Exception e) {
				iter.remove();
				logger.error(SCHEDULER_LISTENER_WARNING);
			}
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#pendingToRunningTaskEvent(org.objectweb.proactive.extra.scheduler.task.TaskEvent)
	 */
	public void pendingToRunningTaskEvent(TaskEvent event) {
		Iterator<SchedulerEventListener> iter = schedulerListeners.iterator();
		while (iter.hasNext()){
			try {
				iter.next().pendingToRunningTaskEvent(event);
			} catch (Exception e) {
				iter.remove();
				logger.error(SCHEDULER_LISTENER_WARNING);
			}
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#runningToFinishedTaskEvent(org.objectweb.proactive.extra.scheduler.task.TaskEvent)
	 */
	public void runningToFinishedTaskEvent(TaskEvent event) {
		Iterator<SchedulerEventListener> iter = schedulerListeners.iterator();
		while (iter.hasNext()){
			try {
				iter.next().runningToFinishedTaskEvent(event);
			} catch (Exception e) {
				iter.remove();
				logger.error(SCHEDULER_LISTENER_WARNING);
			}
		}
	}

}
