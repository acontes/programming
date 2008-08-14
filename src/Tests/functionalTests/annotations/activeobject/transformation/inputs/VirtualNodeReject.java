package functionalTests.annotations.activeobject.transformation.inputs;

import org.apache.log4j.Logger;
import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.virtualnode.VirtualNode;
import org.objectweb.proactive.core.util.log.Loggers;

public class VirtualNodeReject {
	
	private static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS);

	// VirtualNode annotation without ActiveObject annotation
	void test() {
		@VirtualNode(name="workers",descriptorFile="ceva.xml",descriptorType="gcm")
		String str = new String();
	}
	
	//VirtualNode annotation parameter name missing
	void test2() {
		@VirtualNode(descriptorFile="ceva.xml",descriptorType="gcm")
		@ActiveObject(logger="_logger")
		String str = new String();
	}
	
	//VirtualNode annotation parameter descriptorFile missing
	void test3() {
		@VirtualNode(name="workers",descriptorType="gcm")
		@ActiveObject(logger="_logger")
		String str = new String();
	}

	
}
