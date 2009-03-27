package org.eclipse.proactive.extendeddebugger.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.management.JMX;
import javax.management.ObjectName;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.internal.core.LaunchManager;
import org.eclipse.proactive.extendeddebugger.core.listener.ExtendedDebuggerNotificationListener;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.debug.stepbystep.RemoteDebugInfo;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;

public class ExtendedDebugger{

	private List<String> RuntimeURLs = new ArrayList<String>();
	private List<BodyWrapperMBean> bodyWrapperMBeans = new ArrayList<BodyWrapperMBean>();

	public ExtendedDebugger(String runtimeURL) {
		addRuntime(runtimeURL);
	}

	public void RecievedNotification(RemoteDebugInfo rdi){
		String runtimeURL = rdi.getRemoteRuntimeURL();
		if(!RuntimeURLs.contains(runtimeURL)){
			//mode ILaunchManager.DEBUG_MODE
			
			
//			ILaunchConfiguration config = new LaunchConfiguration	
//			DebugUITools.launch(configuration, mode);
//			DebugUITools.
//			addRuntime(runtimeURL);
//			for(BodyWrapperMBean bodyWrapperMBean : bodyWrapperMBeans){
//				if(bodyWrapperMBean.getID().equals(rdi.getId())){
//					System.out.println("are equal!");
//					bodyWrapperMBean.unblockConnection();
//				}
//			}
		}

		// print Runtimes
		System.out.println("\n************************************************");
		for(String url : RuntimeURLs){
			System.out.println("* url: " + url);
		}
		System.out.println("************************************************\n");
	}

	public void addRuntime(String runtimeURL){
		try{
			RuntimeURLs.add(runtimeURL);
			
			// get a proactiveConnection for the new uri
			URI runtimeURI = new URI(runtimeURL);
			RemoteConnection remote = new RemoteConnection(runtimeURI);
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
						runtimeURL);
				BodyWrapperMBean bwmb = JMX.newMBeanProxy(proActiveConnection, bodyName, BodyWrapperMBean.class);
				// enable the ExtendedDebugger for this AO
				bwmb.enableExtendedDebugger();
				bodyWrapperMBeans.add(bwmb);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}

	}
}
