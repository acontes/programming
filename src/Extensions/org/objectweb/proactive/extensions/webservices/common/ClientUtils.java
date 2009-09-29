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


public class ClientUtils {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    public static RPCServiceClient getAxis2Client(String url, String serviceName) throws AxisFault {
        RPCServiceClient serviceClient = new RPCServiceClient();
        Options options = serviceClient.getOptions();

        EndpointReference targetEPR = new EndpointReference(url + WSConstants.SERVICES_PATH + serviceName);

        options.setTo(targetEPR);

        return serviceClient;
    }

    public static Client getCxfClient(String url, Class<?> serviceClass, String serviceName) {
        ClientFactoryBean factory = new ClientFactoryBean();
        factory.setServiceClass(serviceClass);
        factory.setAddress(url + WSConstants.SERVICES_PATH + serviceName);
        factory.getServiceFactory().setQualifyWrapperSchema(false);
        Client client = factory.create();

        return client;
    }

    public static void axis2OneWayCall(String url, String serviceName, String method, Object[] args)
            throws AxisFault {

        RPCServiceClient serviceClient = getAxis2Client(url, serviceName);
        QName op = new QName(method);
        serviceClient.invokeRobust(op, args);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);
    }

    public static void cxfOneWayCall(String url, String serviceName, String method, Object[] args,
            Class<?> serviceClass) throws Exception {

        Client client = getCxfClient(url, serviceClass, serviceName);
        client.invoke(method, args);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);
    }

    public static Object[] axis2Call(String url, String serviceName, String method, Object[] args,
            Class<?>... returnTypes) throws AxisFault {

        RPCServiceClient serviceClient = getAxis2Client(url, serviceName);
        QName op = new QName(method);
        Object[] response = serviceClient.invokeBlocking(op, args, returnTypes);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);

        return response;
    }

    public static Object[] cxfCall(String url, String serviceName, String method, Object[] args,
            Class<?> serviceClass) throws Exception {

        Client client = getCxfClient(url, serviceClass, serviceName);
        Object[] result = client.invoke(method, args);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);

        return result;
    }

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
