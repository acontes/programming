package org.objectweb.proactive.extra.vfs_akapur.PAProvider;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemOptions;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.vfs.MountedNodes;

/*
 * An instance of PAClient
 */

public class PAClientFactory {
 
	
	private PAClientFactory()
	{
		
	}
	
	
	/**
	 * Looks up the PAFileProviderEngine(an Active Object) with a specific URL
	 * 
	 * @param Scheme
	 * @param hostname
	 * @param port
	 * @param path
	 * @return
	 * @throws ActiveObjectCreationException
	 * @throws IOException
	 */
	
	public static PAFileProviderEngine createConnection(String Scheme,String hostname,int port,String path, Node node) throws ActiveObjectCreationException, IOException
	
	{	
			
		String url = Scheme + "://" + hostname + ":" + port + "/" + node.getNodeInformation().getName();
				
		PAFileProviderEngine serverRef = (PAFileProviderEngine)PAActiveObject.lookupActive(PAFileProviderEngine.class.getName(), url);
									
		return serverRef;
		
	}
	
	public void listFiles(Node node)
	{
		
	}
		
	
}
