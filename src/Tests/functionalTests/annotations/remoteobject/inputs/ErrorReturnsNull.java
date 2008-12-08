package functionalTests.annotations.remoteobject.inputs;

import org.objectweb.proactive.extensions.annotation.RemoteObject;


@RemoteObject
public class ErrorReturnsNull {

    // error - active object method returning null
    public String testing() {
        return null;
    }

    private String testing2() {
        return null;
    }
}
