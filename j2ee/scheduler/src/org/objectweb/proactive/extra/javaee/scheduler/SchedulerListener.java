/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.javaee.scheduler;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.extensions.annotation.RemoteObject;
import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;
import org.ow2.proactive.scheduler.common.NotificationData;
import org.ow2.proactive.scheduler.common.SchedulerEvent;
import org.ow2.proactive.scheduler.common.SchedulerEventListener;
import org.ow2.proactive.scheduler.common.job.Job;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.ow2.proactive.scheduler.common.job.JobState;
import org.ow2.proactive.scheduler.common.job.UserIdentification;
import org.ow2.proactive.scheduler.common.task.TaskInfo;
import org.ow2.proactive.scheduler.common.job.JobInfo;

/**
 * This is the ProActive scheduler listener.
 * It is used to receive job finished events.
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
@RemoteObject
public class SchedulerListener implements SchedulerEventListener, Serializable{
	
	// monitored jobs
	private Map<JobId,ConnectorJobInfo> mapOfJobs = Collections.synchronizedMap(
			new HashMap<JobId, ConnectorJobInfo>());
	private transient RemoteManagement remote;
	
	public SchedulerListener(){ 
	}
	
	/**
	 * Create a remote version of this object.
	 * @return remote reference
	 * @throws UnknownProtocolException
	 */
	public SchedulerListener createRemoteReference() throws UnknownProtocolException {
		remote = new RemoteManagement(this);
		
		return remote.create();
	}
	
	/**
	 * Cleanup remote-related stuff
	 */
	public void destroyRemoteReference() throws ProActiveException {
		remote.cleanup();
	}
	
	
	/**
	 * Remote related stuff
	 */
	class RemoteManagement {
		private RemoteObjectExposer<SchedulerListener> roe;
		private URI uri;
 
		public RemoteManagement(SchedulerListener listener) {
			roe = PARemoteObject
			.newRemoteObject(SchedulerListener.class.getName(), listener);

			uri = RemoteObjectHelper.generateUrl(SchedulerListener.class.getSimpleName());
		}
		
		public SchedulerListener create() throws UnknownProtocolException {
			return PARemoteObject.bind(roe, uri); 
		}
		
		public void cleanup() throws ProActiveException {
			roe.unexport(uri);
		}
	}
	
	public void startMonitoring(JobId id) throws SchedulerListenerException{
		if(mapOfJobs.containsKey(id))
			throw new SchedulerListenerException("The listener already monitors the execution of job " + id);
		mapOfJobs.put(id, new ConnectorJobInfo());
	}
	
	public void stopMonitoring(JobId id) {
		mapOfJobs.remove(id);
	}
	
	public void jobRunningToFinishedEvent(NotificationData<JobInfo> event) {
		ConnectorJobInfo job = mapOfJobs.get(event.getData());
		// are we monitoring this job?
		if( job == null) 
			return;
		
		synchronized (job.lock) {
			job.jobFinished = true;
			job.lock.notifyAll();
		}
	}
	
	public boolean jobFinished(JobId jobId) throws SchedulerListenerException{
		ConnectorJobInfo info = mapOfJobs.get(jobId);
		if(info == null)
			throw new SchedulerListenerException("The listener does not monitor the execution of job " + jobId );
		boolean ret;
		synchronized (info.lock) {
			ret = info.jobFinished;
		}
		return ret;
	}
	
	public void waitJobFinished(JobId id) throws InterruptedException, SchedulerListenerException{
		ConnectorJobInfo info = mapOfJobs.get(id);
		if(info == null)
			throw new SchedulerListenerException("The listener does not monitor the execution of job " + id );
		synchronized (info.lock) {
			while(!info.jobFinished){
				info.lock.wait();
			}
		}
	}
	
	class ConnectorJobInfo {
		boolean jobFinished;
		final Object lock;
		
		public ConnectorJobInfo() {
			jobFinished = false;
			lock = new Object();
		}
	}
	

	@Override
	public void jobSubmittedEvent(JobState arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerStateUpdatedEvent(SchedulerEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskStateUpdatedEvent(NotificationData<TaskInfo> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void usersUpdatedEvent(NotificationData<UserIdentification> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobStateUpdatedEvent(NotificationData<JobInfo> arg0) {
		if(arg0.getEventType().equals(SchedulerEvent.JOB_RUNNING_TO_FINISHED)) {
			jobRunningToFinishedEvent(arg0);
		}
	}
	
}
