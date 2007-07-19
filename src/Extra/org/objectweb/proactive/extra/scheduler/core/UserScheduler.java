/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.core;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.job.UserIdentification;

/**
 * Scheduler user interface.
 * This class provides method to managed jobs for a user.
 * 
 * @author ProActive Team
 * @version 1.0, Jun 12, 2007
 * @since ProActive 3.2
 */
public class UserScheduler implements UserSchedulerInterface {
	
	/** serial version UID */
	private static final long serialVersionUID = 3319322779771815630L;
	/** Scheduler logger */
	public static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);
	/** scheduler proxy as an active object */
	protected SchedulerFrontend schedulerFrontend;
	
	
	/**
	 * Connect the user interface to a scheduler with the given SNode.
	 * This method will return a new scheduler user API to managed job.
	 * 
	 * @param schedulerURL the url of an already started scheduler.
	 * @return a userScheduler interface to managed the scheduler as user right
	 * @throws SchedulerException
	 */
	public static UserScheduler connectTo(String schedulerURL) throws SchedulerException {
		UserScheduler us = new UserScheduler();
		
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
		
		us.schedulerFrontend = sp;
		return us;
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#getResult(org.objectweb.proactive.core.util.wrapper.IntWrapper)
	 * @throws SchedulerException 
	 */
	public JobResult getResult(JobId jobId, UserIdentification userId) throws SchedulerException {
		return schedulerFrontend.getResult(jobId, userId);
	}

	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#submit(org.objectweb.proactive.extra.scheduler.job.Job)
	 * @throws SchedulerException 
	 */
	public JobId submit(Job job, UserIdentification userId) throws SchedulerException {
		return schedulerFrontend.submit(job, userId);
	}
	

	/**
	 * @throws SchedulerException 
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#listenLog(int, java.lang.String, int)
	 */
	public void listenLog(JobId jobId, String hostname, int port, UserIdentification userId) throws SchedulerException {
		schedulerFrontend.listenLog(jobId,hostname,port,userId);
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.UserSchedulerInterface#addSchedulerEventListener(org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener)
	 */
	public SchedulerState addSchedulerEventListener (SchedulerEventListener sel) {
		return schedulerFrontend.addSchedulerEventListener (sel);
	}
	
}
