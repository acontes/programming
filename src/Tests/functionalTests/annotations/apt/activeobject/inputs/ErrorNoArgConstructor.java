package functionalTests.annotations.apt.activeobject.inputs;

import java.io.Serializable;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

@ActiveObject
public class ErrorNoArgConstructor implements Serializable{

	public ErrorNoArgConstructor(int n) {
	}
}
