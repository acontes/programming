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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.objectweb.proactive.extra.scheduler.gui.data.FinishedTasksListener;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsController;
import org.objectweb.proactive.extra.scheduler.gui.data.RunningJobsListener;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.task.TaskEvent;

/**
 * This class represents the running jobs
 *
 * @author ProActive Team
 * @version 1.0, Jul 12, 2007
 * @since ProActive 3.2
 */
public class RunningJobComposite extends JobComposite implements RunningJobsListener, FinishedTasksListener {

	/** the unique id and the title for the column "Progress" */
	public static final String COLUMN_TASK_TITLE = "Progress";
	
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
	public RunningJobComposite(Composite parent, String title, JobsController jobsController) {
		super(parent, title, jobsController, RUNNING_TABLE_ID);
		jobsController.addRunningJobsListener(this);
		jobsController.addFinishedTasksListener(this);
	}


	// -------------------------------------------------------------------- //
	// ---------------------- extends JobComposite ------------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composite.JobComposite#getJobs()
	 */
	@Override
	public Vector<JobId> getJobs() {
		return JobsController.getInstance().getRunningsJobs();
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composite.JobComposite#sortJobs()
	 */
	@Override
	public void sortJobs() {
		JobsController.getInstance().sortRunningsJobs();
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composite.JobComposite#createTable(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected Table createTable(Composite parent, int tableId) {
		Table table = super.createTable(parent, tableId);
		TableColumn tc = new TableColumn(table, SWT.RIGHT, 2);
//		tc.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				sort(event, Job.SORT_BY_ID);
//			}
//		});
		tc.setText(COLUMN_TASK_TITLE);
		tc.setWidth(70);
		tc.setMoveable(true);
		tc.setToolTipText("You can't sort by this column");
		return table;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.composite.JobComposite#createItem(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	@Override
	protected TableItem createItem(Job job) {
		Table table = getTable();
		TableItem item = super.createItem(job);
		TableColumn[] cols = table.getColumns();
		for (int i = 0; i < cols.length; i++) {
			String title = cols[i].getText();
			if (title.equals(COLUMN_TASK_TITLE)) {
//				ProgressBar bar = new ProgressBar(table, SWT.NONE);
//				bar.setMaximum(job.getTotalNumberOfTasks());
//				bar.setSelection(job.getNumberOfFinishedTask());
//				TableEditor editor = new TableEditor(table);
//				editor.grabHorizontal = editor.grabVertical = true;
//				editor.setEditor(bar, item, i);
				item.setText(i, job.getNumberOfFinishedTask() + "/" + job.getTotalNumberOfTasks());
			}
		}
		return item;
	}

	// -------------------------------------------------------------------- //
	// ----------------- implements RunningJobsListener ------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.data.RunningJobsListener#addRunningJob(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public void addRunningJob(JobId jobId) {
		addJob(jobId);
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.data.RunningJobsListener#removeRunningJob(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public void removeRunningJob(JobId jobId) {
		removeJob(jobId);
	}

	// -------------------------------------------------------------------- //
	// ----------------- implements FinishedTasksListener ------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.gui.data.FinishedTasksListener#finishedTaskEvent(org.objectweb.proactive.extra.scheduler.task.TaskEvent)
	 */
	@Override
	public void finishedTaskEvent(TaskEvent event) {
		if (!this.isDisposed()) {
			final TaskEvent taskEvent = event;

			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					Table table = getTable();
					TableItem[] items = table.getItems();
					TableItem item = null;
					for(TableItem it : items)
						if(((JobId)(it.getData())).equals(taskEvent.getJobId())) {
							item = it;
							break;
						}
					
					if(item == null)
						throw new IllegalArgumentException("the item which represent the job : " + taskEvent.getJobId() + " is unknown !");
					
					TableColumn[] cols = table.getColumns();
					Job job = JobsController.getInstance().getJobById(taskEvent.getJobId());
					for (int i = 0; i < cols.length; i++) {
						String title = cols[i].getText();
						if ((title != null) && (title.equals(COLUMN_TASK_TITLE))) {
							item.setText(i, job.getNumberOfFinishedTask() + "/" + job.getTotalNumberOfTasks());
							break;
						}
					}
				}
			});
		}
	}
}
