package functionalTests.security.ruleCheck;

import java.io.Serializable;


public class B implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8714694281212951243L;
	public B() {
		// mandatory empty comstructor
    };
    
    
    public C makeADoStuff(A a) {
        return a.doStuff();
    }
}
