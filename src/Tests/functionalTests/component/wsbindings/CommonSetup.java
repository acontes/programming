/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
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
package functionalTests.component.wsbindings;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.junit.After;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.WebServicesFactory;

import functionalTests.ComponentTest;


public abstract class CommonSetup extends ComponentTest {
    public static int NUMBER_SERVERS = 3;
    public static String SERVER_DEFAULT_NAME = "Server";
    public static String SERVER_SERVICES_NAME = "Services";
    public static String SERVER_SERVICEMULTICAST_NAME = "Service";

    protected Component boot;
    protected TypeFactory tf;
    protected GenericFactory gf;
    protected String url;
    protected Component[] servers;
    protected ComponentType componentType;
    protected WebServicesFactory wsf;
    protected WebServices ws;

    public void setUpAndDeploy() throws Exception {
        boot = GCM.getBootstrapComponent();
        tf = GCM.getTypeFactory(boot);
        gf = GCM.getGenericFactory(boot);

        url = AbstractWebServicesFactory.getLocalUrl();
        ComponentType sType = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType(SERVER_SERVICES_NAME, Services.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                tf.createFcItfType(SERVER_SERVICEMULTICAST_NAME, Service.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE) });
        servers = new Component[NUMBER_SERVERS];
        for (int i = 0; i < NUMBER_SERVERS; i++) {
            servers[i] = gf.newFcInstance(sType, new ControllerDescription(SERVER_DEFAULT_NAME + i,
                Constants.PRIMITIVE), new ContentDescription(Server.class.getName()));
            Fractal.getLifeCycleController(servers[i]).startFc();
            ws = wsf.getWebServices(url);
            ws.exposeComponentAsWebService(servers[i], SERVER_DEFAULT_NAME + i);
        }

        componentType = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("Runner", Runner.class.getName(), TypeFactory.SERVER,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                tf.createFcItfType(Client.SERVICES_NAME, Services.class.getName(), TypeFactory.CLIENT,
                        TypeFactory.MANDATORY, TypeFactory.SINGLE),
                ((GCMTypeFactory) tf).createGCMItfType(Client.SERVICEMULTICASTREAL_NAME,
                        ServiceMulticast.class.getName(), TypeFactory.CLIENT, TypeFactory.OPTIONAL,
                        GCMTypeFactory.MULTICAST_CARDINALITY),
                tf.createFcItfType(Client.SERVICEMULTICASTFALSE_NAME, ServiceMulticast.class.getName(),
                        TypeFactory.CLIENT, TypeFactory.OPTIONAL, TypeFactory.SINGLE) });
    }

    @After
    public void undeploy() throws Exception {
        for (int i = 0; i < NUMBER_SERVERS; i++) {
            ws.unExposeComponentAsWebService(servers[i], SERVER_DEFAULT_NAME + i);
        }
    }
}
