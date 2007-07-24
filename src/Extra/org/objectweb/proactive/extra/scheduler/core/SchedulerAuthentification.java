/**
 * 
 */
package org.objectweb.proactive.extra.scheduler.core;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.LoginException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.UserIdentification;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerAuthentificationInterface;
import org.objectweb.proactive.extra.security.FileLogin;
import org.objectweb.proactive.extra.security.Login;

/**
 * This is the authentification class of the scheduler.
 * To get an instance of the scheduler you must ident yourselfwith this class.
 * Once authenticate, the <code>login</code> method returns a user/admin interface
 * in order to managed the scheduler.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 23, 2007
 * @since ProActive 3.2
 *
 */
public class SchedulerAuthentification implements SchedulerAuthentificationInterface {

	
	/** Serial version UID */
	private static final long serialVersionUID = -3143047028779653795L;
	/** Scheduler logger */
	private static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);
	/** the file where to store the allowed user//password */
	private String loginFile;
	/** the scheduler frontend connected to this authentification interface */
	private SchedulerFrontend scheduler;
	
	
	/**
	 * ProActive empty constructor.
	 * This will also set java.security.auth.login.config property.
	 */
	public SchedulerAuthentification(){
		System.setProperty("java.security.auth.login.config",Login.class.getResource("jaas.config").getFile());
	}
	
	
	/**
	 * Get a new instance of SchedulerAuthentification according to the given logins file.
	 * 
	 * @param loginFile the file path where to check if a username//password is correct.
	 * @param scheduler the scheduler front-end on which to connect the user after authentification success.
	 */
	public SchedulerAuthentification(String loginFile, SchedulerFrontend scheduler){
		this();
		this.loginFile = loginFile;
		this.scheduler = scheduler;
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerAuthentificationInterface#logAsUser(java.lang.String, java.lang.String)
	 */
	public UserScheduler logAsUser(String user, String password) throws LoginException, SchedulerException {
		// Verify that this user//password can connect to this existing scheduler
		logger.info("Verifying user name and password...");
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("username", user);
		params.put("pw", password);
		params.put("path", loginFile);
		FileLogin.login(params);
		logger.info("Logging successfull for user : "+user);
		// create user scheduler interface
		logger.info("Connecting to the scheduler...");
		UserScheduler us = new UserScheduler();
		us.schedulerFrontend = scheduler;
		//add this user to the scheduler front-end
		scheduler.connect(
				ProActive.getContext().getCurrentRequest().getSourceBodyID(),
				new UserIdentification(user));
		// return the created interface
		return us;
	}
	
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.SchedulerAuthentificationInterface#logAsAdmin(java.lang.String, java.lang.String)
	 */
	public AdminScheduler logAsAdmin(String user, String password) throws LoginException, SchedulerException {
		// Verify that this user//password can connect (as admin) to this existing scheduler.
		logger.info("Verifying admin name and password...");
		Map<String, Object> params = new HashMap<String, Object>(3);
		params.put("username", user);
		params.put("pw", password);
		params.put("path", loginFile);
		FileLogin.login(params);
		logger.info("Logging successfull for user : "+user);
		// create admin scheduler interface
		logger.info("Connecting to the scheduler...");
		AdminScheduler as = new AdminScheduler();
		as.schedulerFrontend = scheduler;
		//add this user to the scheduler front-end
		scheduler.connect(
				ProActive.getContext().getCurrentRequest().getSourceBodyID(),
				new UserIdentification(user,true));
		// return the created interface
		return as;
	}

}
