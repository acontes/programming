package functionalTests.annotations.activeobject.inputs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.naming.directory.Attribute;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

class SerialParam implements Serializable {}

class Beta extends SerialParam{}

@ActiveObject
public class ErrorMethodArgsNotSerializable {

	// Integer extends Number implements Serializable
	public void doSomething(Integer in) {}

	// String implements Serializable
	void doSomething2(String str) {}
	
	// interface javax.naming.directory.Attribute extends Serializable
	// SerialParam implements Serializable
	private void doSomething3(Attribute attr, SerialParam sp ) {}
	
	// Beta extends SerialParam implements Serializable
	public void doSomethingg(Beta b) {}
	
	// FileInputStream NOT serializable
	public void readInputFromFile(FileInputStream in) throws IOException {}
	
}
