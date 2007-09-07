/*
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 * 
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive@objectweb.org
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Initial developer(s): The ProActive Team
 * http://www.inria.fr/oasis/ProActive/contacts.html Contributor(s):
 * 
 * ################################################################
 */
package org.objectweb.proactive.extra.scheduler.gui.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.objectweb.proactive.extra.scheduler.core.Tools;
import org.objectweb.proactive.extra.scheduler.gui.Colors;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.task.Status;
import org.objectweb.proactive.extra.scheduler.task.TaskId;
import org.objectweb.proactive.extra.scheduler.task.descriptor.TaskDescriptor;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jul 11, 2007
 * @since ProActive 3.2
 */
public class TaskComposite extends Composite {

	/** the unique id and the title for the column "Id" */
	public static final String COLUMN_ID_TITLE = "Id";
	/** the unique id and the title for the column "State" */
	public static final String COLUMN_STATUS_TITLE = "State";
	/** the unique id and the title for the column "Name" */
	public static final String COLUMN_NAME_TITLE = "Name";
	/** the unique id and the title for the column "Description" */
	public static final String COLUMN_DESCRIPTION_TITLE = "Description";
	/** the unique id and the title for the column "Run time limit" */
	public static final String COLUMN_RUN_TIME_LIMIT_TITLE = "Run time limit";
	/** the unique id and the title for the column "Re-runnable" */
	public static final String COLUMN_RERUNNABLE_TITLE = "Re-runnable";
	/** the unique id and the title for the column "Start time" */
	public static final String COLUMN_START_TIME_TITLE = "Start time";
	/** the unique id and the title for the column "Finished time" */
	public static final String COLUMN_FINISHED_TIME_TITLE = "Finished time";
	/** the unique id and the title for the column "host name" */
	public static final String COLUMN_HOST_NAME_TITLE = "Host name";
	/** the background color of failed tasks */
	public static final Color TASKS_FAILED_BACKGROUND_COLOR = Colors.RED;

	private List<TaskDescriptor> tasks = null;
	private Label label = null;
	private Table table = null;
	private int order = TaskDescriptor.ASC_ORDER;
	private int lastSorting = TaskDescriptor.SORT_BY_ID;

	/**
	 * This is the default constructor.
	 * 
	 * @param parent
	 */
	public TaskComposite(Composite parent) {
		super(parent, SWT.NONE);
		this.setLayout(new GridLayout());
		this.label = createLabel(parent);
		this.table = createTable(parent);
	}

	private Label createLabel(Composite parent) {
		Label label = new Label(this, SWT.CENTER);
		label.setText("No job selected");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setForeground(Colors.RED);
		return label;
	}

	private Table createTable(Composite parent) {
		table = new Table(this, SWT.BORDER | SWT.SINGLE);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// creating TableColumn
		TableColumn tc1 = new TableColumn(table, SWT.LEFT);
		TableColumn tc2 = new TableColumn(table, SWT.LEFT);
		TableColumn tc3 = new TableColumn(table, SWT.LEFT);
		TableColumn tc4 = new TableColumn(table, SWT.LEFT);
		TableColumn tc5 = new TableColumn(table, SWT.LEFT);
		TableColumn tc6 = new TableColumn(table, SWT.LEFT);
		TableColumn tc7 = new TableColumn(table, SWT.LEFT);
		TableColumn tc8 = new TableColumn(table, SWT.LEFT);
		TableColumn tc9 = new TableColumn(table, SWT.LEFT);
		// addSelectionListener
		tc1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_ID);
			}
		});
		tc2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_STATUS);
			}
		});
		tc3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_NAME);
			}
		});
		tc4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_HOST_NAME);
			}
		});
		tc5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_STARTED_TIME);
			}
		});
		tc6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_FINISHED_TIME);
			}
		});
		tc7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_RUN_TIME_LIMIT);
			}
		});
		tc8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_RERUNNABLE);
			}
		});
		tc9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				sort(event, TaskDescriptor.SORT_BY_DESCRIPTION);
			}
		});
		// setText
		tc1.setText(COLUMN_ID_TITLE);
		tc2.setText(COLUMN_STATUS_TITLE);
		tc3.setText(COLUMN_NAME_TITLE);
		tc4.setText(COLUMN_HOST_NAME_TITLE);
		tc5.setText(COLUMN_START_TIME_TITLE);
		tc6.setText(COLUMN_FINISHED_TIME_TITLE);
		tc7.setText(COLUMN_RUN_TIME_LIMIT_TITLE);
		tc8.setText(COLUMN_RERUNNABLE_TITLE);
		tc9.setText(COLUMN_DESCRIPTION_TITLE);
		// setWidth
		tc1.setWidth(50);
		tc2.setWidth(100);
		tc3.setWidth(100);
		tc4.setWidth(130);
		tc5.setWidth(130);
		tc6.setWidth(130);
		tc7.setWidth(130);
		tc8.setWidth(100);
		tc9.setWidth(200);
		// setMoveable
		tc1.setMoveable(true);
		tc2.setMoveable(true);
		tc3.setMoveable(true);
		tc4.setMoveable(true);
		tc5.setMoveable(true);
		tc6.setMoveable(true);
		tc7.setMoveable(true);
		tc8.setMoveable(true);
		tc9.setMoveable(true);
		return table;
	}

	private void sort(SelectionEvent event, int field) {
		if (lastSorting == field) {
			// if the new sort is the same as the last sort, invert order.
			order = (order == TaskDescriptor.DESC_ORDER) ? TaskDescriptor.ASC_ORDER
					: TaskDescriptor.DESC_ORDER;
			TaskDescriptor.setSortingOrder(order);
		}
		TaskDescriptor.setSortingBy(field);
		lastSorting = field;

		sort();

		table.setSortColumn((TableColumn) event.widget);
		table.setSortDirection((order == TaskDescriptor.DESC_ORDER) ? SWT.DOWN : SWT.UP);
	}

	private void sort() {
		Collections.sort(tasks);
		refreshTable();
	}

	private void refreshTable() {
		if (!isDisposed()) {
			// Turn off drawing to avoid flicker
			table.setRedraw(false);

			// We remove all the table entries
			table.removeAll();

			// then add the entries
			for (TaskDescriptor td : tasks)
				createItem(td);

			// Turn drawing back on
			table.setRedraw(true);
		}
	}

	private void createItem(TaskDescriptor taskDescriptor) {
		if (!table.isDisposed()) {
			TableItem item = new TableItem(table, SWT.NONE);
			// To have a unique identifier for this TableItem
			item.setData(taskDescriptor.getId());
			fillItem(item, taskDescriptor);
		}
	}

	private void fillItem(TableItem item, TaskDescriptor taskDescriptor) {
		if (!table.isDisposed()) {
			TableColumn[] cols = table.getColumns();
			// I'm must fill item by this way, because all columns are
			// moveable !
			// So i don't know if the column "Id" is at the first or the "nth"
			// position
			if (taskDescriptor.getStatus().equals(Status.FAILED))
				item.setBackground(TASKS_FAILED_BACKGROUND_COLOR);
			for (int i = 0; i < cols.length; i++) {
				String title = cols[i].getText();
				if (title.equals(COLUMN_ID_TITLE))
					item.setText(i, taskDescriptor.getId().toString());
				else if (title.equals(COLUMN_STATUS_TITLE))
					item.setText(i, taskDescriptor.getStatus().toString());
				else if (title.equals(COLUMN_NAME_TITLE))
					item.setText(i, taskDescriptor.getName());
				else if (title.equals(COLUMN_DESCRIPTION_TITLE))
					item.setText(i, taskDescriptor.getDescription());
				else if (title.equals(COLUMN_START_TIME_TITLE))
					item.setText(i, Tools.getFormattedDate(taskDescriptor.getStartTime()));
				else if (title.equals(COLUMN_FINISHED_TIME_TITLE))
					item.setText(i, Tools.getFormattedDate(taskDescriptor.getFinishedTime()));
				else if (title.equals(COLUMN_RERUNNABLE_TITLE))
					item.setText(i, taskDescriptor.getRerunnable() + "");
				else if (title.equals(COLUMN_RUN_TIME_LIMIT_TITLE))
					item.setText(i, Tools.getFormattedDate(taskDescriptor.getRunTimeLimit()));
				else if (title.equals(COLUMN_HOST_NAME_TITLE)) {
					String hostName = taskDescriptor.getExecutionHostName();
					if (hostName == null)
						item.setText(i, "n/a");
					else
						item.setText(i, hostName);
				}
			}
		}
	}

	// -------------------------------------------------------------------- //
	// ------------------------------ public ------------------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * This method "clear" the view by removing all item in the table and set
	 * the label to "No job selected"
	 */
	public void clear() {
		table.removeAll();
		label.setText("No job selected");
	}

	/**
	 * This method remove all item of the table and fill it with the tasks
	 * vector. The label is also updated.
	 * 
	 * @param jobId the jobId, just for the label.
	 * @param tasks
	 */
	public void setTasks(JobId jobId, ArrayList<TaskDescriptor> tasks) {
		this.tasks = tasks;
		int tmp = tasks.size();

		if (!label.isDisposed())
			label.setText("Job " + jobId + " has " + tmp + ((tmp == 1) ? " task" : " tasks"));
		refreshTable();
	}

	/**
	 * This method allow to replace only one line on the task table. This method
	 * identify the "good" item with the taskId. The taskDescriptor is use to
	 * fill item.
	 * 
	 * @param taskId the taskId which must be updated
	 * @param taskDescriptor all informations for fill item
	 */
	public void changeLine(TaskId taskId, TaskDescriptor taskDescriptor) {
		if (!table.isDisposed()) {
			TableItem[] items = table.getItems();
			for (TableItem item : items)
				if (((TaskId) item.getData()).equals(taskId)) {
					fillItem(item, taskDescriptor);
					break;
				}
		}
	}

	// -------------------------------------------------------------------- //
	// ------------------------ extends composite ------------------------- //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.eclipse.swt.widgets.Widget#isDisposed()
	 */
	@Override
	public boolean isDisposed() {
		return super.isDisposed() || ((table != null) && (table.isDisposed()))
				|| ((label != null) && (label.isDisposed()));
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setMenu(org.eclipse.swt.widgets.Menu)
	 */
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		table.setMenu(menu);
		label.setMenu(menu);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (label != null)
			label.setVisible(visible);
		if (table != null)
			table.setVisible(visible);
	}
}
