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
package performanceTests.throughput;

import org.objectweb.proactive.core.config.PAProperties;


public class TestMessageRoutingDirectConnection extends Throughput {
    static {
        PAProperties.PA_COMMUNICATION_PROTOCOL.setValue("pamr");
        if (!PAProperties.PA_TEST_PAMR_ROUTER_STARTED.isTrue())
            PAProperties.PA_NET_ROUTER_ADDRESS.setValue("localhost");
        PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.setValue(true);
        if (!PAProperties.PA_NET_ROUTER_DC_PORT.isSet())
            PAProperties.PA_NET_ROUTER_DC_PORT.setValue(18989); // to be revised
    }

    public TestMessageRoutingDirectConnection() {
        super(TestMessageRoutingDirectConnection.class);
    }
}
