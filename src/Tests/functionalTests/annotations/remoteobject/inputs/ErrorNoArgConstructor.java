package functionalTests.annotations.remoteobject.inputs;

import java.io.Serializable;

import org.objectweb.proactive.extensions.annotation.remoteobject.RemoteObject;


@RemoteObject
public class ErrorNoArgConstructor implements Serializable {

    public ErrorNoArgConstructor(int n) {
    }
}
