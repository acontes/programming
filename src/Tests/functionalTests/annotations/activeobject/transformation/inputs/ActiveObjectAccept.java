package functionalTests.annotations.activeobject.transformation.inputs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.virtualnode.VirtualNode;

public class ActiveObjectAccept {
	
	private static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS); 

	void test() {
		
		@ActiveObject(logger="_logger")
		String str = new String();
		
	}
	
	void test2() {
		
		test();
		
		@org.objectweb.proactive.extra.annotation.activeobject.ActiveObject(logger="_logger")
		java.math.BigDecimal number = new java.math.BigDecimal(123); 
	}
	
	void test3() {
		
		test();
		test2();
		
		@ActiveObject
		java.lang.StringBuffer strBuf = new java.lang.StringBuffer(20);	
	}
	
	void test4() {
		
		try {
			Writer blackOut
			= new BufferedWriter(
					new OutputStreamWriter(System.out));

			@ActiveObject
			ActiveObjectAccept basic = new ActiveObjectAccept();

			blackOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int alttest() {
		
		int nbActiveObjects = 0;
		
		@ActiveObject
		String toto = new String("toto");
		
		nbActiveObjects++;
		
		@ActiveObject(logger="_logger")
		String tata = new String("tata");
		
		nbActiveObjects++;
		
		return nbActiveObjects;
		
	}
	
}
