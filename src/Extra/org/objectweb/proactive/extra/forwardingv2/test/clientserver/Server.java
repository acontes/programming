package org.objectweb.proactive.extra.forwardingv2.test.clientserver;

import java.io.IOException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;

public class Server {
	
	public Server() {
		
	}
	
	public void say(String toto){
		System.out.println("say: "+toto);
	}

	/**
	 * @param args
	 * @throws NodeException 
	 * @throws ActiveObjectCreationException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ActiveObjectCreationException, 
		NodeException, IOException {
		Server server = (Server) PAActiveObject.newActive(Server.class.getName(), null);
		PAActiveObject.register(server, PAActiveObject.getActiveObjectNodeUrl(server)+"/Server");
		System.out.println("Server registered as "+PAActiveObject.getActiveObjectNodeUrl(server)+"/Server");
		System.in.read();
		ProActiveRuntimeImpl.getProActiveRuntime().killRT(true);
	}

}
