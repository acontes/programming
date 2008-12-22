package org.objectweb.proactive.extra.vfs.provider;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;

public class PAClient {
	PAFileProviderEngine proxy = null;
	public void listChildren() {
		
	}
	
	public void createConnection (String url) {
		try {
			proxy = (PAFileProviderEngine)PAActiveObject.lookupActive(PAFileProviderEngine.class.getName(), url);
		} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void printPAVFSClient () {
		proxy.getVFSObjects();
		proxy.printPAVFS();
	}

	public PAFile[] listFiles(String relPath) {
		
		/*try {
			System.out.println("Node of Proxy "+PAActiveObject.getActiveObjectNode(proxy).getNodeInformation().getURL());
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return proxy.getFiles(relPath);
		//return null;
	}

	public boolean removeDirectory(String relPath) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteFile(String relPath) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean rename(String oldName, String newName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean makeDirectory(String relPath) {
		return proxy.makeDirectory(relPath);
	}

	public InputStream readDataClient(String relPath) {

		return proxy.readData(relPath);
	}

	public boolean isConnectionOKClient() {
		// TODO Auto-generated method stub
		return proxy.isConnectionOK();
	}
}
