package functionalTests.annotations.activeobject.transformation.inputs;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.virtualnode.VirtualNode;

public class VirtualNodeAccept {
	
	private static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS); 

	void test() {
		
		@VirtualNode(name="workers",descriptorFile="ceva.xml",descriptorType="old")
		@ActiveObject(logger="_logger")
		String str = new String();
		
	}
	
	// the descriptorType parameter is optional 
	void test2() {
		@VirtualNode(name="workers", descriptorFile="ceva.xml",logger="_logger")
		@ActiveObject(logger="_logger")
		String str = new String();
	}
}
