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
package org.objectweb.proactive.extra.forwardingv2.test.perf;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extra.forwardingv2.registry.ForwardingRegistry;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


@SuppressWarnings("serial")
public class TestPerformances {

    final static public byte buf[] = new byte[10 * 1024 * 1024]; // 1Mo
    private static ForwardingRegistry reg;
    private static Node remoteNode;

    static {
        PAProperties.PA_COMMUNICATION_PROTOCOL.setValue("pamr");
        PAProperties.PA_NET_ROUTER_ADDRESS.setValue("localhost");
    }

    @BeforeClass
    public static void init() throws IOException, ProActiveException {
        reg = new ForwardingRegistry(0, true);

        PAProperties.PA_NET_ROUTER_PORT.setValue(reg.getLocalPort());

        System.out.println("Registry started on port " + reg.getLocalPort() + ".");

        VariableContractImpl vc = new VariableContractImpl();
        vc.setVariableFromProgram("router.address", PAProperties.PA_NET_ROUTER_ADDRESS.getValue(),
                VariableContractType.ProgramVariable);
        vc.setVariableFromProgram("router.port", PAProperties.PA_NET_ROUTER_PORT.getValue(),
                VariableContractType.ProgramVariable);

        URL gcmaUrl = TestPerformances.class.getResource("gcma.xml");
        GCMApplication gcma = PAGCMDeployment.loadApplicationDescriptor(gcmaUrl, vc);
        gcma.startDeployment();
        GCMVirtualNode vn = gcma.getVirtualNode("vn");
        remoteNode = vn.getANode();
        System.err.println("Distant node created.");
    }

    @AfterClass
    public static void cleanup() {
        reg.stop();
        System.out.println("Registry on port " + reg.getLocalPort() + " stopped.");
    }

    @Test
    public void testBandwidth() throws ActiveObjectCreationException, NodeException {
        BandwidthServer server = (BandwidthServer) PAActiveObject.newActive(BandwidthServer.class.getName(),
                new Object[] {}, remoteNode);
        BandwidthClient client = (BandwidthClient) PAActiveObject.newActive(BandwidthClient.class.getName(),
                new Object[] { server });
        client.startTest();
    }

    @Test
    public void testThroughput() throws ActiveObjectCreationException, NodeException {
        // Creating Client and Server
        ThroughputServer server = (ThroughputServer) PAActiveObject.newActive(ThroughputServer.class
                .getName(), new Object[] {}, remoteNode);
        ThroughputClient client = (ThroughputClient) PAActiveObject.newActive(ThroughputClient.class
                .getName(), new Object[] { server });

        // Start Test
        client.startTest();
    }

    static public class ThroughputServer implements Serializable {
        boolean firstRequest = true;
        long count = 0;
        long startTime;

        public ThroughputServer() {

        }

        public void serve() {
            if (firstRequest) {
                startTime = System.currentTimeMillis();
                firstRequest = false;
            }

            count++;
        }

        public void finish() {
            long endTime = System.currentTimeMillis();
            double throughput = (1000.0 * count) / (endTime - startTime);

            System.out.println("Count: " + count);
            System.out.println("Duration: " + (endTime - startTime));
            System.out.println("Throughput = " + throughput);
        }
    }

    static public class ThroughputClient implements Serializable {
        private ThroughputServer server;

        public ThroughputClient() {

        }

        public ThroughputClient(ThroughputServer server) {
            this.server = server;
        }

        public int startTest() {
            // Warmup
            for (int i = 0; i < 1000; i++) {
                server.serve();
            }
            System.out.println("End of warmup");

            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime > PAProperties.PA_TEST_PERF_DURATION
                        .getValueAsInt())
                    break;

                for (int i = 0; i < 50; i++) {
                    server.serve();
                }
            }
            server.finish();

            // startTest must be sync 
            return 0;
        }
    }

    static public class BandwidthServer implements Serializable {
        boolean firstRequest = true;
        long count = 0;
        long startTime;

        public BandwidthServer() {

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
            double size = (1.0 * TestPerformances.buf.length * count) / (1024 * 1024);

            System.out.println("Size: " + size);
            System.out.println("Duration: " + (endTime - startTime));
            double bandwith = (1000.0 * size) / (endTime - startTime);
            System.out.println("Bandwidth = " + bandwith);
        }
    }

    static public class BandwidthClient implements Serializable {
        private BandwidthServer server;

        public BandwidthClient() {

        }

        public BandwidthClient(BandwidthServer server) {
            this.server = server;
        }

        public int startTest() {
            // Warmup
            for (int i = 0; i < 10; i++) {
                server.serve(TestPerformances.buf);
            }
            System.out.println("End of warmup");

            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime > PAProperties.PA_TEST_PERF_DURATION
                        .getValueAsInt())
                    break;

                server.serve(TestPerformances.buf);
            }
            server.finish();

            // startTest must be sync 
            return 0;
        }
    }
}
