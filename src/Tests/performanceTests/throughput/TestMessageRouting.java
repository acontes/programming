package performanceTests.throughput;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;

import performanceTests.HudsonReport;
import performanceTests.Performance;

import functionalTests.GCMFunctionalTestDefaultNodes;


/**
 * Perfomance Test using MessageRouting protocol
 * @author homerunisgood
 *
 */
public class TestMessageRouting extends GCMFunctionalTestDefaultNodes {

    public TestMessageRouting() throws IOException {
        super(1, 1);
        //router = new ForwardingRegistry(PAProperties.PA_NET_ROUTER_PORT.getValueAsInt(), false);
    }

    @Test
    public void testMessageRouting() throws ActiveObjectCreationException, NodeException {
        // Creating Client and Server
        Server server = (Server) PAActiveObject.newActive(Server.class.getName(), new Object[] {}, super
                .getANode());
        Client client = (Client) PAActiveObject.newActive(Client.class.getName(), new Object[] { server });

        // Start Test
        client.startTest();
    }

    static public class Server implements Serializable {
        boolean firstRequest = true;
        long count = 0;
        long startTime;

        public Server() {

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
            System.out.println("Throughput " + throughput);
            HudsonReport.reportToHudson(TestMessageRouting.class, throughput);
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
            for (int i = 0; i < 1000; i++) {
                server.serve();
            }
            System.out.println("End of warmup");

            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime > Performance.DURATION)
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

}
