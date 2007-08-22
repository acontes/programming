/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.scheduler.core;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.NodeSet;
import org.objectweb.proactive.extra.logforwarder.SimpleLoggerServer;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobPriority;
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.job.JobType;
import org.objectweb.proactive.extra.scheduler.job.LightJob;
import org.objectweb.proactive.extra.scheduler.job.LightTask;
import org.objectweb.proactive.extra.scheduler.policy.PolicyInterface;
import org.objectweb.proactive.extra.scheduler.resourcemanager.InfrastructureManagerProxy;
import org.objectweb.proactive.extra.scheduler.task.AppliTaskLauncher;
import org.objectweb.proactive.extra.scheduler.task.ApplicationTask;
import org.objectweb.proactive.extra.scheduler.task.Status;
import org.objectweb.proactive.extra.scheduler.task.TaskDescriptor;
import org.objectweb.proactive.extra.scheduler.task.TaskId;
import org.objectweb.proactive.extra.scheduler.task.TaskLauncher;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;
import org.objectweb.proactive.extra.scheduler.userAPI.JobState;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerInitialState;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerState;

/**
 * <i><font size="-1" color="#FF0000">** Scheduler core ** </font></i>
 * This is the main active object of the scheduler implementation,
 * it communicates with the entity manager to acquire nodes and with a policy
 * to insert and get jobs from the queue.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 27, 2007
 * @since ProActive 3.2
 */
public class SchedulerCore implements SchedulerCoreInterface, RunActive {
	
	/** serial version UID */
	private static final long serialVersionUID = 1581139478784832488L;
	/** Scheduler logger */
	public static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);
	public static final String LOGGER_PREFIX = "logger.scheduler.";
	public static final Integer CONNECTION_DEFAULT_PORT = 1337;
	public static String SERIALIZE_PATH = "/tmp/";
	private static Integer port = CONNECTION_DEFAULT_PORT;
	private static String host = null;
	/** scheduler main loop time out */	
	private static final int SCHEDULER_TIME_OUT = 2000;
	/** Implementation of Infrastructure Manager */
	private InfrastructureManagerProxy resourceManager;
	/** Scheduler frontend. */
	private SchedulerFrontend frontend;
	/** Scheduler current policy */
	private PolicyInterface policy;
	/** list of all jobs managed by the scheduler */
	private HashMap<JobId,Job> jobs = new HashMap<JobId,Job>();
	/** list of pending jobs among the managed jobs */
	private Vector<Job> pendingJobs = new Vector<Job>();
	/** list of running jobs among the managed jobs */
	private Vector<Job> runningJobs = new Vector<Job>();
	/** list of finished jobs among the managed jobs */
	private Vector<Job> finishedJobs = new Vector<Job>();
	/** associated map between TaskId and and taskResult */
	private HashMap<TaskId,TaskResult> taskResults = new HashMap<TaskId,TaskResult>();
	/** Job result storage */
	private HashMap<JobId,JobResult> results = new HashMap<JobId,JobResult>();
	/** Scheduler current state */
	private SchedulerState state = SchedulerState.STOPPED;
	
	
	/**
	 * Pro Active empty constructor
	 */
	public SchedulerCore(){}
	
	
	/**
	 * Create a new scheduler Core with the given resources manager.
	 * 
	 * @param imp the resource manager on which the scheduler will interact.
	 */
	public SchedulerCore(InfrastructureManagerProxy imp, SchedulerFrontend frontend, String policyFullName) {
		try {
			this.resourceManager = imp;
			this.frontend = frontend;
			//logger
	    	host = java.net.InetAddress.getLocalHost().getHostName();
	        SimpleLoggerServer slf = new SimpleLoggerServer(port);
	        Thread slft  = new Thread(slf);
	        slft.start();
			this.policy = (PolicyInterface)Class.forName(policyFullName).newInstance();
			logger.info("Scheduler Core ready !");
		} catch (InstantiationException e) {
			logger.error("The policy class cannot be found : " + e.getMessage());
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			 logger.error("The method cannot be accessed " + e.getMessage());
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			logger.error("The class definition cannot be found, it might be due to case sentivity : " + e.getMessage());
			throw new RuntimeException(e);
		} catch (UnknownHostException e) {
			logger.error("Unknown host in host creation : " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.RunActive#runActivity(org.objectweb.proactive.Body)
	 */
	public void runActivity(Body body) {
		ProActive.setImmediateService("listenLog");
		Service service = new Service(body);
		//set the filter for serveAll method
		RequestFilter filter = new MainLoopRequestFilter("submit","pause","terminate");
		do {
			service.blockingServeOldest();
			while(state == SchedulerState.STARTED || state == SchedulerState.PAUSED){
				service.serveAll(filter);
				schedule();
	            //block the loop until a method is invoked and serve it
				service.blockingServeOldest(SCHEDULER_TIME_OUT);
			}
		} while (state != SchedulerState.SHUTTING_DOWN && state != SchedulerState.KILLED);
		logger.info("Scheduler is shutting down...");
		//FIXME for the moment if shutdown sequence is enable, paused job becomes running
		for (Job job : jobs.values()){
			if (job.getState() == JobState.PAUSED){
				job.setUnPause();
				JobEvent event = job.getJobInfo();
				frontend.jobResumedEvent(event);
				event.setTaskStatusModify(null);
			}
		}
		//terminating jobs...
		logger.info("Terminating jobs...");
		while(runningJobs.size() + pendingJobs.size() > 0){
			service.serveAll("terminate");
			schedule();
            //block the loop until a method is invoked and serve it
			service.blockingServeOldest(SCHEDULER_TIME_OUT);
		}
		//if normal shutdown, serialize
		if (state == SchedulerState.SHUTTING_DOWN){
			try {
				logger.info("Serializing results...");
				Serializer.serializeResults(results,SERIALIZE_PATH);
			} catch (Exception e) {
				e.printStackTrace();
				//if an error occurs during serialization, we wait until the result to be got back
				while (results.size() > 0 && state == SchedulerState.SHUTTING_DOWN){
					service.blockingServeOldest();
				}
			}
		}
		logger.info("Terminating...");
		//shutdown resource manager proxy
		resourceManager.shutdownProxy();
		logger.info("Scheduler is now shutdown !");
		frontend.schedulerShutDownEvent();
		//destroying scheduler active objects
		frontend.terminate();
		ProActive.terminateActiveObject(true);
		//exit
		System.exit(0);
	}

	
	private void schedule() {
		//get light job list with eligible jobs (running and pending)
		ArrayList<LightJob> LightJobList = new ArrayList<LightJob>();
		for (Job j : runningJobs){
			LightJobList.add(j.getLightJob());
		}
		//if scheduler is paused it only finishes running jobs
		if (state != SchedulerState.PAUSED){
			for (Job j : pendingJobs){
				LightJobList.add(j.getLightJob());
			}
		}
		//ask the policy all the tasks to be schedule according to the jobs list.
		Vector<? extends LightTask> taskRetrivedFromPolicy = policy.getOrderedTasks(LightJobList);
		while (!taskRetrivedFromPolicy.isEmpty() && resourceManager.hasFreeResources().booleanValue()){
			LightTask lightTask = taskRetrivedFromPolicy.remove(0);
			Job currentJob = jobs.get(lightTask.getJobId());
			TaskDescriptor taskDescriptor = currentJob.getHMTasks().get(lightTask.getId());
			//TODO améliorer la demande de noeuds
			//il faut associer les scripts aux demandes histoire qu'on execute bien
			//la bonne tache sur le bon noeud d'execution.
			NodeSet nodeSet = resourceManager.getAtMostNodes(taskDescriptor.getNumberOfNodesNeeded(), taskDescriptor.getVerifyingScript());
			try {
				while (nodeSet.size() > 0){
					Node node = nodeSet.remove(0);
					//if the job is an application job and if all nodes can be launched at the same time
					if (currentJob.getType() == JobType.APPLI && nodeSet.size() >= (taskDescriptor.getNumberOfNodesNeeded()-1)){
						TaskLauncher launcher = taskDescriptor.createLauncher(host, port, node);
						ArrayList<Node> nodes = new ArrayList<Node>();
						for (int i=0;i<(taskDescriptor.getNumberOfNodesNeeded()-1);i++){
							nodes.add(nodeSet.remove(0));
						}
						taskResults.put(taskDescriptor.getId(),((AppliTaskLauncher)launcher).doTask((SchedulerCore)ProActive.getStubOnThis(),(ApplicationTask)taskDescriptor.getTask(),nodes));
					} else {
						TaskLauncher launcher = taskDescriptor.createLauncher(host, port, node);
						//if job is TASKSFLOW, preparing the list of parameters for this task.
						int resultSize = lightTask.getParents().size();
						if (currentJob.getType() == JobType.TASKSFLOW && resultSize > 0){
							TaskResult[] params = new TaskResult[resultSize];
							for (int i=0;i<resultSize;i++){
								params[i] = taskResults.get(lightTask.getParents().get(i).getId());
							}
							taskResults.put(taskDescriptor.getId(),launcher.doTask((SchedulerCore)ProActive.getStubOnThis(),taskDescriptor.getTask(),params));
						} else {
							taskResults.put(taskDescriptor.getId(),launcher.doTask((SchedulerCore)ProActive.getStubOnThis(),taskDescriptor.getTask()));
						}
					}
					logger.info(">>>>>>>> New task started on "+node.getNodeInformation().getHostName()+" [ "+taskDescriptor.getId()+" ]");
					// set the different informations on job
					if (currentJob.getStartTime() == -1){
						// if it is the first task of this job
						currentJob.start();
						pendingJobs.remove(currentJob);
						runningJobs.add(currentJob);
						// send job event to frontend
						frontend.pendingToRunningJobEvent(currentJob.getJobInfo());
						// don't forget to set the task status modify to null after a Job.start() method;
						currentJob.setTaskStatusModify(null);
					}
					// set the different informations on task
					currentJob.startTask(taskDescriptor,node.getNodeInformation().getHostName());
					// send task event to frontend
					frontend.pendingToRunningTaskEvent(taskDescriptor.getTaskInfo());
				}
			} catch (Exception e) {
				// TODO qué fa ? rendre le noeud et reessayer avec un autre.
				// ne pas oublier de sauver dans une liste les tache qui ont merdé et les relancer
				//autant qu'il le faut
				e.printStackTrace();
			}
		}
		
	}
	

	/**
	 * Invoke by a task when it is about to finish.
	 * This method can be invoke just a little amount of time before the result arrival.
	 * That's why it can block the execution but only for short time.
	 * 
	 * @param node the node on which the task has been executed.
	 * @param taskId the identification of the executed task.
	 * @param jobId the executed task's job identification. 
	 */
	public void terminate(TaskId taskId, JobId jobId){
		try {
			logger.info("<<<<<<<< Terminated task on job "+jobId+" [ "+taskId+" ]");
			//The task is terminated but it's possible to have to
			//wait for the futur of the task result (TaskResult).
			//accessing to the taskResult could block current execution but for a little time.
			//it is the time between the end of the task and the arrival of the futur from the task.
			Job job = jobs.get(jobId);
			TaskDescriptor descriptor = job.terminateTask(taskId);
			frontend.runningToFinishedTaskEvent(descriptor.getTaskInfo());
			//store this result if the job is PARAMETER_SWIPPING or APPLI or if it is a final task.
			if (job.getType() != JobType.TASKSFLOW || descriptor.isFinalTask()){
				TaskResult res = taskResults.get(taskId);
				results.get(job.getId()).addTaskResult(descriptor.getName(),res);
			}
			//if this job is finished (every task are finished)
			if (job.getNumberOfFinishedTask() == job.getTotalNumberOfTasks()){
				//deleting all task results
				for (TaskDescriptor td : job.getTasks()){
					taskResults.remove(td.getId());
				}
				//terminating job
				job.terminate();
				runningJobs.remove(job);
				finishedJobs.add(job);
				logger.info("<<<<<<<<<<<<<<<<<<< Terminated job "+jobId);
				frontend.runningToFinishedJobEvent(job.getJobInfo());
			}
			//free execution node
			if (job.getType() != JobType.APPLI){
				resourceManager.freeNode(descriptor.getLauncher().getNode(),descriptor.getPostTask());
			} else {
				resourceManager.freeNodes(new NodeSet(((AppliTaskLauncher)descriptor.getLauncher()).getNodes()),descriptor.getPostTask());
			}
		} catch (NodeException e) {
			e.printStackTrace();
		} catch (NullPointerException eNull){
			//the task has been killed. Nothing to do anymore with this one.
		}
	}
	
	
	/**
	 * Submit a new job to the scheduler.
	 * 
	 * @param job the job to be scheduled.
	 * @throws SchedulerException
	 */
	public void submit(Job job) throws SchedulerException {
		if (state == SchedulerState.SHUTTING_DOWN || state == SchedulerState.STOPPED)
			throw new SchedulerException("Scheduler is stopped, cannot submit new job !");
		job.submit();
		jobs.put(job.getId(), job);
		pendingJobs.add(job);
		//creating job result storage
		JobResult jobResult = new JobResult(job.getId(),job.getName());
		//store the job result until user get it
		results.put(job.getId(),jobResult);
		//sending event to client
		frontend.newPendingJobEvent(job);
		logger.info("New job added containing "+job.getTotalNumberOfTasks()+" tasks !");
	}
	

	/**
	 * Return the scheduler current state with the pending, running, finished jobs list.
	 * 
	 * @return the scheduler current state with the pending, running, finished jobs list.
	 */
	public SchedulerInitialState getSchedulerInitialState() {
		SchedulerInitialState sState = new SchedulerInitialState();
		sState.setPendingJobs(pendingJobs);
		sState.setRunningJobs(runningJobs);
		sState.setFinishedJobs(finishedJobs);
		sState.setState(state);
		return sState;
	}
	
	
	/**
	 * Listen for the tasks user log.
	 * 
	 * @param jobId the id of the job to listen to.
	 * @param hostname the hostname where to send the log.
	 * @param port the port number on which the log will be sent.
	 */
	public void listenLog(JobId jobId, String hostname, int port) {
		Logger.getLogger(LOGGER_PREFIX+jobId).addAppender(new SocketAppender(hostname,port));
	}
	
	
	/**
	 * To get the results.
	 * 
	 * @return the results.
	 */
	public JobResult getResults(JobId jobId) {
		JobResult result = results.remove(jobId);
		if (result != null) {
			Job job = jobs.get(jobId);
			job.setRemovedTime(System.currentTimeMillis());
			finishedJobs.remove(job);
			frontend.removeFinishedJobEvent(job.getJobInfo());
			logger.info("Removed result for job "+jobId);
		}
		return result;
	}
	

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#start()
	 */
	public BooleanWrapper coreStart() {
		if (state != SchedulerState.STOPPED)
			return new BooleanWrapper(false);
		state = SchedulerState.STARTED;
		logger.info("Scheduler has just been started !");
		frontend.schedulerStartedEvent();
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#stop()
	 */
	public BooleanWrapper coreStop() {
		if (state == SchedulerState.STOPPED || state == SchedulerState.SHUTTING_DOWN || state == SchedulerState.KILLED)
			return new BooleanWrapper(false);
		state = SchedulerState.STOPPED;
		logger.info("Scheduler has just been stopped, no tasks will be launched until start.");
		frontend.schedulerStoppedEvent();
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#pause()
	 */
	public BooleanWrapper corePause() {
		if (state == SchedulerState.SHUTTING_DOWN || state == SchedulerState.KILLED){
			return new BooleanWrapper(false);
		}
		if (state != SchedulerState.PAUSED_IMMEDIATE && state != SchedulerState.STARTED)
			return new BooleanWrapper(false);
		state = SchedulerState.PAUSED;
		logger.info("Scheduler has just been paused !");
		frontend.schedulerPausedEvent();
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#coreImmediatePause()
	 */
	public BooleanWrapper coreImmediatePause() {
		if (state == SchedulerState.SHUTTING_DOWN || state == SchedulerState.KILLED){
			return new BooleanWrapper(false);
		}
		if (state != SchedulerState.PAUSED && state != SchedulerState.STARTED)
			return new BooleanWrapper(false);
		state = SchedulerState.PAUSED_IMMEDIATE;
		logger.info("Scheduler has just been immediate paused !");
		frontend.schedulerImmediatePausedEvent();
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#resume()
	 */
	public BooleanWrapper coreResume() {
		if (state == SchedulerState.SHUTTING_DOWN || state == SchedulerState.KILLED){
			return new BooleanWrapper(false);
		}
		if (state != SchedulerState.PAUSED && state != SchedulerState.PAUSED_IMMEDIATE && state != SchedulerState.STARTED)
			return new BooleanWrapper(false);
		state = SchedulerState.STARTED;
		logger.info("Scheduler has just been resumed !");
		frontend.schedulerResumedEvent();
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#shutdown()
	 */
	public BooleanWrapper coreShutdown() {
		//TODO si le scheduler est shutting down et qu'un job est en pause que fait on ?
		//actuellement le job reste en pause et le scheduler s'arrete que lorsque tous les jobs sont finis.
		//Le user ne peut pas non plus résumer son job, donc tout reste bloqué.
		if (state == SchedulerState.KILLED || state == SchedulerState.SHUTTING_DOWN)
			return new BooleanWrapper(false);
		state = SchedulerState.SHUTTING_DOWN;
		logger.info("Scheduler is shutting down, this make take time to finish every jobs !");
		frontend.schedulerShuttingDownEvent();
		return new BooleanWrapper(true);
	}
	

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#coreKill()
	 */
	public synchronized BooleanWrapper coreKill() {
		if (state == SchedulerState.KILLED)
			return new BooleanWrapper(false);
		//destroying running active object launcher
		for (Job j : runningJobs){
			for (TaskDescriptor td : j.getTasks()){
				try {
					td.getLauncher().terminate();
				} catch(Exception e) {}
			}
		}
		//cleaning all lists
		jobs.clear();
		pendingJobs.clear();
		runningJobs.clear();
		finishedJobs.clear();
		taskResults.clear();
		results.clear();
		//finally : shutdown
		state = SchedulerState.KILLED;
		logger.info("Scheduler has just been killed !");
		frontend.schedulerKilledEvent();
		return new BooleanWrapper(true);
	}


	/**
	 * Pause the job represented by jobId.
	 * This method will finish every running tasks of this job, and then pause the job.
	 * The job will have to be resumed in order to finish.
	 * 
	 * @param jobId the job to pause.
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper pause(JobId jobId) {
		if (state == SchedulerState.SHUTTING_DOWN || state == SchedulerState.KILLED){
			return new BooleanWrapper(false);
		}
		Job job = jobs.get(jobId);
		if (finishedJobs.contains(job)){
			return new BooleanWrapper(false);
		}
		boolean change = job.setPaused();
		JobEvent event = job.getJobInfo();
		if (change)
			logger.info("job "+jobId+" has just been paused !");
		frontend.jobPausedEvent(event);
		event.setTaskStatusModify(null);
		return new BooleanWrapper(change);
	}


	/**
	 * Resume the job represented by jobId.
	 * This method will restart every tasks of this job.
	 * 
	 * @param jobId the job to resume.
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper resume(JobId jobId) {
		if (state == SchedulerState.SHUTTING_DOWN || state == SchedulerState.KILLED){
			return new BooleanWrapper(false);
		}
		Job job = jobs.get(jobId);
		if (finishedJobs.contains(job)){
			return new BooleanWrapper(false);
		}
		boolean change = job.setUnPause();
		JobEvent event = job.getJobInfo();
		if (change)
			logger.info("job "+jobId+" has just been resumed !");
		frontend.jobResumedEvent(event);
		event.setTaskStatusModify(null);
		return new BooleanWrapper(change);
	}

	
	/**
	 * kill the job represented by jobId.
	 * This method will kill every running tasks of this job, and remove it from the scheduler.
	 * The job won't be terminated, it won't have result.
	 * 
	 * @param jobId the job to kill.
	 * @return true if success, false otherwise.
	 */
	public BooleanWrapper kill(JobId jobId) {
		if (state == SchedulerState.SHUTTING_DOWN || state == SchedulerState.KILLED){
			return new BooleanWrapper(false);
		}
		Job job = jobs.get(jobId);
		jobs.remove(jobId);
		for (TaskDescriptor td : job.getTasks()){
			if (td.getStatus() == Status.RUNNNING){
				//free execution node
				try{
					td.getLauncher().terminate();
				} catch (Exception e){}
				try{
					if (job.getType() != JobType.APPLI){
						resourceManager.freeNode(td.getLauncher().getNode(),td.getPostTask());
					} else {
						resourceManager.freeNodes(new NodeSet(((AppliTaskLauncher)td.getLauncher()).getNodes()),td.getPostTask());
					}
				} catch (NodeException e){
					e.printStackTrace();
				}
				taskResults.remove(td.getId());
			}
		}
		if (runningJobs.remove(job) || pendingJobs.remove(job) || finishedJobs.remove(job));
		results.remove(jobId);
		logger.info("job "+jobId+" has just been killed !");
		frontend.jobKilledEvent(jobId);
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * Change the priority of the job represented by jobId.
	 * 
	 * @param jobId the job on whitch to change the priority.
	 * @throws SchedulerException (can be due to insufficient permission)
	 */
	public void changePriority(JobId jobId, JobPriority priority) {
		Job job = jobs.get(jobId);
		job.setPriority(priority);
		frontend.changeJobPriorityEvent(job.getJobInfo());
	}

}
