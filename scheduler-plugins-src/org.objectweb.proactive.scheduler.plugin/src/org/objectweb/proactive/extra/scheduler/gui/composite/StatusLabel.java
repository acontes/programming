package org.objectweb.proactive.extra.scheduler.gui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.objectweb.proactive.extra.scheduler.gui.Colors;
import org.objectweb.proactive.extra.scheduler.gui.data.EventSchedulerListener;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsController;

public class StatusLabel implements EventSchedulerListener {

	public static final String INITIAL_TEXT = "clique droit + connect to scheduler...";
	public static final Color INITIAL_COLOR = Colors.DARK_TURQUOISE;
	
	public static final String STARTED_TEXT = "STARTED";
	public static final Color STARTED_COLOR = Colors.GOLD;
	
	public static final String STOPPED_TEXT = "STOPPED";
	public static final Color STOPPED_COLOR = Colors.ORANGE_RED;
	
	public static final String FREEZED_TEXT = "FREEZED";
	public static final Color FREEZED_COLOR = Colors.PURPLE;
	
	public static final String PAUSED_TEXT = "PAUSED";
	public static final Color PAUSED_COLOR = Colors.BLUE3;
	
	public static final String RESUMED_TEXT = "RESUMED";
	public static final Color RESUMED_COLOR = Colors.TOMATO4;
	
	public static final String SHUTTING_DOWN_TEXT = "SHUTTING_DOWN";
	public static final Color SHUTTING_DOWN_COLOR = Colors.PURPLE;
	
	public static final String SHUTTED_DOWN_TEXT = "SHUTTED_DOWN";
	public static final Color SHUTTED_DOWN_COLOR = Colors.PURPLE;
	
	public static final String KILLED_TEXT = "KILLED";
	public static final Color KILLED_COLOR = Colors.GREY10;
	
	public static final String DISCONNECTED_TEXT = "DISCONNECTED";
	public static final Color DISCONNECTED_COLOR = Colors.FOREST_GREEN;

	private static StatusLabel instance = null;

	private Label label = null;

	private StatusLabel(Composite parent, GridData gridData, JobsController jobsController) {
		label = new Label(parent, SWT.CENTER | SWT.BORDER);
		label.setText(INITIAL_TEXT);
		label.setForeground(INITIAL_COLOR);
		label.setFont(new Font(Display.getDefault(), "", 12, SWT.BOLD));
		label.setLayoutData(gridData);
		jobsController.addEventSchedulerListener(this);
	}

	public static void newInstance(Composite parent, GridData gridData, JobsController jobsController) {
		instance = new StatusLabel(parent, gridData, jobsController);
	}

	public static StatusLabel getInstance() {
		return instance;
	}

	@Override
	public void freezeEvent() {
		setText(FREEZED_TEXT, FREEZED_COLOR);
	}

	@Override
	public void killedEvent() {
		setText(KILLED_TEXT, KILLED_COLOR);
	}

	@Override
	public void pausedEvent() {
		setText(PAUSED_TEXT, PAUSED_COLOR);
	}

	@Override
	public void resumedEvent() {
		setText(RESUMED_TEXT, RESUMED_COLOR);
	}

	@Override
	public void shutDownEvent() {
		setText(SHUTTED_DOWN_TEXT, SHUTTED_DOWN_COLOR);
	}

	@Override
	public void shuttingDownEvent() {
		setText(SHUTTING_DOWN_TEXT, SHUTTING_DOWN_COLOR);
	}

	@Override
	public void startedEvent() {
		setText(STARTED_TEXT, STARTED_COLOR);
	}

	@Override
	public void stoppedEvent() {
		setText(STOPPED_TEXT, STOPPED_COLOR);
	}
	
	public void disconnect() {
		setText(DISCONNECTED_TEXT, DISCONNECTED_COLOR);
	}

	private void setText(String aText, Color aColor) {
		final String text = aText;
		final Color color = aColor;
		if (!label.isDisposed()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					label.setForeground(color);
					label.setText(text);
				}
			});
		}
	}
}