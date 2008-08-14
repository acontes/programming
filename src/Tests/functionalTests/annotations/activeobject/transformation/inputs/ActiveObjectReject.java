package functionalTests.annotations.activeobject.transformation.inputs;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.objectweb.proactive.annotation.activeobject.ActiveObject;

public class ActiveObjectReject {

	void testNoNew() throws UnknownHostException {
		
		// error - no call to operator new
		@ActiveObject
		String str;
		
		@org.objectweb.proactive.annotation.activeobject.ActiveObject
		String papa = "papa";
		
		@ActiveObject
		String hostName = InetAddress.getLocalHost().getHostName();
		
		@ActiveObject
		String buff;
		
	}
}
