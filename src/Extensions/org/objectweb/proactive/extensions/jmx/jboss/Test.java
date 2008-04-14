package org.objectweb.proactive.extensions.jmx.jboss;

import org.objectweb.proactive.core.util.wrapper.StringWrapper;

public class Test {

	public Test() {	}
	
	public void sayShit( StringWrapper msg ) {
		System.out.println(msg.stringValue());
	}
}
