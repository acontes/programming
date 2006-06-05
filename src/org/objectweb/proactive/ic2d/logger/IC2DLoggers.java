package org.objectweb.proactive.ic2d.logger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

/**
 * This class contains all loggers of the application 
 * (classes which implements IC2DMessageLogger).
 * So when you want to log an event, you just have to call the 
 * appropriate method on the (unique) instance of Loggers
 * and all loggers will be TODO
 */
public class IC2DLoggers implements IC2DMessageLogger {

	/** Pattern singleton */
	private static IC2DLoggers instance;
	
	/** Loggers (List<IC2DMessageLogger>)*/
	private List loggers;
	
	/** If we have no IC2DMessageLogger */
	private static Logger log4jlogger = ProActiveLogger.getLogger(Loggers.IC2D);
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	private IC2DLoggers() {
		this.loggers = new ArrayList();
	}
	
	//
	// -- PUBLIC METHODS ---------------------------------------------
	//
	
	// Pattern singleton
	/**
	 * Gets the unique instance of Loggers.
	 * @return the unique instance of Loggers
	 */
	public static IC2DLoggers getInstance() {
		if(instance == null)
			instance = new IC2DLoggers();
		return instance;
	}
	
	/**
	 * Adds a logger to the list
	 * @param logger a new logger
	 */
	public void addLogger(IC2DMessageLogger logger) {
		instance.loggers.add(logger);
	}
	
	public void warn(String message) {
		if(loggers.size() == 0) {
			log4jlogger.warn(message);
			return;
		}
		for(int i=0 ; i<loggers.size() ; i++)
			((IC2DMessageLogger)loggers.get(i)).warn(message);
	}

	
	public void log(String message, Throwable e, boolean dialog) {
		if(loggers.size() == 0) {
			log4jlogger.info(message, e);
			return;
		}
		for(int i=0 ; i<loggers.size() ; i++)
			((IC2DMessageLogger)loggers.get(i)).log(message, e, dialog);
	}

	
	public void log(Throwable e, boolean dialog) {
		if(loggers.size() == 0) {
			log4jlogger.info(e);
			return;
		}
		for(int i=0 ; i<loggers.size() ; i++)
			((IC2DMessageLogger)loggers.get(i)).log(e, dialog);
	}

	
	
	public void log(String message) {
		if(loggers.size() == 0) {
			log4jlogger.info(message);
			return;
		}
		for(int i=0 ; i<loggers.size() ; i++)
			((IC2DMessageLogger)loggers.get(i)).log(message);
	}

	
	public void log(String message, Throwable e) {
		if(loggers.size() == 0) {
			log4jlogger.info(message, e);
			return;
		}
		for(int i=0 ; i<loggers.size() ; i++)
			((IC2DMessageLogger)loggers.get(i)).log(message, e);
	}

	
	public void log(Throwable e) {
		if(loggers.size() == 0) {
			log4jlogger.info(e);
			return;
		}
		for(int i=0 ; i<loggers.size() ; i++)
			((IC2DMessageLogger)loggers.get(i)).log(e);
	}

	
	
}
