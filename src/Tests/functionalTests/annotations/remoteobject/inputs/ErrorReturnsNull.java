package functionalTests.annotations.remoteobject.inputs;

import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;


@RemoteObject
public class ErrorReturnsNull {

    // error - active object method returning null
    public String testing() {
        return null;
    }
}
