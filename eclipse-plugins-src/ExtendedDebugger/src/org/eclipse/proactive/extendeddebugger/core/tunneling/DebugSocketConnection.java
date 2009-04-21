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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.eclipse.proactive.extendeddebugger.core.tunneling;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.debug.dconnection.DebuggerInformation;
import org.objectweb.proactive.core.jmx.mbean.ProActiveRuntimeWrapperMBean;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;



public class DebugSocketConnection {

    public final static int NB_TRIES = 3;
    private ProActiveRuntimeWrapperMBean proxyMBean = null;

    /**
     * A variable used to avoid garbage collection of the
     * {@link DebuggerSocketServer}
     */
    private DebuggerSocketServer server = null;

    /**
     * A variable used to avoid garbage collection of the
     * {@link DebuggerSocketClient}
     */
    private DebuggerSocketClient client = null;

    public DebugSocketConnection(ProActiveRuntimeWrapperMBean proxyMBean){
    	this.proxyMBean = proxyMBean;
    }

    /**
     * Create a tunnel between the JVM of the object and the IC2D host
     */
    public void connectSocketDebugger() {
        DebuggerInformation nodeInfo = null;

        // create a feedback popup
//        TunnelingCreationWaitingDialog t = new TunnelingCreationWaitingDialog(PlatformUI.getWorkbench()
//                .getActiveWorkbenchWindow().getShell());
        int nbOfTry = 0;
        try {

            while (nbOfTry++ < NB_TRIES) {
//                t.labelUp(nbOfTry); // update the popup
                nodeInfo = proxyMBean.getDebugInfo();
                if ((nodeInfo != null) && (nodeInfo.getDebuggerNode() != null)) {
                    break;
                }
                System.out.println("nodeInfo: " + nodeInfo);
                if (nbOfTry >= NB_TRIES) {
                    throw new TunnelingTimeOutException("Reached the maximum connection attempt number (" +
                        nbOfTry + ")");
                }
            }
            int port = nodeInfo.getDebuggeePort();

            Node node = nodeInfo.getDebuggerNode();
            System.out.println("remote port: " + port);
            System.out.println("debug node: " + node);
            System.out.println("--");

            server = (DebuggerSocketServer) PAActiveObject.newActive(DebuggerSocketServer.class.getName(),
                    null);
            client = (DebuggerSocketClient) PAActiveObject.newActive(DebuggerSocketClient.class.getName(),
                    new Object[] {}, node);
            server.setTarget(client);
            client.setTarget(server);
            // server will be connected on a random port
            server.setPort(0);
            client.setPort(port);
            // client will be connected on its host
            client.setHost("localhost");
            server.connect();
            System.out.println("client port = " + client.getPort());
            System.out.println("server port = " + server.getPort());

//            t.close(); //close the popup
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (TunnelingTimeOutException e) {
//            t.showError();
        }
    }

	public void removeDebugger() {
        if (client != null) {
            try {
                client.closeConnection();
                client.terminate();
            } catch (Exception e) {
            }
            client = null;
        }
        if (server != null) {
            try {
                server.terminate();
            } catch (Exception e) {
            }
            server = null;
        }
        proxyMBean.removeDebugger();
    }

    public boolean hasDebuggerConnected() {
        return proxyMBean.hasDebuggerConnected();
    }
    
    public DebuggerSocketServer getServer() {
		return server;
	}

	public DebuggerSocketClient getClient() {
		return client;
	}
}
