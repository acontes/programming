package functionalTests.messagerouting;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.Before;
import org.objectweb.proactive.extra.forwardingv2.client.Tunnel;
import org.objectweb.proactive.extra.forwardingv2.router.Router;
import org.objectweb.proactive.extra.forwardingv2.router.RouterImpl;

import functionalTests.FunctionalTest;


public class BlackBox extends FunctionalTest {
    protected Router router;
    protected Tunnel tunnel;

    @Before
    public void beforeBlackbox() throws IOException {
        this.router = new RouterImpl(0);
        Thread t = new Thread(router);
        t.setDaemon(true);
        t.setName("Router");
        t.start();

        this.tunnel = new Tunnel(InetAddress.getLocalHost(), this.router.getLocalPort());
    }
}
