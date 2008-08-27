package functionalTests.annotations.activeobject.apt.inputs;

import java.io.Serializable;

public class ErrorGettersSetters implements Serializable{

	public int error;
	
	// OK, should recognize coding conventions
	public int _counter;
	public void setCounter(int counter) { _counter = counter; }
	public int getCounter() { return _counter; }
	
	// OK, should be case-sensitive
	public String name;
	public String getname() {return name;}
	public void setName(String name) {}
}
