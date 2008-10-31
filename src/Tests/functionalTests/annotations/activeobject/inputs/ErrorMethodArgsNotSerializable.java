package functionalTests.annotations.activeobject.inputs;

import java.io.Serializable;

import javax.naming.directory.Attribute;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

class SerialParam implements Serializable {}

@ActiveObject
public class ErrorMethodArgsNotSerializable {

	// ERROR
	public void doSomething(Integer int) {}

	// OK
	void doSomething2(String str) {}
	
	// OK
	private void doSomething3(Attribute attr, SerialParam sp ) {}
	
}
