package functionalTests.activeobject.webservices;

import java.io.Serializable;


public class Couple implements Serializable {
    private String str1;
    private int myInt;

    public Couple() {
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str) {
        this.str1 = str;
    }

    public int getMyInt() {
        return myInt;
    }

    public void setMyInt(int myInt) {
        this.myInt = myInt;
    }
}
