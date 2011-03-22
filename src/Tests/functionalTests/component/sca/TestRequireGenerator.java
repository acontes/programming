package functionalTests.component.sca;

import org.objectweb.proactive.core.component.gen.RequiresClassGenerator;

import functionalTests.ComponentTest;
import functionalTests.component.sca.control.components.CClient;

public class TestRequireGenerator extends ComponentTest{
	
	public TestRequireGenerator() {
		super();
	}
	
	@org.junit.Test
    public void action() throws Exception {
		CClient test = new CClient();
		String generatedIntentClassName = RequiresClassGenerator.instance().
			generateClass(test.getClass().getName(),test.getClass().getName());
	}
}
