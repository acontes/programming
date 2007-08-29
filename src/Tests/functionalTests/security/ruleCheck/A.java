package functionalTests.security.ruleCheck;

import java.io.File;
import java.io.Serializable;


public class A implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6487329851736398592L;

	public A() {
		// mandatory empty comstructor
    }

    public C doStuff() {
        System.out.println("\n2-Your java.home property: " +
            System.getProperty("java.home"));

        System.out.println("\n2-Your user.home property: " +
            System.getProperty("user.home"));

        File f = new File("foo.txt");
        System.out.print("\n2-foo.txt does ");
        if (!f.exists()) {
            System.out.print("not ");
        }
        System.out.println("exist in the current working directory.");

        return new C("42");
    }
}
