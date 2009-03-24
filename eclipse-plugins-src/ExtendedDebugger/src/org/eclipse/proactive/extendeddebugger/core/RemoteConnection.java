package org.eclipse.proactive.extendeddebugger.core;

import java.io.IOException;
import java.net.URI;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.client.ClientConnector;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;

public class RemoteConnection {

	private URI runtimeURI;
	private ProActiveConnection proActiveConnection = null;

	public RemoteConnection(URI runtimeURI){
		this.runtimeURI = runtimeURI;
	}

	public ProActiveConnection getProActiveConnection() throws ProActiveException {
		if( proActiveConnection == null){
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
					ClientConnector cc = new ClientConnector(runtimeURI.toString(), FactoryName
							.getJMXServerName(runtimeURI));
					cc.connect();

					// Connect to the remote JMX Server Connector
					proActiveConnection = cc.getConnection();
				} else {
					throw new ProActiveException("Can't create a JMX/ProActive connection: the object is not an instance of ProActiveRuntime");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return proActiveConnection;
	}

}
