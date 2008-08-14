package functionalTests.annotations.activeobject.transformation.inputs;

import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.virtualnode.VirtualNode;

public class VirtualActiveReject {

	// virtual node name differs between ActiveObject and VirtualNode annotations
	void test() {
		@VirtualNode(name="workers",descriptorFile="ceva.xml",descriptorType="gcm")
		@ActiveObject(logger="_logger",virtualNode="wokers")
		String str = new String();
	}
	
}
