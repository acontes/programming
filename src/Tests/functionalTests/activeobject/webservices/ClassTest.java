package functionalTests.activeobject.webservices;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassTest implements Serializable{
	private String str1;
	private int myInt;
//	private ArrayList<Double> array;
	
	public ClassTest() {
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
