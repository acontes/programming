package org.objectweb.proactive.examples.components.sca.HelloClient;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.component.webservices.WSInfo;
import org.objectweb.proactive.extensions.sca.SCAPAPropertyRepository;
import org.objectweb.proactive.extensions.sca.Utils;


public class Main2 {

    private static final String HelloURL = "http://localhost:9000/HelloService";

    public static void main(String[] args) throws Exception {
        SCAPAPropertyRepository.SCA_PROVIDER.setValue("org.objectweb.proactive.extensions.sca.SCAFractive");
        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory tf = GCM.getGCMTypeFactory(boot);
        GenericFactory gf = GCM.getGenericFactory(boot);
        ComponentType t = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("Runner", Runner.class.getName(), false, false, false),
                tf.createFcItfType(HelloComp.HELLO_SERVICE_NAME, HelloService.class.getName(), true, false,
                        false) });

        Component comp = gf.newFcInstance(t, "primitive", HelloComp.class.getName());
        GCM.getBindingController(comp).bindFc(HelloComp.HELLO_SERVICE_NAME,
                HelloURL + "(" + WSInfo.CXFAEGISWSCALLER_ID + ")");
        GCM.getGCMLifeCycleController(comp).startFc();

        //((HelloService)comp.getFcInterface(HelloComp.HELLO_SERVICE_NAME)).print("hello !!");

        ((Runner) comp.getFcInterface("Runner")).execute();

        PALifeCycle.exitSuccess();
    }
}
