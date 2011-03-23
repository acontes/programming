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
package org.objectweb.proactive.examples.components.sca.currencysms;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.component.webservices.CXFRESTfulServiceCaller;
import org.objectweb.proactive.core.component.webservices.WSInfo;
import org.objectweb.proactive.extensions.sca.Utils;
import org.objectweb.proactive.extensions.sca.control.SCAPropertyController;


public class Main {
    private static final String currencyURL = "http://www.webservicex.net/CurrencyConvertor.asmx";
    private static final String orangeURL = "http://sms.beta.orange-api.net/sms/sendSMS.xml";

    public static void main(String[] args) throws Exception {
        if ((args.length != 2)) {
            System.out.println("Parameters : [Orange_API_Access_Key] [Destination_Number] "
                + "\n        The first parameter is your Orange API Access Key"
                + "\n        The second parameter is your destination number");
            return;
        }
        String orangeID = args[0];
        String destNumber = args[1];
        Component boot = Utils.getBootstrapComponent();
        GCMTypeFactory tf = GCM.getGCMTypeFactory(boot);
        GenericFactory gf = GCM.getGenericFactory(boot);
        ComponentType t = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("Runner", Runner.class.getName(), false, false, false),
                tf.createFcItfType(CurrencySMS.CURRENCY_SERVICE_NAME, CurrencyService.class.getName(), true,
                        false, false),
                tf.createFcItfType(CurrencySMS.ORANGE_SERVICE_NAME, OrangeService.class.getName(), true,
                        false, false) });

        Component comp = gf.newFcInstance(t, "primitive", CurrencySMS.class.getName());
        GCM.getBindingController(comp).bindFc(CurrencySMS.CURRENCY_SERVICE_NAME,
                currencyURL + "(" + WSInfo.DYNAMICCXFWSCALLER_ID + ")");
        GCM.getBindingController(comp).bindFc(CurrencySMS.ORANGE_SERVICE_NAME,
                orangeURL + "(" + CXFRESTfulServiceCaller.class.getName() + ")");
        SCAPropertyController scap = Utils.getSCAPropertyController(comp);
        scap.setValue("fromCurrency", "USD");
        scap.setValue("toCurrency", "EUR");
        scap.setValue("id", orangeID);
        scap.setValue("from", "38100");
        scap.setValue("to", destNumber);

        GCM.getGCMLifeCycleController(comp).startFc();
        ((Runner) comp.getFcInterface("Runner")).execute();

        PALifeCycle.exitSuccess();
    }
}
