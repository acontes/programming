package performanceTests.throughput;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.xml.VariableContractType;

import performanceTests.HudsonReport;
import performanceTests.Performance;

import functionalTests.GCMFunctionalTestDefaultNodes;


/**
 * Perfomance Test using MessageRouting protocol
 * @author homerunisgood
 *
 */
public class TestMessageRouting extends GCMFunctionalTestDefaultNodes {

    static {
//        PAProperties.PA_COMMUNICATION_PROTOCOL.setValue("pamr");
//        PAProperties.PA_NET_ROUTER_PORT.setValue(0);
//        PAProperties.PA_NET_ROUTER_ADDRESS.setValue("localhost");
    }

    public TestMessageRouting() throws IOException {
        super(1, 1);
        super.vContract.setVariableFromProgram(super.VAR_JVMARG, PAProperties.PA_COMMUNICATION_PROTOCOL
                .getCmdLine() +
            "pamr", VariableContractType.DescriptorDefaultVariable);
        super.vContract.setVariableFromProgram("router.address", PAProperties.PA_NET_ROUTER_ADDRESS
                .getValue(), VariableContractType.ProgramVariable);
        super.vContract.setVariableFromProgram("router.port", PAProperties.PA_NET_ROUTER_PORT.getValue(),
                VariableContractType.ProgramVariable);
    }

    @Test
    public void test() throws ActiveObjectCreationException, NodeException {
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
                System.out.println("warmup");
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
