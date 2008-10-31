package functionalTests.annotations.activeobject.inputs;

import java.util.regex.Matcher;

import javax.naming.directory.Attribute;
import java.io.Serializable;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

interface SerTest extends Serializable {}

@ActiveObject
public class ErrorConstructorArgsNotSerializable {

	public ErrorConstructorArgsNotSerializable() {
		// TODO Auto-generated constructor stub
	}

	// OK String serializable
	public ErrorConstructorArgsNotSerializable(String str){
		
	}
	
	// ERR INteger not serializable
	public ErrorConstructorArgsNotSerializable(Integer str){
		
	}
	
	// ERR Matcher
	public ErrorConstructorArgsNotSerializable(Matcher str,StringBuilder blah){
		
	}
	
	// this one should pass!
	public ErrorConstructorArgsNotSerializable(Attribute attr) { }
	
	// this one too! 
	public ErrorConstructorArgsNotSerializable(SerTest test) {}
	
}


