package functionalTests.security.ruleCheck;

import java.io.Serializable;


public class SampleObject implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6487329851736398592L;
	
	private String name;

	public SampleObject() {
		// mandatory empty comstructor
    }
	
	public SampleObject(String name) {
		this.name = name;
	}

    public SerializableString doSomething() {
        System.out.println(this.name + " is doing something.");

        return new SerializableString(this.name + " did something and returned this.");
    }
    
    public void makeTargetDoSomething(SampleObject target) {
    	System.out.println(this.name + " is asking the target to do something.");
    	
    	SerializableString result = target.doSomething();
    	
    	System.out.println(this.name + " got this as result :");
    	System.out.println(">> " + result);
    }
}
