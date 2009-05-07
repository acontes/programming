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
