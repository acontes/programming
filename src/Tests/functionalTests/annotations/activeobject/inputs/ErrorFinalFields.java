package functionalTests.annotations.activeobject.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

@ActiveObject
public class ErrorFinalFields {
	
	final String finalString = "tata";
	
}
