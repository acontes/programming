/*
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 * 
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive@objectweb.org
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
package org.objectweb.proactive.extra.scheduler.gui.composite;

import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.objectweb.proactive.extra.scheduler.gui.actions.KillJobAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.ObtainJobOutputAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.PauseResumeJobAction;
import org.objectweb.proactive.extra.scheduler.gui.data.FinishedJobsListener;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsController;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;

/**
 * This class represents the finished jobs
 * 
 * @author ProActive Team
 * @version 1.0, Jul 12, 2007
 * @since ProActive 3.2
 */
public class FinishedJobComposite extends JobComposite implements FinishedJobsListener {

	// -------------------------------------------------------------------- //
	// --------------------------- constructor ---------------------------- //
	// -------------------------------------------------------------------- //
	/**
	 * This is the default constructor.
	 * 
	 * @param parent
	 * @param title
	 * @param jobsController
	 */
	public FinishedJobComposite(Composite parent, String title, JobsController jobsController) {
		super(parent, title, jobsController, FINISHED_TABLE_ID);
		jobsController.addFinishedJobsListener(this);
	}

	// -------------------------------------------------------------------- //
	// ---------------------- extends JobComposite ------------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composites.JobComposite#getJobs()
	 */
	@Override
	public Vector<JobId> getJobs() {
		return JobsController.getLocalView().getFinishedJobs();
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composites.JobComposite#sortJobs()
	 */
	@Override
	public void sortJobs() {
		JobsController.getLocalView().sortFinishedJobs();
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composites.JobComposite#jobSelected(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	@Override
	public void jobSelected(Job job) {
		// enabling/disabling button permitted with this job
		ObtainJobOutputAction.getInstance().setEnabled(
				SchedulerProxy.getInstance().isItHisJob(job.getOwner()));
		PauseResumeJobAction pauseResumeJobAction = PauseResumeJobAction.getInstance();
		pauseResumeJobAction.setEnabled(false);
		pauseResumeJobAction.setPauseResumeMode();
		KillJobAction.getInstance().setEnabled(false);
	}

	// -------------------------------------------------------------------- //
	// ----------------- implements FinishedJobsListener ------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.data.FinishedJobsListener#addFinishedJob(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public void addFinishedJob(JobId jobId) {
		addJob(jobId);
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.data.FinishedJobsListener#removeFinishedJob(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public void removeFinishedJob(JobId jobId) {
		removeJob(jobId);
	}
}
