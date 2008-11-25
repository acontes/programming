package functionalTests.annotations.activeobject.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;


@ActiveObject
public class PrivateEmptyConstructor {
    private PrivateEmptyConstructor() {
    }

    public PrivateEmptyConstructor(String s) {
    }
}
