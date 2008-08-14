package functionalTests.annotations.activeobject.transformation.inputs;

import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.virtualnode.VirtualNode;

public class VirtualActiveAccept {
	void test() {
		@VirtualNode(name="workers",descriptorFile="ceva.xml",descriptorType="gcm")
		@ActiveObject(logger="_logger",virtualNode="workers")
		String str = new String();
	}
}
