package org.objectweb.proactive.extra.scheduler.core;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.scheduler.exception.AdminSchedulerException;
import org.objectweb.proactive.extra.scheduler.exception.NFEHandler;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.resourcemanager.InfrastructureManagerProxy;


/**
 * Scheduler Admin interface.
 * This class provides method to managed jobs for an administrator.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 28, 2007
 * @since ProActive 3.2
 */
public class AdminScheduler extends UserScheduler implements SchedulerInterface {
	
	/** serial version UID */
	private static final long serialVersionUID = -8799427055681878266L;
	/** Logger to be used for all messages related to the scheduler */
    public static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);
	
    
    
	/**
	 * Create a new scheduler at the specified URL plugged on the given resource manager.
	 * 
	 * @param imp the resource manager to plug on the scheduler.
	 * @return an admin scheduler interface to manage the scheduler.
	 */
	public static AdminScheduler createScheduler(InfrastructureManagerProxy imp, String policyFullClassName) throws AdminSchedulerException {
		logger.info("********************* STARTING NEW SCHEDULER *******************");
		//verifying arguments...
		if (imp == null)
			throw new AdminSchedulerException("The Entity manager must be set !");
		//verifying that the scheduler is an active object
		try {
			ProActive.getActiveObjectNodeUrl(imp);
		} catch (ProActiveRuntimeException e) {
			logger.warn("The infrastructure manager is not an active object, this will decrease the scheduler performance.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AdminSchedulerException("An error has occured trying to access the entity manager " + e.getMessage());
		}
		//creating admin API and scheduler
		AdminScheduler adminScheduler = new AdminScheduler();
		SchedulerFrontend schedulerFrontend;
		try {
			// creating the scheduler proxy.
			// if this fails then it will not continue.
			logger.info("Creating scheduler frontend...");
			schedulerFrontend = (SchedulerFrontend) ProActive.newActive(
					SchedulerFrontend.class.getName(),
					new Object[] {imp, policyFullClassName});
			// adding NFE listener to managed non fonctionnal exception
			// that occurs in Proactive Core
			ProActive.addNFEListenerOnAO(schedulerFrontend, new NFEHandler("Scheduler Frontend"));
			// registering the scheduler proxy at the given URL
			logger.info("Registering scheduler frontend...");
			String schedulerUrl = "//localhost/"+SchedulerFrontend.SCHEDULER_DEFAULT_NAME;
			ProActive.register(schedulerFrontend, schedulerUrl);
			// setting the proxy to the admin scheduler API
			adminScheduler.schedulerFrontend = schedulerFrontend;
			// run forest run !!
			logger.info("Scheduler Created on " + schedulerUrl);
			logger.info("Scheduler is now ready to be started !");
		} catch (Exception e) {
			e.printStackTrace();
			throw new AdminSchedulerException(e.getMessage());
		}
		return adminScheduler;
	}
	
	
	/**
	 * Connect the admin interface to a scheduler with the given SNode.
	 * This method will return a new scheduler admin API to managed job.
	 * 
	 * @param schedulerURL the url of an already started scheduler.
	 * @return an adminScheduler interface to managed the scheduler as user right
	 * @throws SchedulerException
	 */
	public static AdminScheduler connectTo(String schedulerURL) throws SchedulerException {
		AdminScheduler as = new AdminScheduler();
		
		logger.info("******************* JOINING EXISTING SCHEDULER *****************");
		
		if (schedulerURL == null){
			logger.info("Scheduler URL was null, looking for scheduler on localhost...");
			schedulerURL = "//localhost/"+SchedulerFrontend.SCHEDULER_DEFAULT_NAME;
		}
		SchedulerFrontend sp = null;
    	try {
			sp = (SchedulerFrontend)(ProActive.lookupActive(SchedulerFrontend.class.getName(), schedulerURL));
		} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
			throw new SchedulerException("Error while looking up this active object : "+SchedulerFrontend.class.getName());
		} catch (IOException e) {
			e.printStackTrace();
			throw new SchedulerException("IO Error while looking up this active object : "+SchedulerFrontend.class.getName());
		}
		
		as.schedulerFrontend = sp;
		return as;
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerInterface#getResult(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	public JobResult getResult(JobId jobId) throws SchedulerException {
		return schedulerFrontend.getResult(jobId);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerInterface#listenLog(org.objectweb.proactive.extra.scheduler.job.JobId, java.lang.String, int)
	 */
	public void listenLog(JobId jobId, String hostname, int port) {
		schedulerFrontend.listenLog(jobId, hostname, port);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerInterface#submit(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	public JobId submit(Job job) throws SchedulerException {
		return schedulerFrontend.submit(job);
	}
	
	
	/**
	 * Start the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean start(){
		return schedulerFrontend.start().booleanValue();
	}
	
	
	/**
	 * Stop the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean stop(){
		return schedulerFrontend.stop().booleanValue();
	}
	
	
	/**
	 * Pause the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean pause(){
		return schedulerFrontend.pause().booleanValue();
	}
	
	
	/**
	 * Resume the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean resume(){
		return schedulerFrontend.resume().booleanValue();
	}
	
	
	/**
	 * Shutdown the scheduler.
	 * 
	 * @return true if success, false otherwise.
	 */
	public boolean shutdown(){
		return schedulerFrontend.shutdown().booleanValue();
	}
	
}
