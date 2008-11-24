package functionalTests.annotations.activeobject.inputs;

import java.io.Serializable;

// Java types available only @runtime
import javassist.ClassPath; // UnknownInterface
import javassist.bytecode.CodeIterator; // UNknownClass

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

class UnknownExtendedClass extends CodeIterator {} // unknown class @Compile time

interface UnknownExtendedInterface extends ClassPath{}

class UnknownImplementedInterface implements ClassPath, KnownInterface  {}

interface KnownInterface extends Serializable {}

//interface UnknownInterface {
//	blabla
//}

@ActiveObject
public class ErrorMissingTypes {

	public ErrorMissingTypes() {
	}
	
	// error! unknown type
	public ErrorMissingTypes(CodeIterator param){
		
	}
	
	// ERR unknown type.maybe the superclass implements Serializable?
	void someMethod(UnknownExtendedClass param) {}

	// OK - the KnownInterface implements Serializable. don't care about other undefined types
	void anotherMethod( UnknownImplementedInterface param ) {}
	
	// ERR unknown type - maybe UnknownInterface implements Serializable?
	// cannot be tested by the Mirror API - unknown interface definitions do not appear when calling getSuperinterfaces()
	public void external( UnknownExtendedInterface param ) {}
	
}
