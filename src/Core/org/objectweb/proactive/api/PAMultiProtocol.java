/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of
 *              Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */

package org.objectweb.proactive.api;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.UniversalBodyRemoteObjectAdapter;
import org.objectweb.proactive.core.body.proxy.UniversalBodyProxy;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.remoteobject.SynchronousProxy;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;


@PublicAPI
public class PAMultiProtocol {

    /**
     * Force usage of a specific protocol to contact a remote object.
     *
     * @param obj
     *          Could be a stub
     *
     * @param protocol
     *          Can be rmi, http, pamr, rmissh, rmissl
     *
     */
    public static void forceProtocol(Object obj, String protocol) {
        UniversalBodyProxy ubp = (UniversalBodyProxy) ((StubObject) obj).getProxy();
        UniversalBody ub = ubp.getBody();
        // Object is a stub which point to a remote Body
        if (ub instanceof UniversalBodyRemoteObjectAdapter) {
            UniversalBodyRemoteObjectAdapter ubroa = (UniversalBodyRemoteObjectAdapter) ubp.getBody();
            SynchronousProxy sp = (SynchronousProxy) ((StubObject) ubroa).getProxy();
            sp.forceProtocol(protocol);
            return;
        }

        // Object is already a body
        if (ub instanceof Body) {
            ((AbstractBody) ub).getRemoteObjectExposer().forceProtocol(protocol);
        }
    }

    public static void forceToDefault(Object obj) throws UnknownProtocolException {
        forceProtocol(obj, PAProperties.PA_COMMUNICATION_PROTOCOL.getValue());
    }
}
