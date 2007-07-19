package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;


/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jul 5, 2007
 * @since ProActive 3.2
 */
public class JobResult implements Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = 6287355063616273677L;
	private JobId id = null;
	private Object result = null;
    private Throwable exception = null;

    
    /**
     * ProActive empty constructor
     * 
     */
    public JobResult() {}

    
    /**
     * Instanciate a new JobResult with a jobId and a result
     * 
     * @param id the jobId associated with this result
     * @param result the result associated with this result
     * @param 
     */
    public JobResult(JobId id, Object result, Throwable exception){
    	this.id = id;
    	this.result = result;
    	this.exception = exception;
    }
    

	/**
	 * To get the exception
	 * 
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * To set the exception
	 * 
	 * @param exception the exception to set
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * To get the id
	 * 
	 * @return the id
	 */
	public JobId getId() {
		return id;
	}

	/**
	 * To set the id
	 * 
	 * @param id the id to set
	 */
	public void setId(JobId id) {
		this.id = id;
	}

	/**
	 * To get the result
	 * 
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}
	
	
	/**
	 * Return true if an exception has occured, false otherwise.
	 * 
	 * @return true if an exception has occured, false otherwise.
	 */
	public boolean exceptionOccured() {
		return exception != null;
	}
	

	/**
	 * To set the result
	 * 
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}
	
}
