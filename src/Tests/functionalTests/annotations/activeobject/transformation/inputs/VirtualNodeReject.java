package functionalTests.annotations.activeobject.transformation.inputs;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.virtualnode.VirtualNode;

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
	
	// invalid argument for descriptorType
	void testZ() {
		@VirtualNode(name="workers",descriptorFile="ceva.xml",descriptorType="zaza")
		String str = new String();
	}

	
}