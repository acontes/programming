package functionalTests.annotations.activeobject.cta.inputs.reject;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

@ActiveObject
public class ErrorReturnsNull {

	// error - active object method returning null
	public String testing() { return null; }
}
