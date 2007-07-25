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
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.NodeSet;
import org.objectweb.proactive.extra.logforwarder.SimpleLoggerServer;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
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
	/** true if the scheduler is running */
	private boolean schedulerRunning = false;
	/** true if the scheduler is shutting down */
	private boolean schedulerShuttingDown = false;
	
	
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
		// TODO en cours
		ProActive.setImmediateService("listenLog");
		// TODO essayer de retirer le submit des immediate service
		Service service = new Service(body);
		//set the filter for serveAll method
		RequestFilter filter = new MainLoopRequestFilter("submit","terminate");
		do {
			service.blockingServeOldest();
			while(schedulerRunning){
				service.serveAll(filter);
				schedule();
	            //block the loop until a method is invoked and serve it
				service.blockingServeOldest(SCHEDULER_TIME_OUT);
			}
		} while (!schedulerShuttingDown);
		logger.info("Scheduler is shutting down...");
		//TODO finir les jobs restant suivant le mode d'arrêt
		//et ne plus accepter aucune requete.
		logger.info("Scheduler is now shutdown !");
	}

	
	private void schedule() {
		// TODO en cours
		// TODO réfléchir comment passer les dépendances en lecture seule et
		// les tâches élligibles dans une liste.
		// build light job list with eligible jobs (running and pending)
		ArrayList<LightJob> LightJobList = new ArrayList<LightJob>();
		for (int i=0;i<runningJobs.size();i++){
			LightJobList.add(new LightJob(runningJobs.get(i)));
		}
		for (int i=0;i<pendingJobs.size();i++){
			LightJobList.add(new LightJob(pendingJobs.get(i)));
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
	public void terminate(Node node, TaskId taskId, JobId jobId){
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
		resourceManager.freeNode(node);
	}
	
	
	/**
	 * Submit a new job to the scheduler.
	 * 
	 * @param job the job to be scheduled.
	 * @throws SchedulerException
	 */
	public void submit(Job job) throws SchedulerException {
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
		SchedulerState state = new SchedulerState();
		state.setPendingJobs(pendingJobs);
		state.setRunningJobs(runningJobs);
		state.setFinishedJobs(finishedJobs);
		return state;
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
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#start()
	 */
	public BooleanWrapper coreStart() {
		if (schedulerRunning)
			return new BooleanWrapper(false);
		schedulerRunning = true;
		logger.info("Scheduler has just been started !");
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#stop()
	 */
	public BooleanWrapper coreStop() {
		if (!schedulerRunning)
			return new BooleanWrapper(false);
		schedulerRunning = false;
		logger.info("Scheduler has just been stopped, it will finish running tasks...");
		return new BooleanWrapper(true);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#pause()
	 */
	public BooleanWrapper corePause() {
		// TODO Auto-generated method stub
		return new BooleanWrapper(false);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#resume()
	 */
	public BooleanWrapper coreResume() {
		// TODO Auto-generated method stub
		return new BooleanWrapper(false);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerCoreInterface#shutdown()
	 */
	public BooleanWrapper coreShutdown() {
		if (schedulerRunning)
			return new BooleanWrapper(false);
		schedulerShuttingDown = true;
		logger.info("Scheduler is shutting down, this make take time to finish running jobs !");
		return new BooleanWrapper(true);
	}


	/**
	 * To get the schedulerRunning
	 * 
	 * @return the schedulerRunning
	 */
	public BooleanWrapper isSchedulerRunning() {
		return new BooleanWrapper(schedulerRunning);
	}


	/**
	 * To get the schedulerShuttingDown
	 * 
	 * @return the schedulerShuttingDown
	 */
	public BooleanWrapper isSchedulerShuttingDown() {
		return new BooleanWrapper(schedulerShuttingDown);
	}


	/**
	 * To get the results
	 * 
	 * @return the results
	 */
	public JobResult getResults(JobId jobId) {
		JobResult result = results.remove(jobId);
		if (result != null) {
			Job job = jobs.get(jobId);
			job.setRemovedTime(System.currentTimeMillis());
			finishedJobs.remove(job);
			frontend.removeFinishedJobEvent(job.getJobInfo());
		}
		return result;
	}
	
}
