package functionalTests.annotations.remoteobject.inputs;

import java.io.Serializable;

import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;

class UnknownExtendedClass extends UnknownClass {}

interface UnknownExtendedInterface extends UnknownInterface{}

class UnknownImplementedInterface implements UnknownInterface, KnownInterface  {}

interface KnownInterface extends Serializable {}

//interface UnknownInterface {
//	blabla
//}

@RemoteObject
public class ErrorMissingTypes {

	public ErrorMissingTypes() {
	}
	
	// error! unknown type
	public ErrorMissingTypes(UnknownClass param){
		
	}
	
	// ERR unknown type.maybe the superclass implements Serializable?
	void someMethod(UnknownExtendedClass param) {}

	// OK - the KnownInterface implements Serializable. don't care about other undefined types
	void anotherMethod( UnknownImplementedInterface param ) {}
	
	// ERR unknown type - maybe UnknownInterface implements Serializable?
	// cannot be tested by the Mirror API - unknown interface definitions do not appear when calling getSuperinterfaces()
	public void external( UnknownExtendedInterface param ) {}
	
}
