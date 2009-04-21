package org.eclipse.proactive.extendeddebugger.ui.actions;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.proactive.extendeddebugger.core.DebugInfoSingleton;
import org.eclipse.proactive.extendeddebugger.core.ExtendedDebugger;
import org.eclipse.proactive.extendeddebugger.core.RemoteConnection;
import org.eclipse.proactive.extendeddebugger.ui.dialog.DebuggerBranchInfoDialog;
import org.eclipse.proactive.extendeddebugger.ui.dialog.ErrorDialog;
import org.eclipse.proactive.extendeddebugger.ui.dialog.ExtendDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.objectweb.proactive.core.ProActiveException;


public class TunnelingConnexionAction implements IViewActionDelegate {

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
			ExtendDialog extDialog = new ExtendDialog(window.getShell());
			URI runtimeURI = new URI(extDialog.getUrl());
			DebugInfoSingleton info = DebugInfoSingleton.getInstance();
			info.setRuntimURL(runtimeURI);
			RemoteConnection remoteConnection = new RemoteConnection(runtimeURI);
			remoteConnection.createTunneling();
			int port = remoteConnection.getDsc().getServer().getPort();
            new DebuggerBranchInfoDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                port);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			new ErrorDialog(window.getShell());
		}
		
		//		ExtendedDebugger extendedDebugger = new ExtendedDebugger(extDialog.getUrl());

		/*
		try{
			ExtendDialog extDialog = new ExtendDialog(window.getShell());
			URI runtimeURI = new URI(extDialog.getUrl());
			RemoteConnection remote = new RemoteConnection(runtimeURI);
			ProActiveConnection proActiveConnection = remote.getProActiveConnection();

			// Get Mbeans
			Set<ObjectName> names =
				new TreeSet<ObjectName>(proActiveConnection.queryNames(null, null));
			ArrayList<ObjectName> bodyNames = new ArrayList<ObjectName>();
			ProActiveRuntimeWrapperMBean parwmb = null;
			for (ObjectName name : names) {
				if(name.getDomain().equals("org.objectweb.proactive.core.runtimes")){
					parwmb = JMX.newMBeanProxy(proActiveConnection, name, ProActiveRuntimeWrapperMBean.class);
					break;
				}
			}
			// tunneling creation
			System.out.println("BEGIN======================================");
			DebugSocketConnection dsc = new DebugSocketConnection(parwmb);
			dsc.connectSocketDebugger();
			System.out.println("END======================================");
		} catch(Exception e){
			e.printStackTrace();
		}
		 */
		// FOR ADDING A NEW REMOTE CONNECTION	
		// verifier qu'on a le bon launch (Debug)
		/*
		ExtendDialog extDialog = new ExtendDialog(window.getShell());
		for(ILaunch l : DebugPlugin.getDefault().getLaunchManager().getLaunches()){
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
		 */
		// *** Pour supprimer un Launch
		// DebugPlugin.getDefault().getLaunchManager().removeLaunch(ILaunch);
		// DebugPlugin.getDefault().getLaunchManager().addLaunch(ILaunch);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
