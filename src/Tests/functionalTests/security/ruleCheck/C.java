package functionalTests.security.ruleCheck;

import java.io.Serializable;


public class C implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3835590934233235875L;
	private String v;

    public C(String s) {
        v = new String(s);
    }

    public String get() {
        return v;
    }
}
