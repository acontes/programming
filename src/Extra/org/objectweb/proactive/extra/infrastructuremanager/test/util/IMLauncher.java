package org.objectweb.proactive.extra.infrastructuremanager.test.util;


import org.objectweb.proactive.extra.infrastructuremanager.IMFactory;

public class IMLauncher {

	/**
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		IMFactory.startLocal();
		System.in.read();
		IMFactory.getAdmin().shutdown();
	}

}
