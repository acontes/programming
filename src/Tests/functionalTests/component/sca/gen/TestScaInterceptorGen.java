package functionalTests.component.sca.gen;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.extensions.component.sca.SCAConfig;
import org.objectweb.proactive.extensions.component.sca.Utils;
import org.objectweb.proactive.extensions.component.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.component.sca.gen.InterceptorGenerator;

import functionalTests.component.conform.components.C;
import functionalTests.component.conform.components.I;
import functionalTests.component.conform.components.J;
import functionalTests.component.sca.components.CIntententHandler;
import functionalTests.component.sca.components.IntentControllerTestComp;
import functionalTests.component.sca.components.IntentTestInterface;


public class TestScaInterceptorGen {
    protected Component boot;
    protected GCMTypeFactory tf;
    protected GenericFactory gf;
    protected ComponentType t;
    protected ComponentType u;
    protected Component c;
    protected Component d;
    protected Component e;

    @Before
    public void setUp() throws Exception {
        SCAConfig.SCA_PROVIDER.setValue("org.objectweb.proactive.extensions.component.sca.SCAFractive");
        boot = Utils.getBootstrapComponent();
        tf = GCM.getGCMTypeFactory(boot);
        gf = GCM.getGenericFactory(boot);
        t = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("server", IntentTestInterface.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                tf.createFcItfType("servers", IntentTestInterface.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.COLLECTION),
                tf.createFcItfType("client", IntentTestInterface.class.getName(), TypeFactory.CLIENT,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                tf.createFcItfType("clients", IntentTestInterface.class.getName(), TypeFactory.CLIENT,
                        TypeFactory.MANDATORY, TypeFactory.COLLECTION) });
        u = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("serverI", IntentTestInterface.class.getName(), TypeFactory.SERVER,
                        TypeFactory.OPTIONAL, TypeFactory.SINGLE),
                tf.createFcItfType("serverJ", J.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                        TypeFactory.SINGLE), });
        setUpComponents();
    }

    protected void setUpComponents() throws Exception {
        c = gf.newFcInstance(t, "primitive", IntentControllerTestComp.class.getName());
        d = gf.newFcInstance(t, "primitive", IntentControllerTestComp.class.getName());
        e = gf.newFcInstance(u, "primitive", IntentControllerTestComp.class.getName());
    }

    @Test
    public void SimpleTest() throws Exception {
        Object obj = d.getFcInterface("server");
        SCAIntentController scaic = Utils.getSCAIntentController(c);
        scaic.addFcIntentHandler(new CIntententHandler());
        scaic.addFcIntentHandler(new CIntententHandler());
        InterceptorGenerator.instance().generateClass(obj, 2);
    }
}
