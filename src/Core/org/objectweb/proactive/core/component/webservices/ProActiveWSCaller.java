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

import org.apache.log4j.Logger;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Interface that the class used to call web services through web service binding must implements.
 *
 * @author The ProActive Team
 * @see Axis2WSCaller
 * @see CXFWSCaller
 */
@PublicAPI
//@snippet-start proactivewscaller
public interface ProActiveWSCaller {
    static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_REQUESTS);

    /**
     * Method to setup the caller.
     *
     * @param serviceClass Class that the web service implements. Should match with
     * the client interface to bind to the web service.
     * @param wsUrl URL of the web service (not the WSDL address).
     */
    public void setup(Class<?> serviceClass, String wsUrl);

    /**
     * Method to call a web service.
     *
     * @param methodName Name of the service to call.
     * @param args Arguments of the web service.
     * @param returnTypes Array of classes of the return types of the web service. Null if the
     * web service does not return any result.
     * @return Results of the call to the web service if there is, null otherwise or if the
     * invocation failed.
     */
    public Object callWS(String methodName, Object[] args, Class<?> returnTypes);
}
//@snippet-end proactivewscaller
