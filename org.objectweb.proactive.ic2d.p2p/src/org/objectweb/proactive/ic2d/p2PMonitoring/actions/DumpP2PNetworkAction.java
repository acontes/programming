package org.objectweb.proactive.ic2d.p2PMonitoring.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.objectweb.proactive.ic2d.p2PMonitoring.dialog.DumpP2PNetworkDialog;

public class DumpP2PNetworkAction  implements IWorkbenchWindowActionDelegate {

	private Display display;
	
	public void dispose() {
		
	}

	public void init(IWorkbenchWindow window) {
		this.display = window.getShell().getDisplay();
		
		
	}

	public void run(IAction action) {
		DumpP2PNetworkDialog d = new DumpP2PNetworkDialog(display.getActiveShell());
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
