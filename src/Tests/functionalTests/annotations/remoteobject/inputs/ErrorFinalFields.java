package functionalTests.annotations.remoteobject.inputs;

import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;

@RemoteObject
public class ErrorFinalFields {

	final String finalString = "tata";
	private final String finalString2 = "tata";
	protected final String finalString3 = "tata";
	public final String finalString4= "tata";

}
