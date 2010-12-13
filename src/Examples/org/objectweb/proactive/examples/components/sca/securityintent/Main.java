/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 *              Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
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
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.examples.components.sca.currencysms.CurrencySMS;
import org.objectweb.proactive.examples.components.userguide.primitive.ComputeItf;
import org.objectweb.proactive.examples.components.userguide.primitive.PrimitiveComputer;
import org.objectweb.proactive.examples.components.userguide.primitive.PrimitiveMaster;
import org.objectweb.proactive.extensions.sca.SCAPAPropertyRepository;
import org.objectweb.proactive.extensions.sca.Utils;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;


public class Main {

    public static void main(String[] args) throws Exception {
        
        try {
        	SCAPAPropertyRepository.SCA_PROVIDER.setValue("org.objectweb.proactive.extensions.sca.SCAFractive");
        	Component boot = Utils.getBootstrapComponent();
            GCMTypeFactory typeFact = GCM.getGCMTypeFactory(boot);
            GenericFactory genericFact = GCM.getGenericFactory(boot);

            // component types: PrimitiveComputer, PrimitiveMaster, CompositeWrapper
            ComponentType computerType = typeFact.createFcType(new InterfaceType[] { typeFact
                    .createFcItfType("compute-itf", ComputeItf.class.getName(), TypeFactory.SERVER,
                            TypeFactory.MANDATORY, TypeFactory.SINGLE) });
            ComponentType masterType = typeFact.createFcType(new InterfaceType[] {
                    typeFact.createFcItfType("run", Runnable.class.getName(), TypeFactory.SERVER,
                            TypeFactory.MANDATORY, TypeFactory.SINGLE),
                    typeFact.createFcItfType("compute-itf", ComputeItf.class.getName(), TypeFactory.CLIENT,
                            TypeFactory.MANDATORY, TypeFactory.SINGLE) });
            //Component comp = gf.newFcInstance(t, "primitive", CurrencySMS.class.getName());
            // components creation
            Component primitiveComputer = genericFact.newFcInstance(computerType, "primitive", PrimitiveComputer.class
                    .getName());
            Component primitiveMaster = genericFact.newFcInstance(masterType,"primitive",PrimitiveMaster.class
                    .getName());
            //add security intent
            SCAIntentController scai = Utils.getSCAIntentController(primitiveMaster);
            scai.addIntentHandler(new SecurityIntentHandler("pass"));
            // component assembling
            GCM.getBindingController(primitiveMaster).bindFc("compute-itf",
                    primitiveComputer.getFcInterface("compute-itf"));

            // start CompositeWrapper component
            GCM.getGCMLifeCycleController(primitiveComputer).startFc();
            GCM.getGCMLifeCycleController(primitiveMaster).startFc();
            ComputeItf itf = (ComputeItf)GCM.getBindingController(primitiveMaster).lookupFc("compute-itf");
            itf.compute(5);
            // get the run interface
            //Runnable itf = ((Runnable) primitiveMaster.getFcInterface("run"));
            //ComputeItf itf = (ComputeItf) primitiveMaster.getFcInterface(PrimitiveMaster.COMPUTER_CLIENT_ITF);
            //itf.compute(0);
            // call component
            //itf.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PALifeCycle.exitSuccess();
    }
}
