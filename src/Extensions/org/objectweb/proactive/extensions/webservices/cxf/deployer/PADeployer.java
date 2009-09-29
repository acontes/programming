/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.webservices.cxf.deployer;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.util.SerializableMethod;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.common.MethodUtils;
import org.objectweb.proactive.extensions.webservices.cxf.servicedeployer.ServiceDeployerItf;


/**
 * This class is in charge of calling the ServiceDeployer service on hosts specified
 * by urls and invokes its deploy and unDeploy methods with the needed arguments.
 *
 * @author The ProActive Team
 */
public final class PADeployer {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    /**
     * From an URL, checks whether it begins with "http://"
     * and ends with "/". If it is not the case, then it returns
     * the correct URL.
     *
     * @param url
     * @return the correct URL
     */
    private static String getCorrectURL(String url) {
        String correctUrl = "";

        if (!url.endsWith("/")) {
            correctUrl = url + "/";
        } else {
            correctUrl = url;
        }

        if (!correctUrl.startsWith("http://")) {
            correctUrl = "http://" + correctUrl;
        }
        return correctUrl;
    }

    /**
     * Creates a client for the ServiceDeployer service
     *
     * @param url URL where the service is located
     * @return A client of type ServiceDeployerItf
     */
    private static ServiceDeployerItf getClient(String url) {
        ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
        factory.setServiceClass(ServiceDeployerItf.class);
        factory.setAddress(url + WSConstants.SERVICES_PATH + "ServiceDeployer");
        ServiceDeployerItf client = (ServiceDeployerItf) factory.create();
        return client;
    }

    /**
     * Call the method deploy of the ServiceDeployer service
     * deployed on the host we want to deploy our active object.
     *
     * @param o Active object or component we want to deploy
     * @param url Url of the host
     * @param urn Name of the service
     * @param methods Methods to be deployed
     * @param isComponent Boolean saying whether it is a component
     */
    public static void deploy(Object o, String url, String urn, Method[] methods, boolean isComponent) {

        String correctUrl = getCorrectURL(url);
        byte[] marshalledObject = HttpMarshaller.marshallObject(o);
        ServiceDeployerItf client = getClient(correctUrl);
        ArrayList<SerializableMethod> serializableMethods = MethodUtils.getSerializableMethods(methods);
        byte[] marshalledSerializedMethods = HttpMarshaller.marshallObject(serializableMethods);
        client.deploy(marshalledObject, urn, marshalledSerializedMethods);
    }

    /**
     * Call the method undeploy of the ServiceDeployer service
     * deployed on the host.
     *
     * @param url URL of the host where the service is deployed
     * @param urn Name of the service.
     */
    public static void undeploy(String url, String urn) {
        String correctUrl = getCorrectURL(url);
        ServiceDeployerItf client = getClient(correctUrl);
        client.undeploy(urn);
    }
}
