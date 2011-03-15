package functionalTests.component.sca;

import java.lang.reflect.Method;

import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.gen.IntentClassGenerator;

import functionalTests.ComponentTest;
import functionalTests.component.sca.control.components.CServer;
import functionalTests.component.sca.control.components.IntentHandlerTest;

public class TestIntentGenerator extends ComponentTest{
	public TestIntentGenerator() {
		super();
	}
	@org.junit.Test
    public void action() throws Exception {
		IntentHandler ith = new IntentHandlerTest("cool1!");
		IntentHandler ith2 = new IntentHandlerTest("cool2!");
		CServer cs = new CServer();
		String generatedIntentClassName = IntentClassGenerator.instance().generateClass(CServer.class.getName(),CServer.class.getName());
		Class cl = Class.forName(generatedIntentClassName);
		CServer csGen = (CServer) cl.getConstructor(null).newInstance(null);
		Method x = cl.getDeclaredMethod("addIntointentArraym", IntentHandler.class);
		System.out.println(x.getName());
		x.invoke(csGen, new Object[]{ith});
		x.invoke(csGen, new Object[]{ith2});
		csGen.m();
		csGen.m();
	}
}
