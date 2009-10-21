/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
 */
package functionalTests.messagerouting.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServerConfig;
import org.objectweb.proactive.extra.messagerouting.client.dc.server.DirectConnectionServerConfig.DirectConnectionDisabledException;


/**
 * Test the way that the port onto which the DirectConnectionServer
 * binds onto is chosen
 */
public class TestDCPortElection {

    static final private Logger logger = Logger.getLogger("testsuite");

    private static final int DC_PORT = 18989;
    private static final int RANGE = 3;

    @Test
    public void occupyFree() throws UnknownHostException, DirectConnectionDisabledException {
        PAProperties.PA_PAMR_DIRECT_CONNECTION.setValue(true);
        PAProperties.PA_PAMR_DC_PORT.setValue(DC_PORT);
        DirectConnectionServerConfig cfg = new DirectConnectionServerConfig();
        // it should not throw an exception, and it should elect the DC_PORT
        Assert.assertEquals(DC_PORT, cfg.getPort());
    }

    @Test
    public void occupyAvailable() throws IOException {
        // occupy the port
        occupyPort(DC_PORT);
        DirectConnectionServerConfig cfg = new DirectConnectionServerConfig(DC_PORT, DC_PORT + 1);
        // it should not throw an exception, and it should elect a different port
        Assert.assertFalse("The same port was elected:" + DC_PORT, DC_PORT == cfg.getPort());
        Assert.assertTrue("The elected port " + cfg.getPort() + " is not within the range ",
                cfg.getPort() == DC_PORT + 1);
        // the elected port should also be free!
        Assert.assertTrue("The elected port " + cfg.getPort() + " is not free!", testFreePort(cfg.getPort()));
        logger.info("Elected port:" + cfg.getPort());
        releasePort();
    }

    // occupy a range of ports
    // an attempt to find a free DC port within the range should fail
    @Test(expected = IllegalArgumentException.class)
    public void failRange() throws IOException, IllegalArgumentException {
        int lower = DC_PORT - RANGE;
        int upper = DC_PORT + RANGE;
        occupyPortRange(lower, upper);
        try {
            new DirectConnectionServerConfig(lower, upper);
            releasePortRange();
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            releasePortRange();
            throw e;
        }
    }

    @Test
    public void illegalValues() throws IOException {
        // occupy the port
        occupyPort(DC_PORT);
        try {
            new DirectConnectionServerConfig(DC_PORT, DC_PORT);
            Assert.fail("Single port range on occupied port should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }
        releasePort();

        try {
            new DirectConnectionServerConfig(DC_PORT + 1, DC_PORT);
            Assert.fail("Illegal range should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new DirectConnectionServerConfig(0, DC_PORT);
            Assert.fail("Illegal range should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new DirectConnectionServerConfig(DC_PORT, 65536);
            Assert.fail("Illegal range should fail");
        } catch (IllegalArgumentException e) {
            // ok
        }

    }

    private Properties sysProps;
    private ServerSocket ss;

    @Before
    public void stashPAProperties() {
        sysProps = System.getProperties();
    }

    @After
    public void revertPAProperties() {
        System.setProperties(sysProps);
    }

    private void releasePort() throws IOException {
        if (ss != null)
            ss.close();
    }

    private void occupyPort(int dcPort) {
        try {
            ss = new ServerSocket(dcPort);
        } catch (IOException e) {
            // already occupied. good.
            ss = null;
        }
    }

    private List<ServerSocket> lss = new ArrayList<ServerSocket>();

    private void releasePortRange() throws IOException {
        for (ServerSocket ss : lss) {
            ss.close();
        }
    }

    private void occupyPortRange(int lower, int upper) {
        for (int port = lower; port <= upper; port++) {
            try {
                ServerSocket s = new ServerSocket(port);
                lss.add(s);
            } catch (IOException e) {
                // already occupied. good.
            }
        }
    }

    // test if the given port is not occupied on the localhost
    private boolean testFreePort(int port) {
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
