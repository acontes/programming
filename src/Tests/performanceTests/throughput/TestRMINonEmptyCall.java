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
package performanceTests.throughput;

import java.io.Serializable;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.NodeException;

import performanceTests.HudsonReport;
import functionalTests.GCMFunctionalTestDefaultNodes;


public class TestRMINonEmptyCall extends GCMFunctionalTestDefaultNodes {

    
    
    public TestRMINonEmptyCall() {
        super(1, 1);
    }

    @Test
    public void test() throws ActiveObjectCreationException, NodeException {
        
        
        
        Server server = (Server) PAActiveObject.newActive(Server.class.getName(), new Object[] {}, super
                .getANode());
        Client client = (Client) PAActiveObject.newActive(Client.class.getName(), new Object[] { server,1000 });
        client.startTest();
    }

    @SuppressWarnings("serial")
    static public class Server implements Serializable {
        boolean firstRequest = true;
        long count = 0;
        long startTime;
        
        public Server() {}

        public void serve(Object[] o) {
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
            System.out.println("Throughput " + throughput);
            HudsonReport.reportToHudson(TestRMI.class, throughput);
        }
    }

    @SuppressWarnings("serial")
    static public class Client implements Serializable {
        private Server server;

        private MyObj[] root;
        public Client() {

        }

        public Client(Server server,int i) {
            this.server = server;
                i = 1000;
                root = new MyObj[i];
            
                
                for (int j = 0; j< i; j++) {
                    root[j] = new MyObj();
                }
            
            
        }

        public int startTest() {
            // Warmup
            for (int i = 0; i < 10; i++) {
                server.serve(root);
            }

            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime > PAProperties.PA_TEST_PERF_DURATION
                        .getValueAsInt())
                    break;

                for (int i = 0; i < 50; i++) {
                    server.serve(root);
                }
            }
            server.finish();

            // startTest must be sync 
            return 0;
        }
    }
    
    
}
