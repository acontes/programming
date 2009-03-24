package org.eclipse.proactive.extendeddebugger.ui.actions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.management.JMX;
import javax.management.ObjectName;

import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.proactive.extendeddebugger.core.ExtendedDebugger;
import org.eclipse.proactive.extendeddebugger.core.RemoteConnection;
import org.eclipse.proactive.extendeddebugger.ui.dialog.ExtendDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;

public class ExtendedAction implements IViewActionDelegate {

	private IWorkbenchWindow window;
	private IDebugView view;
	private TreeViewer viewer;
	private ExtendedDebugger extendedDebugger;
	
	@Override
	public void init(IViewPart view) {
		this.window = view.getViewSite().getWorkbenchWindow();
		this.view = (IDebugView) view;
		this.viewer = (TreeViewer)this.view.getViewer();
	}

	@Override
	public void run(IAction action) {
		try {
			// -------------- A automatiser!
			ExtendDialog extDialog = new ExtendDialog(window.getShell());
			RemoteConnection remoteConnection = new RemoteConnection(
					new URI(extDialog.getUrl()));
			ProActiveConnection proActiveConnection = remoteConnection.getProActiveConnection();
			// -----------------------------
			
			ExtendedDebugger extendedDebugger = new ExtendedDebugger(proActiveConnection, extDialog.getUrl());
			extendedDebugger.start();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}  catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
