package functionalTests.component.sca.orange;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.extensions.component.sca.SCAConfig;
import org.objectweb.proactive.extensions.component.sca.Utils;
import org.objectweb.proactive.extensions.component.sca.control.SCAPropertyController;

import functionalTests.FunctionalTest;

public class TestOrangeSmsClientComponent extends FunctionalTest {

	protected Component boot;
	protected GCMTypeFactory tf;
	protected GenericFactory gf;
	protected ComponentType t;

	@Before
	public void setUp() throws Exception {
		SCAConfig.SCA_PROVIDER
				.setValue("org.objectweb.proactive.extensions.component.sca.SCAFractive");
		boot = Utils.getBootstrapComponent();
		tf = GCM.getGCMTypeFactory(boot);
		gf = GCM.getGenericFactory(boot);
		t = tf.createFcType(new InterfaceType[] {
				tf.createFcItfType(Constants.ATTRIBUTE_CONTROLLER,
						OrangeSMSClientAttributes.class.getName(), false,
						false, false),
				tf.createFcItfType("Runner", Runner.class.getName(), false,
						false, false),
				tf.createFcItfType(OrangeSMSClientComponent.SERVICES_NAME,
						OrangeSMS.class.getName(), true, false, false) });
	}

	@Test
	public void testOrangeSmsClient() throws Exception {
		Component c = gf.newFcInstance(t, "primitive",
				OrangeSMSClientComponent.class.getName());
		String url = "http://sms.beta.orange-api.net/sms/sendSMS.xml";
		SCAPropertyController scap = Utils.getSCAPropertyController(c);
		scap.init();
		scap.setValue("id", "test"); // Change me
		scap.setValue("from", "38100");
        scap.setValue("to", "33600000000"); // Change me
        scap.setValue("content", "Respecte mon authorité!!");
		GCM.getBindingController(c).bindFc(
				OrangeSMSClientComponent.SERVICES_NAME,
				url + "(" + RestOrangeServiceCaller.class.getName() + ")");
		GCM.getGCMLifeCycleController(c).startFc();
		Runner runner = (Runner) c.getFcInterface("Runner");
		boolean bool =runner.execute();
	}

}
