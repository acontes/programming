package functionalTests.annotations.remoteobject.inputs;

import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;

public class MisplacedAnnotation {

	@RemoteObject
	private String field;
}
