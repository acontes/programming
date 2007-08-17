/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.jmx.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Names used in the creation of ProActive ObjectNames.
 * @author ProActiveRuntime
 */
public class FactoryName {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.JMX);
    public static final String OS = "java.lang:type=OperatingSystem";
    public static final String NODE_TYPE = "Node";
    public static final String NODE = "org.objectweb.proactive.core.node:type=" +
        NODE_TYPE;
    public static final String HOST_TYPE = "Host";
    public static final String HOST = "org.objectweb.proactive.core.host:type=" +
        HOST_TYPE;
    public static final String RUNTIME_TYPE = "Runtime";
    public static final String RUNTIME = "org.objectweb.proactive.core.runtimes:type=" +
        RUNTIME_TYPE;
    public static final String AO_TYPE = "AO";
    public static final String AO = "org.objectweb.proactive.core.body:type=" +
        AO_TYPE;

    /**
     * Creates a ObjectName corresponding to an active object.
     * @param id The unique id of the active object.
     * @return The ObjectName corresponding to the given id.
     */
    public static ObjectName createActiveObjectName(UniqueID id) {
        ObjectName oname = null;
        try {
            oname = new ObjectName(FactoryName.AO + ", id=" +
                    id.toString().replace(':', '-'));
        } catch (MalformedObjectNameException e) {
            logger.error("Can't create the objectName of the active object", e);
        } catch (NullPointerException e) {
            logger.error("Can't create the objectName of the active object", e);
        }
        return oname;
    }

    /**
     * Creates a ObjectName corresponding to a node.
     * @param runtimeUrl The url of the ProActive Runtime.
     * @param nodeName The name of the node
     * @return The ObjectName corresponding to the given id.
     */
    public static ObjectName createNodeObjectName(String runtimeUrl,
        String nodeName) {
        String host = UrlBuilder.getHostNameFromUrl(runtimeUrl);
        String name = UrlBuilder.getNameFromUrl(runtimeUrl);
        String protocol = UrlBuilder.getProtocol(runtimeUrl);
        int port = UrlBuilder.getPortFromUrl(runtimeUrl);

        runtimeUrl = UrlBuilder.buildUrl(host, name, protocol, port);

        ObjectName oname = null;
        try {
            oname = new ObjectName(FactoryName.NODE + ",runtimeUrl=" +
                    runtimeUrl.replace(':', '-') + ", nodeName=" +
                    nodeName.replace(':', '-'));
        } catch (MalformedObjectNameException e) {
            logger.error("Can't create the objectName of the node", e);
        } catch (NullPointerException e) {
            logger.error("Can't create the objectName of the node", e);
        }
        return oname;
    }

    /**
     * Creates a ObjectName corresponding to a ProActiveRuntime.
     * @param url The url of the ProActiveRuntime.
     * @return The ObjectName corresponding to the given url.
     */
    public static ObjectName createRuntimeObjectName(String url) {
        String host = UrlBuilder.getHostNameFromUrl(url);
        String name = UrlBuilder.getNameFromUrl(url);
        String protocol = UrlBuilder.getProtocol(url);
        int port = UrlBuilder.getPortFromUrl(url);

        url = UrlBuilder.buildUrl(host, name, protocol, port);

        ObjectName oname = null;
        try {
            oname = new ObjectName(FactoryName.RUNTIME + ",url=" +
                    url.replace(':', '-'));
        } catch (MalformedObjectNameException e) {
            logger.error("Can't create the objectName of the runtime", e);
        } catch (NullPointerException e) {
            logger.error("Can't create the objectName of the runtime", e);
        }
        return oname;
    }
}
