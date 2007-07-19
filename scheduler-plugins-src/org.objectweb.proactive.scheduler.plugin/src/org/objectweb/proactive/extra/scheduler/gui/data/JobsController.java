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
package org.objectweb.proactive.extra.scheduler.gui.data;

import java.util.Collections;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener;
import org.objectweb.proactive.extra.scheduler.core.SchedulerState;
import org.objectweb.proactive.extra.scheduler.core.UserScheduler;
import org.objectweb.proactive.extra.scheduler.gui.composite.JobComposite;
import org.objectweb.proactive.extra.scheduler.gui.views.JobInfo;
import org.objectweb.proactive.extra.scheduler.gui.views.TaskView;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobEvent;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.task.TaskDescriptor;
import org.objectweb.proactive.extra.scheduler.task.TaskEvent;
import org.objectweb.proactive.extra.scheduler.task.TaskId;

/**
 *
 *
 * @author ProActive Team
 * @version 1.0, Jul 12, 2007
 * @since ProActive 3.2
 */
public class JobsController implements SchedulerEventListener {

	private static final long serialVersionUID = -160416757449171779L;

	// The shared instance
	private static JobsController instance = null;

	// jobs
	private Vector<Job> jobs = null;

	// jobs ids
	private Vector<JobId> pendingJobsIds = null;
	private Vector<JobId> runningJobsIds = null;
	private Vector<JobId> finishedJobsIds = null;

	// listeners
	private Vector<PendingJobsListener> pendingJobsListeners = null;
	private Vector<RunningJobsListener> runningJobsListeners = null;
	private Vector<FinishedJobsListener> finishedJobsListeners = null;
	private Vector<FinishedTasksListener> finishedTasksListeners = null;

	// -------------------------------------------------------------------- //
	// --------------------------- constructor ---------------------------- //
	// -------------------------------------------------------------------- //
	public JobsController() {
		pendingJobsListeners = new Vector<PendingJobsListener>();
		runningJobsListeners = new Vector<RunningJobsListener>();
		finishedJobsListeners = new Vector<FinishedJobsListener>();
		finishedTasksListeners = new Vector<FinishedTasksListener>();
	}

	// -------------------------------------------------------------------- //
	// ----------------------------- private ------------------------------ //
	// -------------------------------------------------------------------- //
	/** call "addPendingJob" method on listeners */
	private void addPendingJobEventInternal(JobId jobId) {
		for (PendingJobsListener listener : pendingJobsListeners)
			listener.addPendingJob(jobId);
	}

	/** call "removePendingJob" method on listeners */
	private void removePendingJobEventInternal(JobId jobId) {
		for (PendingJobsListener listener : pendingJobsListeners)
			listener.removePendingJob(jobId);
	}

	/** call "addRunningJob" method on listeners */
	private void addRunningJobEventInternal(JobId jobId) {
		for (RunningJobsListener listener : runningJobsListeners)
			listener.addRunningJob(jobId);
	}

	/** call "removeRunningJob" method on listeners */
	private void removeRunningJobEventInternal(JobId jobId) {
		for (RunningJobsListener listener : runningJobsListeners)
			listener.removeRunningJob(jobId);
	}

	/** call "addFinishedJob" method on listeners */
	private void addFinishedJobEventInternal(JobId jobId) {
		for (FinishedJobsListener listener : finishedJobsListeners)
			listener.addFinishedJob(jobId);
	}

	/** call "removeFinishedJob" method on listeners */
	private void removeFinishedJobEventInternal(JobId jobId) {
		for (FinishedJobsListener listener : finishedJobsListeners)
			listener.removeFinishedJob(jobId);
	}

	/** call "finishedTaskEvent" method on listeners */
	private void runningToFinishedTaskEventInternal(TaskEvent event) {
		for (FinishedTasksListener listener : finishedTasksListeners)
			listener.finishedTaskEvent(event);
	}

	// -------------------------------------------------------------------- //
	// ---------------- implements SchedulerEventListener ----------------- //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#newPendingJobEvent(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	@Override
	public void newPendingJobEvent(Job job) {
		// add job to the global jobs list
		jobs.add(job);

		// add job to the pending jobs list
		if (!pendingJobsIds.add(job.getId()))
			throw new IllegalStateException("can't add the job (id = " + job.getJobInfo()
					+ ") from the pendingJobsIds list !");

		// call method on listeners
		addPendingJobEventInternal(job.getId());
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#pendingToRunningJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	@Override
	public void pendingToRunningJobEvent(JobEvent event) {
		JobId jobId = event.getJobId();
		getJobById(jobId).setJobInfo(event);

		// remember if the job, which changing list, was selected
		boolean remember = TableManager.getInstance().isJobSelectedInThisTable(jobId,
				JobComposite.PENDING_TABLE_ID);

		// call method on listeners
		removePendingJobEventInternal(jobId);

		// remove job from the pending jobs list 
		if (!pendingJobsIds.remove(jobId))
			throw new IllegalStateException("can't remove the job (id = " + jobId
					+ ") from the pendingJobsIds list !");

		// add job to running jobs list
		if (!runningJobsIds.add(jobId))
			throw new IllegalStateException("can't add the job (id = " + jobId
					+ ") from the runningJobsIds list !");

		// call method on listeners
		addRunningJobEventInternal(jobId);

		// if the job was selected, move its selection to an other table
		if (remember)
			TableManager.getInstance().moveJobSelection(jobId, JobComposite.RUNNING_TABLE_ID);
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#runningToFinishedJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	@Override
	public void runningToFinishedJobEvent(JobEvent event) {
		JobId jobId = event.getJobId();
		getJobById(jobId).setJobInfo(event);

		// remember if the job, which changing list, was selected
		boolean remember = TableManager.getInstance().isJobSelectedInThisTable(jobId,
				JobComposite.RUNNING_TABLE_ID);

		// call method on listeners
		removeRunningJobEventInternal(jobId);

		// remove job from the running jobs list
		if (!runningJobsIds.remove(jobId))
			throw new IllegalStateException("can't remove the job (id = " + jobId
					+ ") from the runningJobsIds list !");

		// add job to finished jobs list
		if (!finishedJobsIds.add(jobId))
			throw new IllegalStateException("can't add the job (id = " + jobId
					+ ") from the finishedJobsIds list !");

		// call method on listeners
		addFinishedJobEventInternal(jobId);

		// if the job was selected, move its selection to an other table
		if (remember)
			TableManager.getInstance().moveJobSelection(jobId, JobComposite.FINISHED_TABLE_ID);
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#removeFinishedJobEvent(org.objectweb.proactive.extra.scheduler.job.JobEvent)
	 */
	@Override
	public void removeFinishedJobEvent(JobEvent event) {
		JobId jobId = event.getJobId();
		getJobById(jobId).setJobInfo(event);

		// call method on listeners
		removeFinishedJobEventInternal(jobId);

		Job job = new Job();
		job.setId(jobId);
		if (!jobs.remove(job))
			throw new IllegalStateException("can't remove the job (id = " + jobId + ") from the jobs list !");

		// remove job from the finished jobs list
		if (!finishedJobsIds.remove(jobId))
			throw new IllegalStateException("can't remove the job (id = " + jobId
					+ ") from the finishedJobsIds list !");

		// remove job's output
		JobsOutputController.getInstance().removeJobOutput(jobId);
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#pendingToRunningTaskEvent(org.objectweb.proactive.extra.scheduler.task.TaskEvent)
	 */
	@Override
	public void pendingToRunningTaskEvent(TaskEvent event) {
		JobId jobId = event.getJobId();
		getJobById(jobId).setTaskInfo(event);
		final TaskEvent taskEvent = event;

		// if this job is selected in the Running table
		if (TableManager.getInstance().isJobSelectedInThisTable(jobId, JobComposite.RUNNING_TABLE_ID)) {
			final Job job = getJobById(jobId);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// update info
					JobInfo jobInfo = JobInfo.getInstance();
					if (jobInfo != null)
						jobInfo.updateInfos(job);

					TaskView taskView = TaskView.getInstance();
					if (taskView != null)
						taskView.lineUpdate(taskEvent, getTaskDescriptorById(job, taskEvent.getTaskID()));
				}
			});
		}
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.SchedulerEventListener#runningToFinishedTaskEvent(org.objectweb.proactive.extra.scheduler.task.TaskEvent)
	 */
	@Override
	public void runningToFinishedTaskEvent(TaskEvent event) {
		JobId jobId = event.getJobId();
		getJobById(jobId).setTaskInfo(event);
		final TaskEvent taskEvent = event;

		// call method on listeners
		runningToFinishedTaskEventInternal(event);

		// if this job is selected in the Running table
		if (TableManager.getInstance().isJobSelectedInThisTable(jobId, JobComposite.RUNNING_TABLE_ID)) {
			final Job job = getJobById(jobId);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// update info
					JobInfo jobInfo = JobInfo.getInstance();
					if (jobInfo != null)
						jobInfo.updateInfos(job);

					TaskView taskView = TaskView.getInstance();
					if (taskView != null)
						taskView.lineUpdate(taskEvent, getTaskDescriptorById(job, taskEvent.getTaskID()));
				}
			});
		}
	}

	// -------------------------------------------------------------------- //
	// ---------------------- add & remove Listeners ---------------------- //
	// -------------------------------------------------------------------- //
	public void addPendingJobsListener(PendingJobsListener listener) {
		pendingJobsListeners.add(listener);
	}

	public void removePendingJobsListener(PendingJobsListener listener) {
		pendingJobsListeners.remove(listener);
	}

	public void addRunningJobsListener(RunningJobsListener listener) {
		runningJobsListeners.add(listener);
	}

	public void removeRunningJobsListener(RunningJobsListener listener) {
		runningJobsListeners.remove(listener);
	}

	public void addFinishedJobsListener(FinishedJobsListener listener) {
		finishedJobsListeners.add(listener);
	}

	public void removeFinishedJobsListener(FinishedJobsListener listener) {
		finishedJobsListeners.remove(listener);
	}

	public void addFinishedTasksListener(FinishedTasksListener listener) {
		finishedTasksListeners.add(listener);
	}

	public void removeFinishedTasksListener(FinishedTasksListener listener) {
		finishedTasksListeners.remove(listener);
	}

	// -------------------------------------------------------------------- //
	// ----------------------------- get jobs ----------------------------- //
	// -------------------------------------------------------------------- //
	public Vector<JobId> getPendingsJobs() {
		return pendingJobsIds;
	}

	public Vector<JobId> getRunningsJobs() {
		return runningJobsIds;
	}

	public Vector<JobId> getFinishedJobs() {
		return finishedJobsIds;
	}

	// -------------------------------------------------------------------- //
	// ---------------------------- sort jobs ----------------------------- //
	// -------------------------------------------------------------------- //
	public void sortPendingsJobs() {
		Vector<Job> jobs = new Vector<Job>();
		for (JobId id : pendingJobsIds)
			jobs.add(getJobById(id));
		Collections.sort(jobs);

		Vector<JobId> tmp = new Vector<JobId>();
		for (Job job : jobs)
			tmp.add(job.getId());

		pendingJobsIds = tmp;
	}

	public void sortRunningsJobs() {
		Vector<Job> jobs = new Vector<Job>();
		for (JobId id : runningJobsIds)
			jobs.add(getJobById(id));
		Collections.sort(jobs);

		Vector<JobId> tmp = new Vector<JobId>();
		for (Job job : jobs)
			tmp.add(job.getId());

		runningJobsIds = tmp;
	}

	public void sortFinishedJobs() {
		Vector<Job> jobs = new Vector<Job>();
		for (JobId id : finishedJobsIds)
			jobs.add(getJobById(id));
		Collections.sort(jobs);

		Vector<JobId> tmp = new Vector<JobId>();
		for (Job job : jobs)
			tmp.add(job.getId());

		finishedJobsIds = tmp;
	}

	// -------------------------------------------------------------------- //
	// ------------------------------ others ------------------------------ //
	// -------------------------------------------------------------------- //
	public Job getJobById(JobId id) {
		for (Job job : jobs)
			if (job.getId().equals(id))
				return job;
		throw new IllegalArgumentException("there are no jobs with the id : " + id);
	}

	public TaskDescriptor getTaskDescriptorById(Job job, TaskId id) {
		TaskDescriptor taskDescriptor = job.getHMTasks().get(id);
		if (taskDescriptor == null)
			throw new IllegalArgumentException("there are no taskDescriptor with the id : " + id
					+ " in the job : " + job.getId());
		return taskDescriptor;
	}

	public boolean setScheduler(UserScheduler userScheduler) {
		SchedulerState state = userScheduler.addSchedulerEventListener(((SchedulerEventListener) ProActive
				.getStubOnThis()));
		jobs = new Vector<Job>();
		pendingJobsIds = new Vector<JobId>();
		runningJobsIds = new Vector<JobId>();
		finishedJobsIds = new Vector<JobId>();

		Vector<Job> tmp = state.getPendingJobs();
		for (Job job : tmp) {
			jobs.add(job);
			pendingJobsIds.add(job.getId());
		}

		tmp = state.getRunningJobs();
		for (Job job : tmp) {
			jobs.add(job);
			runningJobsIds.add(job.getId());
		}

		tmp = state.getFinishedJobs();
		for (Job job : tmp) {
			jobs.add(job);
			finishedJobsIds.add(job.getId());
		}
		return true;
	}

	public static JobsController getInstance() {
		if (instance == null)
			instance = new JobsController();
		return instance;
	}
}
