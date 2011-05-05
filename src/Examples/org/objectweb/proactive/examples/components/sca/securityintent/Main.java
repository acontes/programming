package org.objectweb.proactive.examples.components.sca.securityintent;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.examples.components.sca.securityintent.components.CClient;
import org.objectweb.proactive.examples.components.sca.securityintent.components.CServer;
import org.objectweb.proactive.examples.components.sca.securityintent.components.TestIntentItf;
import org.objectweb.proactive.extensions.sca.SCAPAPropertyRepository;
import org.objectweb.proactive.extensions.sca.Utils;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;


public class Main {

    public static void main(String[] args) throws Exception {
        Component componentA = null;
        Component componentB = null;
        try {
            SCAPAPropertyRepository.SCA_PROVIDER
                    .setValue("org.objectweb.proactive.extensions.sca.SCAFractive");
            Component boot = Utils.getBootstrapComponent();
            GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
            GenericFactory cf = GCM.getGenericFactory(boot);

            componentA = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] {
                    type_factory.createFcItfType(TestIntentItf.SERVER_ITF_NAME,
                            TestIntentItf.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                            TypeFactory.SINGLE),
                    type_factory.createFcItfType(TestIntentItf.CLIENT_ITF_NAME,
                            TestIntentItf.class.getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY,
                            TypeFactory.SINGLE) }), Constants.PRIMITIVE, new ContentDescription(CClient.class
                    .getName(), new Object[] {}));

            componentB = cf.newFcInstance(type_factory.createFcType(new InterfaceType[] { type_factory
                    .createFcItfType(TestIntentItf.SERVER_ITF_NAME, TestIntentItf.class.getName(),
                            TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE) }),
                    Constants.PRIMITIVE, new ContentDescription(CServer.class.getName(), new Object[] {}));
            //add encryption intent on the client side 
            SCAIntentController scai = Utils.getSCAIntentController(componentA);
            scai.addIntentHandler(new EncryptionIntentHandler(), TestIntentItf.CLIENT_ITF_NAME);

            //add decryption intent on the server side 
            SCAIntentController scaiClient = Utils.getSCAIntentController(componentB);
            scaiClient.addIntentHandler(new DecryptionIntentHandler(), TestIntentItf.SERVER_ITF_NAME);
            // component assembling

            GCM.getBindingController(componentA).bindFc(TestIntentItf.CLIENT_ITF_NAME,
                    componentB.getFcInterface(TestIntentItf.SERVER_ITF_NAME));

            GCM.getGCMLifeCycleController(componentA).startFc();
            GCM.getGCMLifeCycleController(componentB).startFc();

            TestIntentItf itf = (TestIntentItf) componentA.getFcInterface(TestIntentItf.SERVER_ITF_NAME);

            byte[] data = "security transfer".getBytes();
            byte[] res = itf.dataTreatment(data);
            System.err.println("reversed resultat : " + new String(res));

        } catch (Exception e) {
            e.printStackTrace();
        }

        GCM.getGCMLifeCycleController(componentA).stopFc();
        GCM.getGCMLifeCycleController(componentB).stopFc();
        PALifeCycle.exitSuccess();
    }
}
