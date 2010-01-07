/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
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
 * If needed, contact us to obtain a release under GPL Version 2.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package functionalTests.messagerouting.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.messagerouting.client.AgentImpl;
import org.objectweb.proactive.extra.messagerouting.client.ProActiveMessageHandler;
import org.objectweb.proactive.extra.messagerouting.remoteobject.util.socketfactory.MessageRoutingPlainSocketFactory;

import unitTests.UnitTests;


public class TestInvalidRouter extends UnitTests {

    @Test(expected = ProActiveException.class)
    public void test() throws ProActiveException, UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        new AgentImpl(localhost, 12423, ProActiveMessageHandler.class, new MessageRoutingPlainSocketFactory());
    }
}
