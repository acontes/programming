package functionalTests.component.sca.gen;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extensions.component.sca.gen.PropertyClassGenerator;
import org.objectweb.proactive.extensions.component.sca.gen.Utils;

import functionalTests.component.sca.components.C1;



public class TestScaClassGen {
	C1 test;
	@Before
	public void setUp() throws Exception {
		 test = new C1();
		
	}
	@Test
    public void testSCASimple() throws Exception {
		System.err.println(Utils.getPropertyClassName("C1"));
		PropertyClassGenerator pcg = new PropertyClassGenerator();
		String cname = pcg.generateClass(test.getClass().getName());
		Class cla = Class.forName(cname);
		Object obj = cla.newInstance();
		java.lang.reflect.Method[] meds = obj.getClass().getMethods();
		for (int i = 0; i < meds.length; i++) {
			System.err.println("med = "+meds[i].getName());
		}
		Method setter = obj.getClass().getMethod("setX1",boolean.class);
		Method setter2 = obj.getClass().getMethod("setX2",byte.class);
		Object args[] = new Object[1];
		args[0]=true;
		setter.invoke(obj,args);
		args[0]=(byte)5;
		setter2.invoke(obj, args);
		System.err.println(obj);
	}
}
