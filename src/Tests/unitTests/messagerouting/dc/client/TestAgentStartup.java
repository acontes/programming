package unitTests.messagerouting.dc.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.extra.messagerouting.client.ProActiveMessageHandler;
import org.objectweb.proactive.extra.messagerouting.router.Router;
import org.objectweb.proactive.extra.messagerouting.router.RouterConfig;

import unitTests.UnitTests;
import unitTests.messagerouting.dc.TestAgentImpl;
import functionalTests.ft.Agent;


/*
 * OBS: this unit test should be run in a separate JVM/phase,
 *  because it alters ProActive properties in an irreversible way
 */
public class TestAgentStartup extends UnitTests {

    private Router router;
    private TestAgentImpl agent;

    private static final int DC_PORT = 18989;

    protected void startRouterAgent() throws IOException, ProActiveException {
        router = Router.createAndStart(new RouterConfig());
        try {
            agent = new TestAgentImpl(router.getInetAddr(), router.getPort(), ProActiveMessageHandler.class);
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(Agent.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(Agent.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        } catch (NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(Agent.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            Assert.fail(Agent.class.getName() +
                " implementation changed. This unit test should also be re-implemented.");
        }
    }

    // try to connect to the DC server at the given address and port
    private boolean checkDCServerStarted(InetAddress inetAddress, int port) {
        try {
            Socket socket = new Socket(inetAddress, port);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // start with DC disabled
    @Test
    public void startNoDC() throws IOException, ProActiveException {
        // start with DC flag unset
        if (PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.isSet()) {
            PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.setValue(false);
        }
        startRouterAgent();
        Assert.assertFalse(agent.getDCManager().isEnabled());
        router.stop();
        agent.shutdown();

        // explicitly set DC flag to false
        PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.setValue(false);
        startRouterAgent();
        Assert.assertFalse(agent.getDCManager().isEnabled());
        router.stop();
        agent.shutdown();
    }

    // start DC but "forget" to put the mandatory DC port
    @Test
    public void startDCMissingPort() throws IOException, ProActiveException {
        PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.setValue(true);
        // make sure DC port is not set
        if (PAProperties.PA_NET_ROUTER_DC_PORT.isSet()) {
            // no equivalent of :
            // System.clearProperty(PAProperties.PA_NET_ROUTER_DC_PORT.getKey());
            // in PAProperties
            PAProperties.PA_NET_ROUTER_DC_PORT.setValue("");
        }
        inspectDCState(false);
        logger.info("If you just saw a stacktrace, it's normal.");
    }

    // start with DC enabled for server and client also
    @Test
    public void startDC() throws IOException, ProActiveException {
        // run the test only if DC_PORT is free; we don't test here port election
        if (!freePort(DC_PORT)) {
            return;
        }
        PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.setValue(true);
        PAProperties.PA_NET_ROUTER_DC_PORT.setValue(DC_PORT);
        inspectDCState(true);
    }

    // start DC but try to bind to an invalid IP address
    @Test
    public void startDCInvalidAddr() throws IOException, ProActiveException {
        PAProperties.PA_NET_ROUTER_DIRECT_CONNECTION.setValue(true);
        String oldAddr = System.getProperty(PAProperties.PA_NET_ROUTER_DC_ADDRESS.getKey());
        // bind to an invalid IP address
        PAProperties.PA_NET_ROUTER_DC_ADDRESS.setValue("dumb");
        inspectDCState(false);
        logger.info("If you just saw a stacktrace, it's normal.");

        if (oldAddr != null)
            PAProperties.PA_NET_ROUTER_DC_ADDRESS.setValue(oldAddr);
        else
            System.clearProperty(PAProperties.PA_NET_ROUTER_DC_ADDRESS.getKey());
    }

    // after starting , we check to see if the DC manager is started, and
    // we test the DC server
    private void inspectDCState(boolean serverStartedExpect) throws IOException, ProActiveException {
        startRouterAgent();
        Assert.assertTrue(agent.getDCManager().isEnabled());
        boolean serverStarted = checkDCServerStarted(InetAddress.getLocalHost(), DC_PORT);
        Assert.assertEquals(serverStartedExpect, serverStarted);
        agent.shutdownWait();
        if (serverStartedExpect)
            // after we stopped, we are expecting not to have any DC server up and running!
            Assert.assertFalse(checkDCServerStarted(InetAddress.getLocalHost(), DC_PORT));
        // stop the router
        router.stop();
    }

    private Properties sysProps;

    @Before
    public void stashPAProperties() {
        sysProps = System.getProperties();
    }

    @After
    public void revertPAProperties() {
        System.setProperties(sysProps);
    }

    // test if the given port is not occupied on the localhost
    private boolean freePort(int port) {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            Socket socket = new Socket(localhost, port);
            socket.close();
            return false;
        } catch (UnknownHostException e) {
            // cannot get the localhost address => we cannot test => assume ok
            return true;
        } catch (IOException e) {
            // expect to fail
            return true;
        }
    }

}
