package org.eclipse.proactive.extendeddebugger.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.management.JMX;
import javax.management.ObjectName;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.proactive.extendeddebugger.core.listener.ExtendedDebuggerNotificationListener;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.debug.dconnection.DebuggerInformation;
import org.objectweb.proactive.core.debug.stepbystep.RemoteDebugInfo;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;

import com.sun.jdi.connect.Connector;

public class ExtendedDebugger{

	private List<URI> RuntimeURLs = new ArrayList<URI>();
	private List<BodyWrapperMBean> bodyWrapperMBeans = new ArrayList<BodyWrapperMBean>();
	private DebuggerInformation nodeInfo = null;

	public ExtendedDebugger(URI runtimeURL) {
		addRuntime(runtimeURL);
	}

	public void connectDebugger(int port){
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
							argMap.put(key, "localhost");
						}
						if(key.equals("port")){
							argMap.put(key, "" + port);
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
	}

	public void RecievedNotification(RemoteDebugInfo rdi){
		String runtimeURL = rdi.getRemoteRuntimeURL();
		try {
			URI runtimeURI = new URI(runtimeURL);
			if(!RuntimeURLs.contains(runtimeURI)){
				System.out.println("***New Runtime found!");
				addRuntime(runtimeURI);
				RemoteConnection remoteConnection = new RemoteConnection(runtimeURI);
				remoteConnection.createTunneling();
				connectDebugger(remoteConnection.getDsc().getServer().getPort());
			}
			for(BodyWrapperMBean bwmb : bodyWrapperMBeans){
				if(bwmb.getID().equals(rdi.getId())){
					System.out.println("AO unblocked!");
					bwmb.unblockConnection();
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}

		// print Runtimes
		System.out.println("\n**************************************************");
		for(URI url : RuntimeURLs){
			System.out.println("* url: " + url);
		}
		System.out.println("**************************************************\n");
	}

	public void addRuntime(URI runtimeURL){
		try{
			RuntimeURLs.add(runtimeURL);

			// get a proactiveConnection for the new uri
			RemoteConnection remote = new RemoteConnection(runtimeURL);
			ProActiveConnection proActiveConnection = remote.getProActiveConnection();

			// Get Mbeans
			Set<ObjectName> names =
				new TreeSet<ObjectName>(proActiveConnection.queryNames(null, null));
			ArrayList<ObjectName> bodyNames = new ArrayList<ObjectName>();
			for (ObjectName name : names) {
				if(name.getDomain().equals("org.objectweb.proactive.core.body")){
					bodyNames.add(name);
				}
			}

			// add jmx listener
			ExtendedDebuggerNotificationListener listener = new ExtendedDebuggerNotificationListener(this);
			for(ObjectName bodyName : bodyNames){
				JMXNotificationManager.getInstance().subscribe(bodyName, listener,
						runtimeURL.toString());
				BodyWrapperMBean bwmb = JMX.newMBeanProxy(proActiveConnection, bodyName, BodyWrapperMBean.class);
				// enable the ExtendedDebugger for this AO
				bwmb.enableExtendedDebugger();
				bodyWrapperMBeans.add(bwmb);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}

	}
}
