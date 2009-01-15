/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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
package performanceTests.bandwidth;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.MessageRoutingRemoteObjectFactory;

import performanceTests.HudsonReport;
import performanceTests.Performance;
import functionalTests.GCMFunctionalTestDefaultNodes;


@SuppressWarnings("serial")
public class TestMessageRouting extends GCMFunctionalTestDefaultNodes {
    final static public byte buf[] = new byte[10 * 1024 * 1024]; // 1Mo

    static {
        System.err.println("PLOP STATIC");
        PAProperties.PA_COMMUNICATION_PROTOCOL.setValue("pamr");
        PAProperties.PA_NET_ROUTER_PORT.setValue(0);
        PAProperties.PA_NET_ROUTER_ADDRESS.setValue("localhost");
    }

    public TestMessageRouting() throws IOException {
        super(1, 1);
        super.vContract.setVariableFromProgram(super.VAR_JVMARG, PAProperties.PA_COMMUNICATION_PROTOCOL
                .getCmdLine() +
            MessageRoutingRemoteObjectFactory.PROTOCOL_ID, VariableContractType.DescriptorDefaultVariable);
    }

    @Test
    public void test() throws ActiveObjectCreationException, NodeException {
        Server server = (Server) PAActiveObject.newActive(Server.class.getName(), new Object[] {}, super
                .getANode());
        Client client = (Client) PAActiveObject.newActive(Client.class.getName(), new Object[] { server });
        client.startTest();
    }

    static public class Server implements Serializable {
        boolean firstRequest = true;
        long count = 0;
        long startTime;

        public Server() {

        }

        public int serve(byte[] buf) {
            if (firstRequest) {
                startTime = System.currentTimeMillis();
                firstRequest = false;
            }

            count++;
            return 0;
        }

        public void finish() {
            long endTime = System.currentTimeMillis();
            double size = (1.0 * TestRMI.buf.length * count) / (1024 * 1024);

            System.out.println("Size: " + size);
            System.out.println("Duration: " + (endTime - startTime));

            double bandwith = (1000.0 * size) / (endTime - startTime);
            System.out.println("Bandwidth " + bandwith);
            HudsonReport.reportToHudson(TestMessageRouting.class, bandwith);
        }
    }

    static public class Client implements Serializable {
        private Server server;

        public Client() {

        }

        public Client(Server server) {
            this.server = server;
        }

        public int startTest() {
            // Warmup
            for (int i = 0; i < 10; i++) {
                server.serve(TestRMI.buf);
            }
            System.out.println("End of warmup");

            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime > Performance.DURATION)
                    break;

                server.serve(TestRMI.buf);
            }
            server.finish();

            // startTest must be sync 
            return 0;
        }
    }
}
