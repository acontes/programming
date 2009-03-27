package org.eclipse.proactive.extendeddebugger.ui.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.proactive.extendeddebugger.core.ExtendedDebugger;
import org.eclipse.proactive.extendeddebugger.ui.dialog.ExtendDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.sun.jdi.connect.Connector;

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
		// -------------- A automatiser!
		//		ExtendDialog extDialog = new ExtendDialog(window.getShell());
		//		ExtendedDebugger extendedDebugger = new ExtendedDebugger(extDialog.getUrl());
		// -----------------------------	

		ExtendDialog extDialog = new ExtendDialog(window.getShell());

		// verifier qu'on a le bon launch (Debug)
		for(ILaunch l : (DebugPlugin.getDefault().getLaunchManager().getLaunches())){
			if(l.getLaunchMode().equals(ILaunchManager.DEBUG_MODE)){
				try {
					// create a launch config to do the attach
					IVMConnector connector = JavaRuntime.getVMConnector(IJavaLaunchConfigurationConstants.ID_SOCKET_ATTACH_VM_CONNECTOR);
					Map def = connector.getDefaultArguments();
					Map argMap = new HashMap(def.size());
					Iterator iter = connector.getArgumentOrder().iterator();
					while (iter.hasNext()) {
						String key = (String)iter.next();
						Connector.Argument arg = (Connector.Argument)def.get(key);
						if(key.equals("hostname")){
							argMap.put(key, extDialog.getUrl());
						}
						if(key.equals("port")){
							argMap.put(key, extDialog.getPort());
						}
					}

					ILaunchConfigurationWorkingCopy config = l.getLaunchConfiguration().getWorkingCopy();
					config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, argMap);
					ILaunchConfiguration attachConfig = config.doSave();
					attachConfig.launch(ILaunchManager.DEBUG_MODE, null);

				} catch (CoreException e) {
					e.printStackTrace();
				}
				break;
			}
		}

		// *** Pour supprimer un Launch
		// DebugPlugin.getDefault().getLaunchManager().removeLaunch(ILaunch);
		// DebugPlugin.getDefault().getLaunchManager().addLaunch(ILaunch);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
