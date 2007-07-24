/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.userAPI;

import java.io.IOException;
import java.io.Serializable;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.scheduler.core.SchedulerAuthentification;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;

/**
 * Scheduler connection class provide method to join an existing scheduler.
 * The method return a scheduler authentification in order to permit the scheduler
 * to authenticate user that want to connect a scheduler.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 24, 2007
 * @since ProActive 3.2
 */
public class SchedulerConnection implements Serializable{

	/** Serial version UID */
	private static final long serialVersionUID = 278178831821342953L;
	/** default scheduler node name */
	public static final String SCHEDULER_DEFAULT_NAME = "SCHEDULER";
	/** Scheduler logger */
	private static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);
	
	
	/**
	 * Return the scheduler authentification at the specified URL.
	 * 
	 * @param schedulerURL the URL of the scheduler to join.
	 * @return the scheduler authentification at the specified URL.
	 * @throws SchedulerException thrown if the connection to the scheduler cannot be established.
	 */
	public static SchedulerAuthentificationInterface join(String schedulerURL) throws SchedulerException{
		// Get the scheduler authentification at the specified URL
		SchedulerAuthentificationInterface schedulerAuth = null;
		logger.info("******************* TRYING TO JOIN EXISTING SCHEDULER *****************");
		if (schedulerURL == null){
			logger.info("Scheduler URL was null, looking for scheduler on localhost...");
			schedulerURL = "//localhost/"+SCHEDULER_DEFAULT_NAME;
		}
    	try {
    		schedulerAuth = (SchedulerAuthentificationInterface)(ProActive.lookupActive(SchedulerAuthentification.class.getName(), schedulerURL));
    		return schedulerAuth;
    	} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
			throw new SchedulerException("Error while looking up this active object : "+SchedulerAuthentification.class.getName());
		} catch (IOException e) {
			e.printStackTrace();
			throw new SchedulerException("IO Error while looking up this active object : "+SchedulerAuthentification.class.getName());
		}
	}

}
