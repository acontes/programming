package org.objectweb.proactive.extra.scheduler.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;
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
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.job.LightJob;
import org.objectweb.proactive.extra.scheduler.policy.PolicyInterface;
import org.objectweb.proactive.extra.scheduler.resourcemanager.InfrastructureManagerProxy;
import org.objectweb.proactive.extra.scheduler.task.LightTask;
import org.objectweb.proactive.extra.scheduler.task.Status;
import org.objectweb.proactive.extra.scheduler.task.TaskDescriptor;
import org.objectweb.proactive.extra.scheduler.task.TaskId;
import org.objectweb.proactive.extra.scheduler.task.TaskLauncher;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerState;
import org.objectweb.proactive.extra.scheduler.userAPI.State;

/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i>
 * Scheduler core. This is the main active object of the scheduler implementation,
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
	private State state = State.STOPPED;
	
	
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
		RequestFilter filter = new MainLoopRequestFilter("submit","terminate");
		do {
			service.blockingServeOldest();
			while(state == State.STARTED || state == State.PAUSED){
				service.serveAll(filter);
				schedule();
	            //block the loop until a method is invoked and serve it
				service.blockingServeOldest(SCHEDULER_TIME_OUT);
			}
		} while (state != State.SHUTTING_DOWN && state != State.KILLED);
		logger.info("Scheduler is shutting down...");
		//terminating jobs...
		logger.info("Terminating jobs...");
		while(runningJobs.size() + pendingJobs.size() > 0){
			service.serveAll("terminate");
			schedule();
            //block the loop until a method is invoked and serve it
			service.blockingServeOldest(SCHEDULER_TIME_OUT);
		}
		//if normal shutdown, serialize
		if (state == State.SHUTTING_DOWN){
			try {
				logger.info("Serializing results...");
				serialyseResults(SERIALIZE_PATH);
			} catch (Exception e) {
				e.printStackTrace();
				while (results.size() > 0 && state == State.SHUTTING_DOWN){
					service.blockingServeOldest();
				}
			}
		}
		logger.info("Terminating...");
		//shutdown resource manager proxy
		resourceManager.shutdownProxy();
		logger.info("Scheduler is now shutdown !");
		frontend.SchedulerShutDownEvent();
		//destroying scheduler active objects
		frontend.terminate();
		ProActive.terminateActiveObject(frontend,true);
		ProActive.terminateActiveObject(true);
		//exit
		System.exit(0);
	}

	
	private void schedule() {
		// TODO en cours
		// TODO réfléchir comment passer les dépendances en lecture seule et
		// les tâches élligibles dans une liste.
		// build light job list with eligible jobs (running and pending)
		ArrayList<LightJob> LightJobList = new ArrayList<LightJob>();
		for (Job j : runningJobs){
			LightJobList.add(new LightJob(j));
		}
		for (Job j : pendingJobs){
			LightJobList.add(new LightJob(j));
		}
		//ask the policy all the tasks to be schedule according to the jobs list.
		Vector<LightTask> taskRetrivedFromPolicy = policy.getReadyTasks(LightJobList);
		//TODO attention la liste (vector) dans un light job est modifiable
		//et je me base la dessus pour maintenir les dépendances
		//schedule the tasks
		while (!taskRetrivedFromPolicy.isEmpty() && resourceManager.hasFreeResources().booleanValue()){
			LightTask lightTask = taskRetrivedFromPolicy.remove(0);
			Job currentJob = jobs.get(lightTask.getJobId());
			TaskDescriptor taskDescriptor = currentJob.getHMTasks().get(lightTask.getId());
			//TODO améliorer la demande de noeuds
			NodeSet nodeSet = resourceManager.getAtMostNodes(1, taskDescriptor.getVerifyingScript());
			for (Node node : nodeSet){
				try {
					TaskLauncher launcher;
					if (taskDescriptor.getPreTask() == null){
						launcher = (TaskLauncher)ProActive.newActive(TaskLauncher.class.getName(), new Object[]{taskDescriptor.getId(),taskDescriptor.getJobId(), host, port}, node);
					} else {
						launcher = (TaskLauncher)ProActive.newActive(TaskLauncher.class.getName(), new Object[]{taskDescriptor.getId(),taskDescriptor.getJobId(),taskDescriptor.getPreTask(), host, port}, node);
					}
					taskDescriptor.setRunningTask(launcher);
					//TODO gérer les résultats à faire suivre dans le cas d'un task flow
					taskResults.put(taskDescriptor.getId(),launcher.doTask((SchedulerCore)ProActive.getStubOnThis(),taskDescriptor.getTask()));
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
					currentJob.setNumberOfPendingTasks(currentJob.getNumberOfPendingTask()-1);
					currentJob.setNumberOfRunningTasks(currentJob.getNumberOfRunningTask()+1);
					// set the different informations on task
					taskDescriptor.setStatus(Status.RUNNNING);
					taskDescriptor.setStartTime(System.currentTimeMillis());
					taskDescriptor.setExecutionHostName(node.getNodeInformation().getHostName());
					// send task event to frontend
					frontend.pendingToRunningTaskEvent(taskDescriptor.getTaskInfo());
				} catch (Exception e) {
					// TODO qué fa ? rendre le noeud et reessayer avec un autre.
					e.printStackTrace();
				}
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
		logger.info("<<<<<<<< Terminated task on job "+jobId+" [ "+taskId+" ]");
		//The task is terminated but it's possible to have to
		//wait for the futur of the task result (TaskResult).
		Job job = jobs.get(jobId);
		TaskDescriptor descriptor = job.terminateTask(taskId);
		frontend.runningToFinishedTaskEvent(descriptor.getTaskInfo());
		//TODO si y'a pas de tache finale, terminer quand toutes les taches sont finies
		if (job.getFinalTask().equals(descriptor)){
			//job is finished, creating jobResult
			//accessing to the taskResult could block current execution but for a little time.
			TaskResult res = taskResults.get(taskId);
			JobResult jobResult = new JobResult(jobId,res.value(),res.getException());
			//store the job result until user get it
			results.put(jobId,jobResult);
			//TODO être sur que toutes les listes sont vides lors de la terminaison d'un job
			taskResults.remove(taskId);
			job.setFinishedTime(System.currentTimeMillis());
			runningJobs.remove(job);
			finishedJobs.add(job);
			logger.info("<<<<<<<<<<<<<<<<<<< Terminated job "+jobId);
			frontend.runningToFinishedJobEvent(job.getJobInfo());
		}
		//free execution node
		try {
			resourceManager.freeNode(descriptor.getRunningTask().getNode());
		} catch (NodeException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Submit a new job to the scheduler.
	 * 
	 * @param job the job to be scheduled.
	 * @throws SchedulerException
	 */
	public void submit(Job job) throws SchedulerException {
		if (state == State.SHUTTING_DOWN || state == State.STOPPED)
			throw new SchedulerException("Scheduler is stopped, cannot submit new job !");
		job.setSubmittedTime(System.currentTimeMillis());
		jobs.put(job.getId(), job);
		pendingJobs.add(job);
		frontend.newPendingJobEvent(job);
		logger.info("New job added containing "+job.getTotalNumberOfTasks()+" tasks !");
	}
	

	/**
	 * Return the scheduler current state with the pending, running, finished jobs list.
	 * 
	 * @return the scheduler current state with the pending, running, finished jobs list.
	 */
	public SchedulerState getSchedulerState() {
		SchedulerState sState = new SchedulerState();
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
		if (state == State.SHUTTING_DOWN || state == State.KILLED){
			return new BooleanWrapper(false);
		}
		if (state != State.STOPPED)
			return new BooleanWrapper(false);
		state = State.STARTED;
		logger.info("Scheduler has just been started !");
		frontend.SchedulerStartedEvent();
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#stop()
	 */
	public BooleanWrapper coreStop() {
		if (state == State.SHUTTING_DOWN || state == State.KILLED){
			return new BooleanWrapper(false);
		}
		if (state == State.STOPPED)
			return new BooleanWrapper(false);
		state = State.STOPPED;
		logger.info("Scheduler has just been stopped, it will finish every submitted jobs...");
		frontend.SchedulerStoppedEvent();
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#pause()
	 */
	public BooleanWrapper corePause() {
		if (state == State.SHUTTING_DOWN || state == State.KILLED){
			return new BooleanWrapper(false);
		}
		if (state != State.PAUSED_IMMEDIATE && state != State.STARTED)
			return new BooleanWrapper(false);
		//job info preparation for listener
		HashMap<JobId,JobEvent> statusList = new HashMap<JobId,JobEvent>();
		for (Job j : pendingJobs){
			j.setPaused();
			statusList.put(j.getId(), j.getJobInfo());
		}
		state = State.PAUSED;
		logger.info("Scheduler has just been paused !");
		frontend.SchedulerPausedEvent(new SchedulerEvent(statusList));
		//job info cleaning
		for (Job j : pendingJobs){
			j.setTaskStatusModify(null);
		}
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#coreImmediatePause()
	 */
	public BooleanWrapper coreImmediatePause() {
		if (state == State.SHUTTING_DOWN || state == State.KILLED){
			return new BooleanWrapper(false);
		}
		if (state != State.PAUSED && state != State.STARTED)
			return new BooleanWrapper(false);
		//job info preparation for listener
		HashMap<JobId,JobEvent> statusList = new HashMap<JobId,JobEvent>();
		for (Job j : pendingJobs){
			j.setPaused();
			statusList.put(j.getId(), j.getJobInfo());
		}
		for (Job j : runningJobs){
			j.setPaused();
			statusList.put(j.getId(), j.getJobInfo());
		}
		state = State.PAUSED_IMMEDIATE;
		logger.info("Scheduler has just been immediate paused !");
		frontend.SchedulerImmediatePausedEvent(new SchedulerEvent(statusList));
		//job info cleaning
		for (Job j : pendingJobs){
			j.setTaskStatusModify(null);
		}
		for (Job j : runningJobs){
			j.setTaskStatusModify(null);
		}
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#resume()
	 */
	public BooleanWrapper coreResume() {
		if (state == State.SHUTTING_DOWN || state == State.KILLED){
			return new BooleanWrapper(false);
		}
		if (state != State.PAUSED && state != State.PAUSED_IMMEDIATE && state != State.STARTED)
			return new BooleanWrapper(false);
		//job info preparation for listener
		HashMap<JobId,JobEvent> statusList = new HashMap<JobId,JobEvent>();
		for (Job j : pendingJobs){
			j.setUnPause();
			statusList.put(j.getId(), j.getJobInfo());
		}
		if (state == State.PAUSED_IMMEDIATE){
			for (Job j : runningJobs){
				j.setUnPause();
				statusList.put(j.getId(), j.getJobInfo());
			}
		}
		state = State.STARTED;
		logger.info("Scheduler has just been resumed !");
		frontend.SchedulerResumedEvent(new SchedulerEvent(statusList));
		//job info cleaning
		for (Job j : pendingJobs){
			j.setTaskStatusModify(null);
		}
		if (state == State.PAUSED_IMMEDIATE){
			for (Job j : runningJobs){
				j.setTaskStatusModify(null);
			}
		}
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#shutdown()
	 */
	public BooleanWrapper coreShutdown() {
		if (state == State.KILLED || state == State.SHUTTING_DOWN)
			return new BooleanWrapper(false);
		state = State.SHUTTING_DOWN;
		logger.info("Scheduler is shutting down, this make take time to finish every jobs !");
		frontend.SchedulerShuttingDownEvent();
		return new BooleanWrapper(true);
	}
	

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#coreKill()
	 */
	public synchronized BooleanWrapper coreKill() {
		if (state == State.KILLED)
			return new BooleanWrapper(false);
		//destroying running active object launcher
		for (Job j : runningJobs){
			for (TaskDescriptor td : j.getTasks()){
				try {
					ProActive.terminateActiveObject(td.getRunningTask(),true);
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
		state = State.KILLED;
		logger.info("Scheduler has just been killed !");
		frontend.SchedulerkilledEvent();
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
		if (state == State.SHUTTING_DOWN || state == State.KILLED){
			return new BooleanWrapper(false);
		}
		boolean change = jobs.get(jobId).setPaused();
		JobEvent event = jobs.get(jobId).getJobInfo();
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
		if (state == State.SHUTTING_DOWN || state == State.KILLED){
			return new BooleanWrapper(false);
		}
		boolean change = jobs.get(jobId).setUnPause();
		JobEvent event = jobs.get(jobId).getJobInfo();
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
		if (state == State.SHUTTING_DOWN || state == State.KILLED){
			return new BooleanWrapper(false);
		}
		Job job = jobs.get(jobId);
		for (TaskDescriptor td : job.getTasks()){
			if (td.getRunningTask() != null){
				ProActive.terminateActiveObject(td.getRunningTask(), true);
				taskResults.remove(td.getId());
			}
		}
		if (runningJobs.remove(job) || pendingJobs.remove(job) || finishedJobs.remove(job));
		jobs.remove(jobId);
		results.remove(jobId);
		logger.info("job "+jobId+" has just been killed !");
		frontend.jobKilledEvent(jobId);
		return new BooleanWrapper(true);
	}

	
	/**
	 * Serialyse every jobs results on hard drive before shutting down.
	 * file will be named like that pattern : SCHED_{date}_{numJob}.result
	 * 
	 * @param path the path on which to saves the results.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void serialyseResults(String path) throws FileNotFoundException, IOException {
		for (Entry<JobId,JobResult> e : results.entrySet()){
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			String f = path+"SCHED_"+String.format("%1$tm-%1$te-%1$tY", calendar)+"_"+e.getKey().value()+".result";
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(f));
			objOut.writeObject(e.getValue());
			objOut.close();
		}
	}

}
