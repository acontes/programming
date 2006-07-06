/*
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 * 
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive-support@inria.fr
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Initial developer(s): The ProActive Team
 * http://www.inria.fr/oasis/ProActive/contacts.html Contributor(s):
 * 
 * ################################################################
 */
package org.objectweb.proactive.calcium.proactive;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.calcium.Skernel;
import org.objectweb.proactive.calcium.Task;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;

public class ActiveObjectSkernel<T> extends Skernel<T> implements RunActive, Serializable {

	public ActiveObjectSkernel(){
	}

	public ActiveObjectSkernel(Skernel<T> skernel){
		super();
		
		while(skernel.getReadyQueueLength() >0){
			this.putTask(skernel.getReadyTask());
		}
	}
	
	@SuppressWarnings("unchecked")
	//Producer-Consumer
	public void runActivity(Body body) {
		Service service = new Service(body);
		
		while(true){
			String allowedMethodNames="getStats|putTask|getReadyQueueLength|hasResults|isFinished|isPaniqued";
			
			if(getReadyQueueLength() > 0 || isFinished()) allowedMethodNames +="getReadyTask|";
			if(hasResults()) allowedMethodNames += "getResult|";

			service.blockingServeOldest( new RequestFilterOnAllowedMethods(allowedMethodNames));
		}
	}
	
	@SuppressWarnings("unchecked")
	public Task<T> getReadyTask(){
		Task task= super.getReadyTask();
		if(task==null){ //ProActive doesn't handle null
			task= new Task();
			task.setDummy();
			return task; //return dummy task
		}
		return task;
	}
	
	protected class RequestFilterOnAllowedMethods implements RequestFilter,
        java.io.Serializable {
        private String allowedMethodNames;

        public RequestFilterOnAllowedMethods(String allowedMethodNames) {
            this.allowedMethodNames = allowedMethodNames;
        }

        public boolean acceptRequest(Request request) {
            return allowedMethodNames.indexOf(request.getMethodName())>=0;
        }
    }
}
