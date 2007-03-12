/*
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2006 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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



package org.objectweb.proactive.taskscheduler;
import org.objectweb.proactive.core.util.wrapper.*;
import org.objectweb.proactive.taskscheduler.exception.UserException;

/**
 * 
 *	Used internally by the scheduler to hold the result. 
 *	This class is used as a future when being executed
 * @author walzouab
 *
 */
public class InternalResult implements java.io.Serializable{
	
	private GenericTypeWrapper<Object> proActiveTaskExecutionResult;
	private LongWrapper executionTime;
	
	private GenericTypeWrapper<Exception> proActiveTaskException;
	private BooleanWrapper exceptionOccured;
	private LongWrapper executionTimeBeforeException;
	private  String killedMessage;
	
	public InternalResult(){}

	
	public String getKilledMessage() {
		return killedMessage;
	}


	public void setKilledMessage(String killedMessage) {
		this.killedMessage = killedMessage;
	}


	//follows are the setters and getters for the above fields.
	public BooleanWrapper getExceptionOccured() 
	{
		
		return exceptionOccured;
	}

	public void setExceptionOccured(BooleanWrapper exceptionOccured) {
		this.exceptionOccured = exceptionOccured;
	}

	public LongWrapper getExecutionTime() 
	{
	
		return executionTime;
	}

	public void setExecutionTime(LongWrapper executionTime) {
		this.executionTime = executionTime;
	}

	public LongWrapper getExecutionTimeBeforeException() 
	{

		return executionTimeBeforeException;
	}

	public void setExecutionTimeBeforeException(
			LongWrapper executionTimeBeforeException) {
		this.executionTimeBeforeException = executionTimeBeforeException;
	}

	public GenericTypeWrapper<Exception> getProActiveTaskException() {
		return proActiveTaskException;
	}

	public void setProActiveTaskException(
			GenericTypeWrapper<Exception> proActiveTaskException) {
		this.proActiveTaskException = proActiveTaskException;
	}

	public GenericTypeWrapper<Object> getProActiveTaskExecutionResult() {
		return proActiveTaskExecutionResult;
	}

	public void setProActiveTaskExecutionResult(
			GenericTypeWrapper<Object> proActiveTaskExecutionResult) {
		this.proActiveTaskExecutionResult = proActiveTaskExecutionResult;
	}

}
