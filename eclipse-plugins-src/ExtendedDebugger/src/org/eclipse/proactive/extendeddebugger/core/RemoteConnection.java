package org.eclipse.proactive.extendeddebugger.core;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import javax.management.JMX;
import javax.management.ObjectName;

import org.eclipse.proactive.extendeddebugger.core.tunneling.DebugSocketConnection;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.client.ClientConnector;
import org.objectweb.proactive.core.jmx.mbean.ProActiveRuntimeWrapperMBean;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;

public class RemoteConnection {

	private URI runtimeURI;
	private ProActiveConnection proActiveConnection = null;
	private DebugSocketConnection dsc = null;

	/**
	 * @param runtimeURI, The runtime URL of the Debuggee
	 */
	public RemoteConnection(URI runtimeURI) {
		this.runtimeURI = runtimeURI;
	}

	/**
	 * Get a ProActiveRuntimeWrapperMBean to establish the tunneling connectio
	 */
	public void createTunneling() throws ProActiveException{
		try {
			proActiveConnection = getProActiveConnection();
			// Get Mbeans
			Set<ObjectName> names = new TreeSet<ObjectName>(proActiveConnection
					.queryNames(null, null));
			ArrayList<ObjectName> bodyNames = new ArrayList<ObjectName>();
			ProActiveRuntimeWrapperMBean parwmb = null;
			for (ObjectName name : names) {
				if (name.getDomain().equals(
						"org.objectweb.proactive.core.runtimes")) {
					parwmb = JMX.newMBeanProxy(proActiveConnection, name,
							ProActiveRuntimeWrapperMBean.class);
					break;
				}
			}
			// tunneling creation
			dsc = new DebugSocketConnection(parwmb);
			dsc.connectSocketDebugger();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return ProActiveConnection, a ProActive jmx connection
	 * @throws ProActiveException
	 */
	public ProActiveConnection getProActiveConnection()
			throws ProActiveException {
		if (proActiveConnection == null) {
			RemoteObject<?> remoteObject = null;
			Object stub = null;
			try {
				remoteObject = RemoteObjectHelper.lookup(runtimeURI);
				stub = RemoteObjectHelper.generatedObjectStub(remoteObject);
				if (stub instanceof ProActiveRuntime) {
					// Active the Remote JMX Server Connector
					ProActiveRuntime proActiveRuntime = (ProActiveRuntime) stub;
					proActiveRuntime.startJMXServerConnector();

					// Create a new connection
					ClientConnector cc = new ClientConnector(runtimeURI
							.toString(), FactoryName
							.getJMXServerName(runtimeURI));
					cc.connect();

					// Connect to the remote JMX Server Connector
					proActiveConnection = cc.getConnection();
				} else {
					throw new ProActiveException(
							"Can't create a JMX/ProActive connection: the object is not an instance of ProActiveRuntime");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return proActiveConnection;
	}
	
	public DebugSocketConnection getDsc() {
		return dsc;
	}

}
