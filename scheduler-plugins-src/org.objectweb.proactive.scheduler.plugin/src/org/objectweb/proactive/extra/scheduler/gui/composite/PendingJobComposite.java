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
import org.objectweb.proactive.extra.scheduler.gui.data.JobsController;
import org.objectweb.proactive.extra.scheduler.gui.data.PendingJobsListener;
import org.objectweb.proactive.extra.scheduler.job.JobId;

/**
 * This class represents the pending jobs
 *
 * @author ProActive Team
 * @version 1.0, Jul 12, 2007
 * @since ProActive 3.2
 */
public class PendingJobComposite extends JobComposite implements PendingJobsListener {
	
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
	public PendingJobComposite(Composite parent, String title, JobsController jobsController) {
		super(parent, title, jobsController, PENDING_TABLE_ID);
		jobsController.addPendingJobsListener(this);
	}
	
	// -------------------------------------------------------------------- //
	// ---------------------- extends JobComposite ------------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composite.JobComposite#getJobs()
	 */
	@Override
	public Vector<JobId> getJobs() {
		return JobsController.getInstance().getPendingsJobs();
	}
	
	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composite.JobComposite#sortJobs()
	 */
	@Override
	public void sortJobs() {
		JobsController.getInstance().sortPendingsJobs();
	}

	// -------------------------------------------------------------------- //
	// ----------------- implements PendingJobsListener ------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.data.PendingJobsListener#addPendingJob(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public void addPendingJob(JobId jobId) {
		addJob(jobId);
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.data.PendingJobsListener#removePendingJob(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public void removePendingJob(JobId jobId) {
		removeJob(jobId);
	}
}
