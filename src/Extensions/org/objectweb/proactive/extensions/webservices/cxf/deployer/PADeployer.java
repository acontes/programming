/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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


public final class PADeployer {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    public PADeployer() {
    }

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
