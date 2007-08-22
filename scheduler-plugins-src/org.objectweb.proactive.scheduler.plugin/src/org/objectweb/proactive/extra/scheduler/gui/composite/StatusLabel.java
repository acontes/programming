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

	private static StatusLabel instance = null;

	private Label label = null;

	private StatusLabel(Composite parent, GridData gridData, JobsController jobsController) {
		label = new Label(parent, SWT.CENTER | SWT.BORDER);
		label.setText(INITIAL_TEXT);
		label.setForeground(Colors.RED);
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
		setText("freezeeeeeeeee", Colors.GREEN);
	}

	@Override
	public void killedEvent() {
		setText("killed", Colors.RED);
	}

	@Override
	public void pausedEvent() {
		setText("paused", Colors.GREEN);
	}

	@Override
	public void resumedEvent() {
		setText("resumed", Colors.PURPLE);
	}

	@Override
	public void shutDownEvent() {
		setText("shutdown...", Colors.RED);
	}

	@Override
	public void shuttingDownEvent() {
		setText("shutting down.........", Colors.BLUE);
	}

	@Override
	public void startedEvent() {
		setText("démarrer", Colors.PURPLE);
	}

	@Override
	public void stoppedEvent() {
		setText("stoppé...", Colors.PURPLE);
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
