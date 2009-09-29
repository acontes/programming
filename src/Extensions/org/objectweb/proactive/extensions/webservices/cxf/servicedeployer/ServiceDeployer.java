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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.FaultOutInterceptor;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.ServiceImpl;
import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.ServiceGenerator;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.local.LocalTransportFactory;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;


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

    /**
     * Returns the methods to be excluded. These methods are methods defined in the
     * WSConstants.disallowedMethods vector and methods which are not in methodsName.
     * In case of a null methodsName, only methods in dissallowdMethods vector are
     * returned.
     *
     * @param objectClass
     * @param methodsName
     * @return
     */
    private ArrayList<Method> getIgnoredMethods(Class<?> objectClass, String[] methodsName) {
        ArrayList<Method> ignoredMethods = new ArrayList<Method>();

        //        try {
        //            Method m1 = objectClass.getMethod("setHello", null);
        //            Method m2 = objectClass.getMethod("setTextAndReturn", String.class);
        //            ignoredMethods.add(m1);
        //            ignoredMethods.add(m2);
        //        } catch (SecurityException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        } catch (NoSuchMethodException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }

        //        Iterator<String> it = WSConstants.disallowedMethods.iterator();
        //
        //        while (it.hasNext()) {
        //            try {
        //                Method method = objectClass.getMethod(it.next());
        //                ignoredMethods.add(method);
        //            } catch (NoSuchMethodException nsme) {
        //
        //            }
        //        }
        //
        //        if (methodsName.length == 0)
        //            return ignoredMethods;
        //
        //        Method[] methodsTable = objectClass.getMethods();
        //
        //        ArrayList<Method> methodsArray = new ArrayList<Method>();
        //        for (String name : methodsName) {
        //            methodsArray.add(objectClass.getMethod;
        //        }
        //
        //        for (Method m : methodsTable) {
        //            if (!methodsNameArray.contains(m.getName())) {
        //                ignoredMethods.add(m);
        //            }
        //        }

        return ignoredMethods;
    }

    public void deploy(byte[] marshalledObject, String serviceName) {
        this.deploy(marshalledObject, serviceName, null);
    }

    public void deploy(byte[] marshalledObject, String serviceName, String[] methods) {
        logger.info("Entering into the deploy method");

        Object o = HttpMarshaller.unmarshallObject(marshalledObject);
        Class superclass = o.getClass().getSuperclass();

        ReflectionServiceFactoryBean serviceFactory = new ReflectionServiceFactoryBean();
        serviceFactory.setServiceClass(superclass);
        List<Method> ignoredMethods = this.getIgnoredMethods(superclass, methods);
        serviceFactory.setIgnoredMethods(ignoredMethods);

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

        String address = "http://localhost:8081/" + WSConstants.SERVICES_PATH + serviceName;
        String implClass = superclass.getName();
        logger.info("Object of type " + implClass + " has been deployed on " + address);

    }

    public String sayHi() {
        System.out.println("Called sayHi()");
        return "Hi";
    }

    public String sayHiWithName(String name) {
        System.out.println("Called sayHi(String name)");
        return "Hi " + name;
    }
}
