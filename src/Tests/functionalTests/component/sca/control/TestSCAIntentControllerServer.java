package functionalTests.component.sca.control;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.examples.components.sca.securityintent.SecurityIntentHandler;
import org.objectweb.proactive.extensions.sca.Utils;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.sca.control.SCAPropertyController;

import functionalTests.component.sca.SCAComponentTest;
import functionalTests.component.sca.control.components.CClient;
import functionalTests.component.sca.control.components.CServer;
import functionalTests.component.sca.control.components.ExecuteItf;
import functionalTests.component.sca.control.components.IntentHandlerTest;
import functionalTests.component.sca.control.components.TestIntentItf;
import functionalTests.component.sca.control.components.TestIntentItf2;

public class TestSCAIntentControllerServer extends SCAComponentTest{
	Component componentA;
    Component componentB;

    public TestSCAIntentControllerServer() {
        super();
    }

    @Before
    public void initTest() throws Exception {
        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
        GenericFactory cf = GCM.getGenericFactory(boot);
//        System.err.println("cf type"+cf.getClass().getName());
        componentA = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] {
                type_factory.createFcItfType(TestIntentItf.SERVER_ITF_NAME, TestIntentItf.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType(TestIntentItf.CLIENT_ITF_NAME, TestIntentItf.class.getName(),
                        TypeFactory.CLIENT, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType(TestIntentItf2.SERVER_ITF_NAME, TestIntentItf2.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType("run", ExecuteItf.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType(TestIntentItf2.CLIENT_ITF_NAME, TestIntentItf2.class.getName(),
                        TypeFactory.CLIENT, TypeFactory.MANDATORY, TypeFactory.SINGLE) }),
                Constants.PRIMITIVE, new ContentDescription(CClient.class.getName(), new Object[] {}));

        componentB = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] {
                type_factory.createFcItfType(TestIntentItf.SERVER_ITF_NAME, TestIntentItf.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType(TestIntentItf2.SERVER_ITF_NAME, TestIntentItf2.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE) }),
                Constants.PRIMITIVE, new ContentDescription(CServer.class.getName(), new Object[] {}));
    }
    @org.junit.Test
    public void action() throws Exception {
    	SCAIntentController scaic1 = org.objectweb.proactive.extensions.sca.Utils
        .getSCAIntentController(componentA); //client
        SCAIntentController scaic = org.objectweb.proactive.extensions.sca.Utils
                .getSCAIntentController(componentB); //server
        
        //SCAPropertyController scac3 = Utils.getSCAPropertyController(componentA);
        //SCAPropertyController scac4 = Utils.getSCAPropertyController(componentB);
        IntentHandler y1 = new IntentHandlerTest("test server");
        IntentHandler y2 = new IntentHandlerTest("test client");
        scaic.addIntentHandler(y1);
        scaic1.addIntentHandler(y2);
        //scaic.addIntentHandler(y, TestIntentItf2.SERVER_ITF_NAME);
        //scaic.addIntentHandler(new SecurityIntentHandler("pass"), TestIntentItf.CLIENT_ITF_NAME, "m");
        GCM.getBindingController(componentA).bindFc(TestIntentItf.CLIENT_ITF_NAME,
                componentB.getFcInterface(TestIntentItf.SERVER_ITF_NAME));
        GCM.getBindingController(componentA).bindFc(TestIntentItf2.CLIENT_ITF_NAME,
                componentB.getFcInterface(TestIntentItf2.SERVER_ITF_NAME));
        GCM.getGCMLifeCycleController(componentB).startFc();
        GCM.getGCMLifeCycleController(componentA).startFc();
        TestIntentItf i = (TestIntentItf) componentA.getFcInterface(TestIntentItf.SERVER_ITF_NAME); //here the get interface is from server side 
        TestIntentItf2 i2 = (TestIntentItf2) componentA.getFcInterface(TestIntentItf2.SERVER_ITF_NAME);
        ExecuteItf i3 = ((ExecuteItf) componentA.getFcInterface("run"));
        try {
            i.m();
            System.out.println("invocation of method m success");
            int x = i2.n2();
            System.out.println("invocation of method n success, value of n : " + x);
            i3.execute();
        } catch (Exception e) {
            //fail();
            e.printStackTrace();
        }
        GCM.getGCMLifeCycleController(componentA).stopFc();
        GCM.getGCMLifeCycleController(componentB).stopFc();
        GCM.getBindingController(componentA).unbindFc(TestIntentItf.CLIENT_ITF_NAME);
        GCM.getBindingController(componentA).unbindFc(TestIntentItf2.CLIENT_ITF_NAME);

    }
}
