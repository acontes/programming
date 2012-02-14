package org.objectweb.proactive.examples.binarytree_multiactive;

import java.io.Serializable;

public class CustomValue implements Serializable {

	private String v;

	
	public CustomValue() {
		
	}
	
	public CustomValue(String v) {
		super();
		this.v = v;
	} 
	
	
	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	
	public String toString() {
		return v;
	}
	
	
}
