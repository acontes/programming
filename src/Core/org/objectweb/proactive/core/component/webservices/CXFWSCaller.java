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
package org.objectweb.proactive.core.component.webservices;

import java.net.URL;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.log4j.Logger;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Implementation of the {@link ProActiveWSCaller} interface using the {@link http://cxf.apache.org/ CXF} API.
 *
 * @author The ProActive Team
 * @see ProActiveWSCaller
 */
@PublicAPI
public class CXFWSCaller implements ProActiveWSCaller {
    protected static final transient Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_REQUESTS);

    public CXFWSCaller() {
    }

    public Object[] callWS(String wsUrl, String methodName, Object[] args, Class<?>[] returnTypes) {
        try {
            DynamicClientFactory dcf = DynamicClientFactory.newInstance();
            URL wsdlUrl = new URL(wsUrl + "?wsdl");
            Client client = dcf.createClient(wsdlUrl);
            Object[] results = client.invoke(methodName, args);
            client.destroy();
            return results;
        } catch (Exception e) {
            logger.error("[CXF] Failed to invoke web service: " + wsUrl, e);
            return new Object[] { null };
        }
    }
}
