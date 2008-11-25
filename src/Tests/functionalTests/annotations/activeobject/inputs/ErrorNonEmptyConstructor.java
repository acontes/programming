package functionalTests.annotations.activeobject.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;


// 1 warning - constructor is not empty
@ActiveObject
public class ErrorNonEmptyConstructor {

    public ErrorNonEmptyConstructor() {
        String tata = "mama";
        tata.substring(3);
    }

    public ErrorNonEmptyConstructor(String z) {
    }
}
