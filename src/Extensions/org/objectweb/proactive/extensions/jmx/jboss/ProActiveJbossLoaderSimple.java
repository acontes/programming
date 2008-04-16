package org.objectweb.proactive.extensions.jmx.jboss;

import java.net.InetAddress;
import java.net.URI;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import org.jboss.system.ServiceMBeanSupport;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Job;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.StartNode;
import org.objectweb.proactive.core.rmi.RegistryHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.URIBuilder;

public class ProActiveJbossLoaderSimple extends ServiceMBeanSupport implements
		ProActiveJbossLoaderSimpleMBean {
	
	private ProActiveRuntimeImpl _proActiveRuntime;
	private String _localNodeUrl;

	
	/**
	 * starts a ProActive runtime within JBoss AS
	 * @see org.objectweb.proactive.core.runtime.StartRuntime.run()
	 * @see org.objectweb.proactive.p2p.service.StartP2PService.start()
	 */
	private void startProActiveRuntime() throws NodeException, ActiveObjectCreationException {

		_proActiveRuntime = ProActiveRuntimeImpl.getProActiveRuntime();
		_proActiveRuntime.setVMName(_vmName);
		getLog().debug("New PART created succesfully at URL:" + _proActiveRuntime.getMBean().getURL());

		// get params
		String commProtocol = PAProperties.PA_COMMUNICATION_PROTOCOL.getValue();
		String rmiPort = PAProperties.PA_RMI_PORT.getValue();
		getLog().debug("Communication protocol: " + commProtocol + ";port number:" + rmiPort + ";p2p node name:" + _nodeName );

		int portNumer = Integer.parseInt(rmiPort);

		try {
			// if no registry started on port rmiPort , now it's the time to start it!
			getOrCreateRegistry(portNumer);
			
			// get the hostname
			InetAddress netAddr =  ProActiveInet.getInstance().getInetAddress();
			String hostName = netAddr.getHostName();

			// create local node
			URI nodeURI = URIBuilder.buildURI( hostName , _nodeName,
					commProtocol, portNumer );
			getLog().debug( "Creating a local node on nodeURI = " + nodeURI );
			_localNodeUrl = _proActiveRuntime.createLocalNode( nodeURI.toString() , false , null, _vmName, 
					Job.DEFAULT_JOBID);
			getLog().debug( "Local node created!" );
		} catch (AlreadyBoundException e) {
			getLog().warn("This name " + _nodeName + " is already bound in the registry ", e);
		} catch (RemoteException e) {
			getLog().error( " I could neither locate nor create a rmiregistry " +
					"on localhost , port " + portNumer );
			getLog().error( e.getMessage() , e );
		}

	}
	
	private void getOrCreateRegistry(int portNumber) throws RemoteException {
		RegistryHelper registryHelper = new RegistryHelper();
		registryHelper.setRegistryPortNumber(portNumber);
		registryHelper.initializeRegistry();
	}

	
	/**
	 * stops the ProActiveRuntime started within the Application Server
	 */
	private void stopProActiveRuntime() {
		// kill the created node
		_proActiveRuntime.killNode(_nodeName);
		// JMX Notification
        if (_proActiveRuntime.getMBean() != null) {
            _proActiveRuntime.getMBean().sendNotification(NotificationType.runtimeDestroyed);
        }
	}
	
	@Override
	protected void createService() throws Exception {
		log.info("Creating service " + serviceName.getCanonicalName());
		// configure ProActive separate logging
		configureLogging();
		
	}

	private void configureLogging() {
		// override the default log4j initialization phase		
		System.setProperty("log4j.defaultInitOverride", "true");
		System.setProperty("log4j.configuration", _log4jConfigFile ); 		
	 }
	
	@Override
	protected void startService() throws Exception {
		log.info("Starting the service " + serviceName.getCanonicalName());
		startProActiveRuntime();
		
		//StartNode.main( new String[] { _nodeName } );
	}

	@Override
	protected void stopService() throws Exception {
		log.info("Stopping the service " + serviceName.getCanonicalName());
		
		stopProActiveRuntime();
	}
	
	////////// 		MBean 	config 	params
	private String _vmName;
	private String _log4jConfigFile;
	private String _nodeName;
	
	@Override
	public String getLog4jConfigFile() {
		return _log4jConfigFile;
	}

	@Override
	public String getProActiveRuntimeURL() {
		return _proActiveRuntime.getURL().toString();
	}

	@Override
	public String getvmName() {
		return _vmName;
	}

	@Override
	public void setLog4jConfigFile(String configFile) {
		_log4jConfigFile = configFile;
	}

	@Override
	public void setvmName(String vmName) {
		_vmName = vmName;
	}
	
	@Override
	public String getProActiveNodeUrl(){
		return _localNodeUrl;
	}

	@Override
	public String getNodeName() {
		return _nodeName;
	}

	@Override
	public void setNodeName(String nodeName) {
		_nodeName = nodeName; 
	}
}
