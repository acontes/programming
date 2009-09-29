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

package org.objectweb.proactive.extensions.webservices.cxf.servicedeployer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.core.util.SerializableMethod;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.common.MethodUtils;


/**
 * This class implements the service which will be deployed on the server at the
 * same time as the proactive web application. This service is used to deploy and undeploy
 * Active Object and components on the server side.
 *
 * @author The ProActive Team
 */
public class ServiceDeployer implements ServiceDeployerItf {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    static {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new java.rmi.RMISecurityManager());
        try {
            RuntimeFactory.getDefaultRuntime();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    // Map of servers corresponding to service servers
    // It is used to undeploy a service. In that case, we just need
    // to retrieve the corresponding Server and to stop it.
    private HashMap<String, Server> serverList = new HashMap<String, Server>();

    /**
     * Expose the marshalled active object as a web service
     *
     * @param marshalledObject marshalled object
     * @param serviceName Name of the service
     * @param marshalledSerializedMethods byte array representing the methods (of type Method)
     *        to be exposed
     */
    @SuppressWarnings("unchecked")
    public void deploy(byte[] marshalledObject, String serviceName, byte[] marshalledSerializedMethods) {

        Object o = HttpMarshaller.unmarshallObject(marshalledObject);
        Class<?> superclass = o.getClass().getSuperclass();

        ReflectionServiceFactoryBean serviceFactory = new ReflectionServiceFactoryBean();
        serviceFactory.setServiceClass(superclass);

        ArrayList<SerializableMethod> serializableMethods = (ArrayList<SerializableMethod>) HttpMarshaller
                .unmarshallObject(marshalledSerializedMethods);
        Method[] methods = MethodUtils.getMethodsFromSerializableMethods(serializableMethods);
        if (methods != null) {
            MethodUtils mc = new MethodUtils(superclass);
            List<Method> ignoredMethods;
            ignoredMethods = mc.getExcludedMethods(methods);
            serviceFactory.setIgnoredMethods(ignoredMethods);
        }

        ServerFactoryBean svrFactory = new ServerFactoryBean(serviceFactory);
        svrFactory.setAddress("/" + serviceName);
        svrFactory.setServiceBean(superclass.cast(o));

        /*
         * Attaches a list of in-interceptors
         */
        List<Interceptor> inInterceptors = new ArrayList<Interceptor>();
        LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
        inInterceptors.add(loggingInInterceptor);
        svrFactory.setInInterceptors(inInterceptors);

        /*
         * Attaches a list of out-interceptors
         */
        List<Interceptor> outInterceptors = new ArrayList<Interceptor>();
        LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
        outInterceptors.add(loggingOutInterceptor);
        svrFactory.setOutInterceptors(outInterceptors);
        svrFactory.create();
        serverList.put(serviceName, svrFactory.getServer());

        logger.info("The service " + serviceName + " has been deployed");

    }

    /**
     * Undeploy the service whose name is serviceName
     *
     * @param serviceName name of the service
     */
    public void undeploy(String serviceName) {
        Server serviceServer = serverList.get(serviceName);
        serviceServer.stop();
        serverList.remove(serviceName);
        logger.info("The service " + serviceName + " has been undeployed");
    }
}
