package functionalTests.annotations.apt.activeobject.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

@ActiveObject
public class ErrorFinalFields {
	
	final String finalString = "tata";
	
}
