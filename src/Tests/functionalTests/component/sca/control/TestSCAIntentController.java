package functionalTests.component.sca.control;

import static org.junit.Assert.assertEquals;

import org.apache.axis2.databinding.types.xsd.String;
import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.extensions.component.sca.SCAConfig;
import org.objectweb.proactive.extensions.component.sca.Utils;
import org.objectweb.proactive.extensions.component.sca.control.SCAIntentController;

import functionalTests.component.conform.Conformtest;
import functionalTests.component.conform.components.C;
import functionalTests.component.interceptor.A;
import functionalTests.component.interceptor.B;
import functionalTests.component.interceptor.FooItf;
import functionalTests.component.sca.components.CClient;
import functionalTests.component.sca.components.SecurityIntentHandler;
import functionalTests.component.sca.components.CServer;
import functionalTests.component.sca.components.TestIntentComponent;
import functionalTests.component.sca.components.TestIntentItf;
import functionalTests.component.sca.components.TimeOutIntentHandler;
//@snippet-start component_scauserguide_7

public class TestSCAIntentController extends Conformtest {
    Component componentA;
    Component componentB;

    public TestSCAIntentController() {
        super();
    }

    /**
     * @see testsuite.test.FunctionalTest#action()
     */
    @org.junit.Test
    public void action() throws Exception {
        SCAConfig.SCA_PROVIDER.setValue("org.objectweb.proactive.extensions.component.sca.SCAFractive");
        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
        GenericFactory cf = GCM.getGenericFactory(boot);

        componentA = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] {
                type_factory.createFcItfType("server", TestIntentItf.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                type_factory.createFcItfType("client", TestIntentItf.class.getName(), TypeFactory.CLIENT,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE) }), new ControllerDescription("A",
            Constants.PRIMITIVE),
        new ContentDescription(CClient.class.getName(), new Object[] {}));

        componentB = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] { type_factory
                .createFcItfType("server", TestIntentItf.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                        TypeFactory.SINGLE), }), new ControllerDescription("B", Constants.PRIMITIVE),
                new ContentDescription(CServer.class.getName(), new Object[] {}));
//@snippet-end component_scauserguide_7
//@snippet-start component_scauserguide_8        
        
        SCAIntentController scaic = org.objectweb.proactive.extensions.component.sca.Utils
                .getSCAIntentController(componentA);
        
        scaic.addFcIntentHandler(new TimeOutIntentHandler());
        scaic.addFcIntentHandler(new SecurityIntentHandler("first"));
        GCM.getBindingController(componentA).bindFc("client", componentB.getFcInterface("server"));

        GCM.getGCMLifeCycleController(componentA).startFc();
        GCM.getGCMLifeCycleController(componentB).startFc();

        TestIntentItf i = (TestIntentItf) componentA.getFcInterface("server"); 
        try{
        	i.m();
        	System.out.println("invocation of method n success");
        
        }catch (Exception e)
        {
        	System.err.println(e);
        }
    }
}
//@snippet-end component_scauserguide_8