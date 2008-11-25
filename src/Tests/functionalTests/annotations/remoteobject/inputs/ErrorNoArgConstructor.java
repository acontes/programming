package functionalTests.annotations.remoteobject.inputs;

import java.io.Serializable;

import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;


@RemoteObject
public class ErrorNoArgConstructor implements Serializable {

    public ErrorNoArgConstructor(int n) {
    }
}
