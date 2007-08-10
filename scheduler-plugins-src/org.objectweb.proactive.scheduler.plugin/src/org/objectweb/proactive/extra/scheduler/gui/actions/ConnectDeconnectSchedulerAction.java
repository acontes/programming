package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsController;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;
import org.objectweb.proactive.extra.scheduler.gui.dialog.SelectSchedulerDialog;
import org.objectweb.proactive.extra.scheduler.gui.dialog.SelectSchedulerDialogResult;
import org.objectweb.proactive.extra.scheduler.gui.views.SeparatedJobView;

public class ConnectDeconnectSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = true;

	private static ConnectDeconnectSchedulerAction instance = null;
	private Composite parent = null;
	private boolean isConnected = false;

	private ConnectDeconnectSchedulerAction(Composite parent) {
		this.parent = parent;
		this.isConnected = false;
		this.setText("Connect to a scheduler");
		this.setToolTipText("Connect to a started scheduler by its url");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/run.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		if(isConnected)
			disconnection();
		else
			connection();
	}
	
	private void connection() {
		SelectSchedulerDialogResult dialogResult = SelectSchedulerDialog.showDialog(parent.getShell());
		if (dialogResult != null) {

			int res = SchedulerProxy.getInstance().connectToScheduler(dialogResult);

			if (res == SchedulerProxy.CONNECTED) {
				isConnected = true;
				this.setText("Disconnect");
				this.setToolTipText("Disconnect to the scheduler");
				this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/run.png"));
				
				// active reference
				JobsController.getActiveView().init();

				// the call "JobsController.getActiveView().init();"
				// must be terminated here, before starting other call.
				SeparatedJobView.getPendingJobComposite().initTable();
				SeparatedJobView.getRunningJobComposite().initTable();
				SeparatedJobView.getFinishedJobComposite().initTable();

				ChangeViewModeAction.getInstance().setEnabled(true);
				SubmitJobAction.getInstance().setEnabled(true);
				StartStopSchedulerAction.getInstance().setEnabled(true);
				KillSchedulerAction.getInstance().setEnabled(true);

				SeparatedJobView.setVisible(true);
			} else if (res == SchedulerProxy.LOGIN_OR_PASSWORD_WRONG) {
				MessageDialog.openError(parent.getShell(), "Couldn't connect",
						"The login and/or the password are wrong !");
			} else {
				MessageDialog.openError(parent.getShell(), "Couldn't connect",
						"Couldn't Connect to the scheduler based on : \n" + dialogResult.getUrl());
			}
		}
	}
	
	private void disconnection() {
		isConnected = false;
		this.setText("Connect to a scheduler");
		this.setToolTipText("Connect to a started scheduler by its url");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/run.png"));
		
		SeparatedJobView.clearOnDisconnection();
		
		// the follows actions can't be null
		// if the user is not logged as an admin !
		ChangeViewModeAction.getInstance().setEnabled(false);
		KillJobAction.getInstance().setEnabled(false);
		ObtainJobOutputAction.getInstance().setEnabled(false);
		PauseResumeJobAction.getInstance().setEnabled(false);
		SubmitJobAction.getInstance().setEnabled(false);
		
		FreezeSchedulerAction freezeSchedulerAction = FreezeSchedulerAction.getInstance();
		if(freezeSchedulerAction != null)
			freezeSchedulerAction.setEnabled(false);
		
		KillSchedulerAction killSchedulerAction = KillSchedulerAction.getInstance();
		if(killSchedulerAction != null)
			killSchedulerAction.setEnabled(false);
		
		PauseSchedulerAction pauseSchedulerAction = PauseSchedulerAction.getInstance();
		if(pauseSchedulerAction != null)
			pauseSchedulerAction.setEnabled(false);
		
		ResumeSchedulerAction resumeSchedulerAction = ResumeSchedulerAction.getInstance();
		if(resumeSchedulerAction != null)
			resumeSchedulerAction.setEnabled(false);
		
		ShutdownSchedulerAction shutdownSchedulerAction = ShutdownSchedulerAction.getInstance();
		if(shutdownSchedulerAction != null)
			shutdownSchedulerAction.setEnabled(false);
		 
		StartStopSchedulerAction startStopSchedulerAction = StartStopSchedulerAction.getInstance();
		if(startStopSchedulerAction != null)
			startStopSchedulerAction.setEnabled(false);
	}

	public static ConnectDeconnectSchedulerAction newInstance(Composite parent) {
		instance = new ConnectDeconnectSchedulerAction(parent);
		return instance;
	}

	public static ConnectDeconnectSchedulerAction getInstance() {
		return instance;
	}
}
