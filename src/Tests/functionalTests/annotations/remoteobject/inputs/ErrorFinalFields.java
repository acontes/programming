package functionalTests.annotations.remoteobject.inputs;

import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;

@RemoteObject
public class ErrorFinalFields {

	final String finalString = "tata";

}
