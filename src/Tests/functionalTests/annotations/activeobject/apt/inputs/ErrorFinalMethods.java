package functionalTests.annotations.activeobject.apt.inputs;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

@ActiveObject
public class ErrorFinalMethods{

	public final void doSomething() {}
}
