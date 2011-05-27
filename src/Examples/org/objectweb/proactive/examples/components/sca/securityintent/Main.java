/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
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
import org.objectweb.proactive.extensions.sca.intentpolicies.confidentiality.DecryptionIntentHandler;
import org.objectweb.proactive.extensions.sca.intentpolicies.confidentiality.EncryptionIntentHandler;
import org.objectweb.proactive.extensions.sca.intentpolicies.integrity.ClientIntegrityIntentHandler;
import org.objectweb.proactive.extensions.sca.intentpolicies.integrity.ServerIntegrityIntentHandler;


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
            SCAIntentController scaiClient = Utils.getSCAIntentController(componentA);
            scaiClient.addIntentHandler(new EncryptionIntentHandler(), TestIntentItf.CLIENT_ITF_NAME);
            scaiClient.addIntentHandler(new ClientIntegrityIntentHandler(), TestIntentItf.CLIENT_ITF_NAME);
            //scaiClient.addIntentHandler(new EncryptionIntentHandler(), TestIntentItf.CLIENT_ITF_NAME);

            //add decryption intent on the server side 
            SCAIntentController scaiServer = Utils.getSCAIntentController(componentB);
            //scaiServer.addIntentHandler(new DecryptionIntentHandler(), TestIntentItf.SERVER_ITF_NAME);
            scaiServer.addIntentHandler(new ServerIntegrityIntentHandler(), TestIntentItf.SERVER_ITF_NAME);
            scaiServer.addIntentHandler(new DecryptionIntentHandler(), TestIntentItf.SERVER_ITF_NAME);

            // component assembling

            GCM.getBindingController(componentA).bindFc(TestIntentItf.CLIENT_ITF_NAME,
                    componentB.getFcInterface(TestIntentItf.SERVER_ITF_NAME));

            GCM.getGCMLifeCycleController(componentA).startFc();
            GCM.getGCMLifeCycleController(componentB).startFc();

            TestIntentItf itf = (TestIntentItf) componentA.getFcInterface(TestIntentItf.SERVER_ITF_NAME);

            byte[] data = "security transfer".getBytes();
            byte[] res = itf.dataTreatment(data);
            System.out.println("Returned result: " + new String(res));

        } catch (Exception e) {
            e.printStackTrace();
        }

        GCM.getGCMLifeCycleController(componentA).stopFc();
        GCM.getGCMLifeCycleController(componentB).stopFc();
        PALifeCycle.exitSuccess();
    }
}
