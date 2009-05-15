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
import org.objectweb.proactive.annotation.PublicAPI;


/**
 * Implementation of the {@link ProActiveWSCaller} interface using the {@link http://cxf.apache.org/ CXF} API.
 *
 * @author The ProActive Team
 * @see ProActiveWSCaller
 */
@PublicAPI
public class CXFWSCaller implements ProActiveWSCaller {
    public CXFWSCaller() {
    }

    public Object[] callWS(String wsUrl, String methodName, Object[] args, Class<?>[] returnTypes) {
        try {
            URL wsdlUrl = new URL(wsUrl + "?wsdl");
            DynamicClientFactory dcf = DynamicClientFactory.newInstance();
            Client client = dcf.createClient(wsdlUrl);
            return client.invoke(methodName, args);
        } catch (Exception e) {
            System.err.println("[CXF] Failed to invoke web service: " + wsUrl);
            e.printStackTrace();
            return new Object[] { null };
        }
    }
}
