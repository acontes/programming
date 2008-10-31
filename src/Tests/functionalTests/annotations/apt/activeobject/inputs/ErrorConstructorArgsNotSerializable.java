package functionalTests.annotations.apt.activeobject.inputs;

import java.util.regex.Matcher;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

@ActiveObject
public class ErrorConstructorArgsNotSerializable {

	public ErrorConstructorArgsNotSerializable() {
		// TODO Auto-generated constructor stub
	}
	
	public ErrorConstructorArgsNotSerializable(String str){
		
	}
	
	public ErrorConstructorArgsNotSerializable(Integer str){
		
	}
	
	public ErrorConstructorArgsNotSerializable(Matcher str,StringBuilder blah){
		
	}
	
}
