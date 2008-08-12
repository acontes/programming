package functionalTests.annotations.activeobject.transformation.inputs;

import org.objectweb.proactive.annotation.activeobject.ActiveObject;

public class AcceptBasic {

	void test() {
		
		@ActiveObject
		String str = new String();
		
		{
			@ActiveObject
			java.lang.StringBuffer strBuf = new java.lang.StringBuffer();
		}
		
	}
	
	void test2() {
		@org.objectweb.proactive.annotation.activeobject.ActiveObject
		java.math.BigDecimal number = new java.math.BigDecimal(123); 
	}
	
	private int alttest() {
		
		int nbActiveObjects = 0;
		
		@ActiveObject
		String toto = new String();
		
		nbActiveObjects++;
		
		@ActiveObject
		String tata = new String();
		
		nbActiveObjects++;
		
		return nbActiveObjects;
		
	}
	
}
