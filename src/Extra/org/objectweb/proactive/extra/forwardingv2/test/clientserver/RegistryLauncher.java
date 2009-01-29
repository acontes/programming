package org.objectweb.proactive.extra.forwardingv2.test.clientserver;

import java.io.IOException;

import org.objectweb.proactive.extra.forwardingv2.registry.ForwardingRegistry;

public class RegistryLauncher {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new ForwardingRegistry(6565, false);
	}

}
