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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.gui.composite.FinishedJobComposite;
import org.objectweb.proactive.extra.scheduler.gui.composite.JobComposite;
import org.objectweb.proactive.extra.scheduler.gui.composite.PendingJobComposite;
import org.objectweb.proactive.extra.scheduler.gui.composite.RunningJobComposite;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsController;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsOutputController;
import org.objectweb.proactive.extra.scheduler.gui.data.TableManager;
import org.objectweb.proactive.extra.scheduler.gui.dialog.SelectSchedulerDialog;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobFactory;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.scripting.InvalidScriptException;
import org.xml.sax.SAXException;

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
	private JobsController jobsController = null;
	private JobComposite pendingJobComposite = null;
	private JobComposite runningJobComposite = null;
	private JobComposite finishedJobComposite = null;
	private Action connectSchedulerAction = null;
	private Action changeViewModeAction = null;
	private Action getJobOutputAction = null;
	private Action submitJob = null;
	private Shell shell = null;
	private Composite parent = null;
	private boolean firstTime = true;
	private static UserSchedulerInterface userScheduler = null;

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
		//manager.add(new Separator());
		manager.add(getJobOutputAction);
		manager.add(submitJob);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(connectSchedulerAction);
		manager.add(changeViewModeAction);
		manager.add(getJobOutputAction);
		manager.add(submitJob);
	}

	private void makeActions() {
		connectSchedulerAction = new Action() {
			@Override
			public void run() {
				String[] tmp = SelectSchedulerDialog.showDialog(shell);
//				userScheduler = SelectSchedulerDialog.showDialog(shell);
//				if (userScheduler != null) {
				if (tmp != null) {
					if (firstTime) {
						firstTime = false;
						try {
							jobsController = (JobsController) (ProActive.turnActive(jobsController));
						} catch (ActiveObjectCreationException e) {
							e.printStackTrace();
						} catch (NodeException e) {
							e.printStackTrace();
						}
					}
//					jobsController.setScheduler(userScheduler);
					jobsController.setScheduler(userScheduler, tmp);
					pendingJobComposite.initTable();
					runningJobComposite.initTable();
					finishedJobComposite.initTable();
					changeViewModeAction.setEnabled(true);
					getJobOutputAction.setEnabled(true);
					submitJob.setEnabled(true);
					setVisible(true);
				}
			}
		};
		connectSchedulerAction.setText("Connect to a scheduler");
		connectSchedulerAction.setToolTipText("Connect to a started scheduler by its url");
		connectSchedulerAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(),
				"icons/run.png"));

		changeViewModeAction = new Action() {
			@Override
			public void run() {
				FillLayout layout = (FillLayout) (parent.getLayout());
				boolean isVertical = layout.type == SWT.VERTICAL;
				layout.type = isVertical ? SWT.HORIZONTAL : SWT.VERTICAL;
				if (isVertical) {
					changeViewModeAction.setToolTipText("Switch view to horizontal mode");
					changeViewModeAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(),
							"icons/horizontal.png"));
				} else {
					changeViewModeAction.setToolTipText("Switch view to vertical mode");
					changeViewModeAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(),
							"icons/vertical.png"));
				}
				parent.layout();
			}
		};
		changeViewModeAction.setText("Switch view mode");
		changeViewModeAction.setToolTipText("Switch view to horizontal mode");
		changeViewModeAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(),
				"icons/horizontal.png"));
		changeViewModeAction.setEnabled(false);

		getJobOutputAction = new Action() {
			@Override
			public void run() {
				TableItem item = TableManager.getInstance().getLastSelectedItem();
				if (item != null) {
					JobId jobId = (JobId) item.getData();
					JobsOutputController.getInstance().createJobOutput(jobId);
				}
			}
		};
		getJobOutputAction.setText("Get job output");
		getJobOutputAction.setToolTipText("To get the job output");
		getJobOutputAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(),
				"icons/output.png"));
		getJobOutputAction.setEnabled(false);

		submitJob = new Action() {
			@Override
			public void run() {
				// TODO
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.xml" });
				String fileName = fileDialog.open();

				if (fileName != null) {
					try {
						// CREATE JOB
						Job job = JobFactory.getFactory().createJob(fileName);
						// SUBMIT JOB
						job.setId(userScheduler.submit(job));
					} catch (XPathExpressionException e) {
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InvalidScriptException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SchedulerException e) {
						//TODO 
						e.printStackTrace();
					}
				}
			}
		};
		submitJob.setText("Submit a job");
		submitJob.setToolTipText("Submit a job to the scheduler");
		submitJob.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/submit.png"));
		submitJob.setEnabled(false);
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
	 * Returns the user scheduler
	 * 
	 * @return the user scheduler
	 */
	public static UserSchedulerInterface getUserScheduler() {
		return userScheduler;
	}

	/**
	 * Returns the jobs controller
	 * 
	 * @return the jobs controller
	 */
	public JobsController getJobsController() {
		return jobsController;
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
		this.shell = parent.getShell();
		this.parent = parent;
		this.firstTime = true;

		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		layout.spacing = 5;
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		parent.setLayout(layout);
		jobsController = JobsController.getInstance();
		pendingJobComposite = new PendingJobComposite(parent, "Pending", jobsController);
		runningJobComposite = new RunningJobComposite(parent, "Running", jobsController);
		finishedJobComposite = new FinishedJobComposite(parent, "Finished", jobsController);
		makeActions();
		hookContextMenu(parent);
		contributeToActionBars();
		setVisible(false);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}
}
