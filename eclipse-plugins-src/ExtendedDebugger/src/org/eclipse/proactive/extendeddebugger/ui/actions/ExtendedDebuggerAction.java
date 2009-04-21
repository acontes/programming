package org.eclipse.proactive.extendeddebugger.ui.actions;

import java.net.URI;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.proactive.extendeddebugger.core.DebugInfoSingleton;
import org.eclipse.proactive.extendeddebugger.core.ExtendedDebugger;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class ExtendedDebuggerAction implements IViewActionDelegate {

	private boolean enable = false;
	
	@Override
	public void init(IViewPart view) {
	}

	@Override
	public void run(IAction action) {
		if(!enable){
			enable = true;
			action.setChecked(true);
			DebugInfoSingleton info = DebugInfoSingleton.getInstance();
			URI runtimeURL = info.getRuntimURL();
			if(runtimeURL != null){
				new ExtendedDebugger(runtimeURL);
			} else {
				System.err.println("the tunneling is not currently established");
			}
		} else {
			action.setChecked(false);
			enable = false;
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
