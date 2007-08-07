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
package org.objectweb.proactive.extra.scheduler.gui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.extra.scheduler.gui.actions.ChangeViewModeAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.ConnectDeconnectSchedulerAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.KillJobAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.KillSchedulerAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.ObtainJobOutputAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.PauseResumeJobAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.StartStopSchedulerAction;
import org.objectweb.proactive.extra.scheduler.gui.actions.SubmitJob;
import org.objectweb.proactive.extra.scheduler.gui.composites.FinishedJobComposite;
import org.objectweb.proactive.extra.scheduler.gui.composites.JobComposite;
import org.objectweb.proactive.extra.scheduler.gui.composites.PendingJobComposite;
import org.objectweb.proactive.extra.scheduler.gui.composites.RunningJobComposite;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsController;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsOutputController;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;
import org.objectweb.proactive.extra.scheduler.gui.data.TableManager;

/**
 * This class display the state of the scheduler in real time
 * 
 * @author ProActive Team
 * @version 1.0, Jul 12, 2007
 * @since ProActive 3.2
 */
public class SeparatedJobView extends ViewPart {
	/** the view part id */
	public static final String ID = "org.objectweb.proactive.extra.scheduler.gui.views.SeparatedJobView";
	private static final long serialVersionUID = -6958852991395601640L;
	private JobComposite pendingJobComposite = null;
	private JobComposite runningJobComposite = null;
	private JobComposite finishedJobComposite = null;
	private Action connectSchedulerAction = null;
	private Action changeViewModeAction = null;
	private Action obtainJobOutputAction = null;
	private Action submitJob = null;
	private Action pauseResumeJobAction = null;
	private Action killJobAction = null;

	private Action startStopSchedulerAction = null;
	private Action killSchedulerAction = null;
	private Composite parent = null;

	// -------------------------------------------------------------------- //
	// --------------------------- constructor ---------------------------- //
	// -------------------------------------------------------------------- //
	/**
	 * The constructor.
	 */
	public SeparatedJobView() {}

	// -------------------------------------------------------------------- //
	// ----------------------------- private ------------------------------ //
	// -------------------------------------------------------------------- //
	private void hookContextMenu(Composite parent) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(parent);
		parent.setMenu(menu);
		pendingJobComposite.setMenu(menu);
		runningJobComposite.setMenu(menu);
		finishedJobComposite.setMenu(menu);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(connectSchedulerAction);
		manager.add(changeViewModeAction);
		manager.add(new Separator());
		manager.add(submitJob);
		manager.add(pauseResumeJobAction);
		manager.add(obtainJobOutputAction);
		manager.add(killJobAction);
		if (SchedulerProxy.getInstance().isAnAdmin()) {
			manager.add(new Separator());
			manager.add(startStopSchedulerAction);
			manager.add(killSchedulerAction);
		}
// // Other plug-ins can contribute there actions here
// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(connectSchedulerAction);
		manager.add(changeViewModeAction);
		manager.add(new Separator());
		manager.add(submitJob);
		manager.add(pauseResumeJobAction);
		manager.add(obtainJobOutputAction);
		manager.add(killJobAction);
	}

	private void makeActions() {
		connectSchedulerAction = ConnectDeconnectSchedulerAction.newInstance(this, parent);
		changeViewModeAction = ChangeViewModeAction.newInstance(parent);
		obtainJobOutputAction = ObtainJobOutputAction.newInstance();
		submitJob = SubmitJob.newInstance(parent);
		pauseResumeJobAction = PauseResumeJobAction.newInstance();
		killJobAction = KillJobAction.newInstance();

		startStopSchedulerAction = StartStopSchedulerAction.newInstance();
		killSchedulerAction = KillSchedulerAction.newInstance();
	}

	// -------------------------------------------------------------------- //
	// ------------------------------ public ------------------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * To display or not the view
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		pendingJobComposite.setVisible(visible);
		runningJobComposite.setVisible(visible);
		finishedJobComposite.setVisible(visible);
		// JobInfo jobInfo = JobInfo.getInstance();
		// if(jobInfo != null)
		// jobInfo.setVisible(visible);
		// TaskView taskView = TaskView.getInstance();
		// if(taskView != null)
		// taskView.setVisible(visible);
	}

	/**
	 * Returns the pending job composite
	 * 
	 * @return the pending job composite
	 */
	public JobComposite getPendingJobComposite() {
		return pendingJobComposite;
	}

	/**
	 * Returns the running job composite
	 * 
	 * @return the running job composite
	 */
	public JobComposite getRunningJobComposite() {
		return runningJobComposite;
	}

	/**
	 * Returns the finished job composite
	 * 
	 * @return the finished job composite
	 */
	public JobComposite getFinishedJobComposite() {
		return finishedJobComposite;
	}

	// -------------------------------------------------------------------- //
	// ------------------------- extends viewPart ------------------------- //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		// It must be a FillLayout !
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		layout.spacing = 5;
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		parent.setLayout(layout);

		pendingJobComposite = new PendingJobComposite(parent, "Pending", JobsController.getLocalView());
		runningJobComposite = new RunningJobComposite(parent, "Running", JobsController.getLocalView());
		finishedJobComposite = new FinishedJobComposite(parent, "Finished", JobsController.getLocalView());

		// I must turn active the jobsController after create
		// pendingJobComposite, runningJobComposite and finishedJobComposite.
		JobsController.turnActive();

		makeActions();
		hookContextMenu(parent);
		contributeToActionBars();
		setVisible(false);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {}

	@Override
	public void dispose() {
		TableManager.clearInstance();
		TaskView taskView = TaskView.getInstance();
		if (taskView != null)
			taskView.clear();
		JobInfo jobInfo = JobInfo.getInstance();
		if (jobInfo != null)
			jobInfo.clear();
		JobsOutputController.clearInstance();
		ProActive.terminateActiveObject(JobsController.getActiveView(), false);
		ProActive.terminateActiveObject(SchedulerProxy.getInstance(), false);
		JobsController.clearInstances();
		SchedulerProxy.clearInstance();
		super.dispose();
	}

	private void clearOnDisconnection() {
		setVisible(false);
		TaskView taskView = TaskView.getInstance();
		if (taskView != null)
			taskView.clear();
		JobInfo jobInfo = JobInfo.getInstance();

		if (jobInfo != null)
			jobInfo.clear();
		JobsOutputController jobsOutputController = JobsOutputController.getInstance();
		if (jobsOutputController != null)
			jobsOutputController.removeAllJobOutput();
		SchedulerProxy.clearInstance();
	}
}