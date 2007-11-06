package functionalTests.security.ruleCheck;

import java.io.Serializable;


public class SerializableString implements Serializable {

    /**
         *
         */
    private static final long serialVersionUID = -3835590934233235875L;
    private String v;

    public SerializableString() {
        this.v = "";
    }

    public SerializableString(String s) {
        this.v = new String(s);
    }

    public String get() {
        return this.v;
    }

    @Override
    public String toString() {
        return this.v;
    }
}
