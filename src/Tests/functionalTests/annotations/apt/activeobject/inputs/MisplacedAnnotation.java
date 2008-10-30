package functionalTests.annotations.apt.activeobject.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

public class MisplacedAnnotation {

	@ActiveObject
	private String field;
}
