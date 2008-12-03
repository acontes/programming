package functionalTests.annotations.activeobject.inputs;

import org.objectweb.proactive.extensions.annotation.activeobject.ActiveObject;


@ActiveObject
public class PrivateEmptyConstructor {
    private PrivateEmptyConstructor() {
    }

    public PrivateEmptyConstructor(String s) {
    }
}
