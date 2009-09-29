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
package org.objectweb.proactive.extensions.webservices.common;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;


/**
 * This class gives some useful methods to easily call an axis2 or a cxf web
 * service. It also gives methods to only get an axis2 or a cxf client without
 * processing the a call.
 *
 * @author The ProActive Team
 */
public class ClientUtils {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    /**
     * Get an axis2 client
     *
     * @param url Url of the service
     * @param serviceName Name of the service
     * @return an RPCServiceClient
     * @throws AxisFault
     */
    public static RPCServiceClient getAxis2Client(String url, String serviceName) throws AxisFault {
        RPCServiceClient serviceClient = new RPCServiceClient();
        Options options = serviceClient.getOptions();

        EndpointReference targetEPR = new EndpointReference(url + WSConstants.SERVICES_PATH + serviceName);

        serviceClient.getAxisService().setElementFormDefault(false);
        options.setTo(targetEPR);

        return serviceClient;
    }

    /**
     * get a cxf client
     *
     * @param url Url of the service
     * @param serviceClass Class of the service (needed for cxf contrary to Axis2)
     * @param serviceName Name of the service
     * @return a cxf Client
     */
    public static Client getCxfClient(String url, Class<?> serviceClass, String serviceName) {
        ClientFactoryBean factory = new ClientFactoryBean();
        factory.setServiceClass(serviceClass);
        factory.setAddress(url + WSConstants.SERVICES_PATH + serviceName);
        factory.getServiceFactory().setQualifyWrapperSchema(false);
        Client client = factory.create();

        return client;
    }

    /**
     * Axis2 one way call (for void methods)
     *
     * @param url Url of the service
     * @param serviceName Name of the service
     * @param method Method to call
     * @param args Arguments to give to the method
     * @throws AxisFault
     */
    public static void axis2OneWayCall(String url, String serviceName, String method, Object[] args)
            throws AxisFault {

        RPCServiceClient serviceClient = getAxis2Client(url, serviceName);
        QName op = new QName(method);
        serviceClient.invokeRobust(op, args);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);
    }

    /**
     * CXF one way call (for void methods)
     *
     * @param url Url of the service
     * @param serviceName Name of the service
     * @param method Method to call
     * @param args Arguments to give to the method
     * @param serviceClass Class of the service
     * @throws Exception
     */
    public static void cxfOneWayCall(String url, String serviceName, String method, Object[] args,
            Class<?> serviceClass) throws Exception {

        Client client = getCxfClient(url, serviceClass, serviceName);
        client.invoke(method, args);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);
    }

    /**
     * Call of a method whose return type is different to void using Axis2
     *
     * @param url Url of the service
     * @param serviceName Name of the service
     * @param method Method to call
     * @param args Arguments to give to the method
     * @param returnTypes Return type class
     * @return Result of the call
     * @throws AxisFault
     */
    public static Object[] axis2Call(String url, String serviceName, String method, Object[] args,
            Class<?>... returnTypes) throws AxisFault {

        RPCServiceClient serviceClient = getAxis2Client(url, serviceName);
        QName op = new QName(method);
        Object[] response = serviceClient.invokeBlocking(op, args, returnTypes);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);

        return response;
    }

    /**
     * Call of a method whose return type is different to void using CXF.
     * In the case of a CXF call, we do not need to specify the class of
     * the return type
     *
     * @param url Url of the service
     * @param serviceName Name of the service
     * @param method Method to call
     * @param args Arguments to give to the method
     * @param serviceClass Class of the service
     * @return Result of the call
     * @throws Exception
     */
    public static Object[] cxfCall(String url, String serviceName, String method, Object[] args,
            Class<?> serviceClass) throws Exception {

        Client client = getCxfClient(url, serviceClass, serviceName);
        Object[] result = client.invoke(method, args);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);

        return result;
    }

    /**
     * One way call using the framework specified in the first argument.
     *
     * @param wsFrameWork
     * @param url Url of the service
     * @param serviceName Name of the service
     * @param method Method to call
     * @param args Arguments to give to the method
     * @param serviceClass Class of the service (only used for cxf call, should be null for Axis2)
     * @throws Exception
     */
    public static void oneWayCall(String wsFrameWork, String url, String serviceName, String method,
            Object[] args, Class<?> serviceClass) throws Exception {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            axis2OneWayCall(url, serviceName, method, args);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            cxfOneWayCall(url, serviceName, method, args, serviceClass);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }

    /**
     * Two ways call using the framework specified in the first argument.
     *
     * @param wsFrameWork
     * @param url Url of the service
     * @param serviceName Name of the service
     * @param method Method to call
     * @param args Arguments to give to the method
     * @param serviceOrReturnClass
     *      In the Axis2 case, this argument corresponds to  the class of the return types.
     *      In the CXF case, it corresponds to the service class.
     * @return
     * @throws Exception
     */
    public static Object[] call(String wsFrameWork, String url, String serviceName, String method,
            Object[] args, Class<?>... serviceOrReturnClass) throws Exception {
        if (WSConstants.AXIS2_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            return axis2Call(url, serviceName, method, args, serviceOrReturnClass);
        } else if (WSConstants.CXF_FRAMEWORK_IDENTIFIER.equals(wsFrameWork)) {
            return cxfCall(url, serviceName, method, args, serviceOrReturnClass[0]);
        } else {
            throw new ProActiveException("Unknown web service framework identifier: " + wsFrameWork);
        }
    }
}
