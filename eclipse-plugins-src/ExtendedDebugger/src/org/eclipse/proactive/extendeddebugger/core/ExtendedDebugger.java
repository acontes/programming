package org.eclipse.proactive.extendeddebugger.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.management.JMX;
import javax.management.ObjectName;

import org.eclipse.proactive.extendeddebugger.core.listener.ExtendedDebuggerNotificationListener;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;


public class ExtendedDebugger extends Thread{

	private ProActiveConnection proActiveConnection;
	private ArrayList<String> RuntimeURLs = new ArrayList<String>();

	public ExtendedDebugger(ProActiveConnection proActiveConnection, String runtimeURL) {
		this.proActiveConnection = proActiveConnection;
		this.RuntimeURLs.add(runtimeURL);
	}

	public void run(){
		try {
			// Get Mbeans
			Set<ObjectName> names =
				new TreeSet<ObjectName>(proActiveConnection.queryNames(null, null));
			ArrayList<ObjectName> bodyNames = new ArrayList<ObjectName>();
			for (ObjectName name : names) {
				if(name.getDomain().equals("org.objectweb.proactive.core.body")){
					bodyNames.add(name);
				}
			}
			ExtendedDebuggerNotificationListener listener = new ExtendedDebuggerNotificationListener();
			ArrayList<BodyWrapperMBean> bodyWrapperMBeans = new ArrayList<BodyWrapperMBean>();
			for(ObjectName bodyName : bodyNames){
				JMXNotificationManager.getInstance().subscribe(bodyName, listener,
						RuntimeURLs.get(0));
				bodyWrapperMBeans.add(JMX.newMBeanProxy(proActiveConnection, bodyName, BodyWrapperMBean.class));
			}

			// enable extendedDebugger
			for(BodyWrapperMBean bodyWrapperMBean : bodyWrapperMBeans){
				System.out.println("enable ExtendedDebugger for: " + bodyWrapperMBean.getNodeUrl());
				bodyWrapperMBean.enableExtendedDebugger();
			}

			// show informations
			URI newURI = null;
			for(BodyWrapperMBean bodyWrapperMBean : bodyWrapperMBeans){
				String runtimeURL = bodyWrapperMBean.getRumtimeInfo();
				if(!RuntimeURLs.contains(runtimeURL)){
					newURI = new URI(runtimeURL);
					System.out.println("************* new URI found: " + runtimeURL);
					break;
				}
			}

			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
