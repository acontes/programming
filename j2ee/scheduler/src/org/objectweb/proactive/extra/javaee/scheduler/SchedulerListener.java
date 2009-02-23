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

import org.objectweb.proactive.extensions.annotation.RemoteObject;
import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;
import org.ow2.proactive.scheduler.common.job.Job;
import org.ow2.proactive.scheduler.common.job.JobEvent;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.ow2.proactive.scheduler.common.job.UserIdentification;
import org.ow2.proactive.scheduler.common.scheduler.SchedulerEventListener;
import org.ow2.proactive.scheduler.common.task.TaskEvent;

/**
 * This is the ProActive scheduler listener.
 * It is used to receive job finished events.
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
@RemoteObject
public class SchedulerListener implements SchedulerEventListener<Job>, Serializable{
	
	private JobId awaitedJob;
	private boolean resultsAvailable;
	private transient RemoteManagement remote;
	
	public SchedulerListener(){ 
	}
	
	public SchedulerListener(JobId awaitedJob) {
		this.awaitedJob = awaitedJob;
		this.resultsAvailable = false;
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
	
	@Override
	public void jobRunningToFinishedEvent(JobEvent event) {
		// is it our job?
		if( !awaitedJob.equals(event.getJobId())) 
			return;
		synchronized (this) {
			this.resultsAvailable = true;
			this.notifyAll();
		}
	}
	
	public synchronized boolean jobFinished() {
		return resultsAvailable;
	}
	
	public void waitJobFinished(JobId id) throws InterruptedException {
		synchronized (this) {
			while(!resultsAvailable){
				this.wait();
			}
		}
	}
	
	
	////// don't care about these other events
	@Override
	public void jobChangePriorityEvent(JobEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobPausedEvent(JobEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobPendingToRunningEvent(JobEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobRemoveFinishedEvent(JobEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobResumedEvent(JobEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobSubmittedEvent(Job arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerFrozenEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerKilledEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerPausedEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerRMDownEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerRMUpEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerResumedEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerShutDownEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerShuttingDownEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerStartedEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerStoppedEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskPendingToRunningEvent(TaskEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskRunningToFinishedEvent(TaskEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskWaitingForRestart(TaskEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void usersUpdate(UserIdentification arg0) {
		// TODO Auto-generated method stub
		
	}



	
}
