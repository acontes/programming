package org.objectweb.proactive.extra.forwardingv2.test.clientserver;

import java.io.IOException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;

public class Client {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ActiveObjectCreationException 
	 */
	public static void main(String[] args) throws ActiveObjectCreationException, IOException {
		Server server = (Server) PAActiveObject.lookupActive(Server.class.getName(), args[0]);
		System.out.println("Saying '"+args[1]+"'");
		server.say(args[1]);
		ProActiveRuntimeImpl.getProActiveRuntime().killRT(true);
	}

}
