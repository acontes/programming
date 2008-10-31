package functionalTests.annotations.apt.activeobject.inputs;

import java.io.Serializable;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

@ActiveObject
public class WarningGettersSetters implements Serializable{

	public int error;
	
	// OK, should recognize coding conventions
	public int _counter;
	public void setCounter(IntWrapper counter) { _counter = counter.intValue(); }
	public IntWrapper getCounter() { return new IntWrapper(_counter); }
	
	// OK, should be case-sensitive
	public String name;
	public StringWrapper getname() {return new StringWrapper(name);}
	public void setName(String name) { }
	
	// OK
	String test;
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	// OK
	String _test;
	public String get_test() {
		return _test;
	}

	public void set_test(String _test) {
		this._test = _test;
	}
}
