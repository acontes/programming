package functionalTests.annotations.activeobject.transformation.inputs;

import org.apache.log4j.Logger;
import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.virtualnode.VirtualNode;
import org.objectweb.proactive.core.util.log.Loggers;

public class VirtualNodeAccept {
	
	private static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS); 

	void test() {
		
		@VirtualNode(name="workers",descriptorFile="ceva.xml",descriptorType="gcm")
		@ActiveObject(logger="_logger")
		String str = new String();
		
	}
	
	// the descriptorType parameter is optional 
	void test2() {
		@VirtualNode(name="workers",descriptorFile="ceva.xml")
		@ActiveObject(logger="_logger")
		String str = new String();
	}
}
