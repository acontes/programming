package functionalTests.messagerouting;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.Before;
import org.objectweb.proactive.extra.forwardingv2.client.Tunnel;
import org.objectweb.proactive.extra.forwardingv2.router.Router;
import org.objectweb.proactive.extra.forwardingv2.router.RouterConfig;

import functionalTests.FunctionalTest;


public class BlackBox extends FunctionalTest {
    protected Router router;
    protected Tunnel tunnel;

    @Before
    public void beforeBlackbox() throws IOException {
        RouterConfig config = new RouterConfig();
        this.router = Router.createAndStart(config);

        this.tunnel = new Tunnel(InetAddress.getLocalHost(), this.router.getPort());
    }
}
