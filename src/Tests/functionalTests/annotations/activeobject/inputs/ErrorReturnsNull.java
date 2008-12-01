package functionalTests.annotations.activeobject.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;


@ActiveObject
public class ErrorReturnsNull {

    // error - active object method returning null
    public String testing() {
        return null;
    }

    private String testing2() {
        return null;
    }
}
