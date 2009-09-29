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
package org.objectweb.proactive.examples.webservices.c3dWS;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;


public class WSDispatcherCaller {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    public static void call(String url, String method, Object[] args) throws AxisFault {
        RPCServiceClient serviceClient = new RPCServiceClient();
        Options options = serviceClient.getOptions();

        EndpointReference targetEPR = new EndpointReference(url + WSConstants.AXIS_SERVICES_PATH +
            "C3DDispatcher");

        System.out.println(targetEPR.getAddress());
        options.setTo(targetEPR);
        options.setAction(method);
        QName op = new QName(method);

        serviceClient.invokeRobust(op, args);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);

    }

    public static Object[] call(String url, String method, Object[] args, Class<?>[] returnTypes)
            throws AxisFault {
        RPCServiceClient serviceClient = new RPCServiceClient();
        Options options = serviceClient.getOptions();

        EndpointReference targetEPR = new EndpointReference(url + WSConstants.AXIS_SERVICES_PATH +
            "C3DDispatcher");
        System.out.println(targetEPR.getAddress());

        options.setTo(targetEPR);
        options.setAction(method);
        System.out.println(method);

        QName op = new QName(method);

        Object[] response = serviceClient.invokeBlocking(op, args, returnTypes);

        logger.info("Called the method " + method + " of the dispatcher hosted at " + url);

        return response;
    }

}
