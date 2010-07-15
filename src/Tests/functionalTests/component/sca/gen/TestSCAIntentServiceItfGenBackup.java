package functionalTests.component.sca.gen;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.extensions.component.sca.SCAConfig;
import org.objectweb.proactive.extensions.component.sca.Utils;
import org.objectweb.proactive.extensions.component.sca.control.SCAIntentController;

import functionalTests.component.conform.components.J;
import functionalTests.component.sca.components.CIntententHandler;
import functionalTests.component.sca.components.TestIntentComponent;
import functionalTests.component.sca.components.TestIntentItf;


public class TestSCAIntentServiceItfGenBackup {
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
                tf.createFcItfType(TestIntentItf.SERVER_ITF_NAME, TestIntentItf.class.getName(),
                        TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                tf.createFcItfType("servers", TestIntentItf.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.COLLECTION),
                tf.createFcItfType(TestIntentItf.CLIENT_ITF_NAME, TestIntentItf.class.getName(),
                        TypeFactory.CLIENT, TypeFactory.MANDATORY, TypeFactory.SINGLE),
                tf.createFcItfType("clients", TestIntentItf.class.getName(), TypeFactory.CLIENT,
                        TypeFactory.MANDATORY, TypeFactory.COLLECTION) });
        u = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("serverI", TestIntentItf.class.getName(), TypeFactory.SERVER,
                        TypeFactory.OPTIONAL, TypeFactory.SINGLE),
                tf.createFcItfType("serverJ", J.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                        TypeFactory.SINGLE), });
        setUpComponents();
    }

    protected void setUpComponents() throws Exception {
        c = gf.newFcInstance(t, "primitive", TestIntentComponent.class.getName());
        d = gf.newFcInstance(t, "primitive", TestIntentComponent.class.getName());
        e = gf.newFcInstance(u, "primitive", TestIntentComponent.class.getName());
    }

    @Test
    public void SimpleTest() throws Exception {
        SCAIntentController scaic = org.objectweb.proactive.extensions.component.sca.Utils
                .getSCAIntentController(c);
        scaic.addFcIntentHandler(new CIntententHandler());
        GCM.getBindingController(c).bindFc(TestIntentItf.CLIENT_ITF_NAME,
                d.getFcInterface(TestIntentItf.SERVER_ITF_NAME));

        //GCM.getGCMLifeCycleController(c).startFc();
        GCM.getGCMLifeCycleController(d).startFc();

        //((I) componentA.getFcInterface("server")).n('c', 10.0);
        ((TestIntentItf) c.getFcInterface(TestIntentItf.SERVER_ITF_NAME)).m(10);
        Object cItf = GCM.getBindingController(c).lookupFc("client");
    }
}
