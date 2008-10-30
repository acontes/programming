package functionalTests.annotations.ctree.activeobject.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

// 1 error - constructor is not empty
@ActiveObject
public class ErrorEmptyConstructor {

	public ErrorEmptyConstructor() {
		String tata="mama";
		tata.substring(3);
	}
}
