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
package unitTests.messagerouting.dc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;

import org.junit.Ignore;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.extra.messagerouting.client.AgentImpl;
import org.objectweb.proactive.extra.messagerouting.client.AgentInternal;
import org.objectweb.proactive.extra.messagerouting.client.RoutedMessageHandler;
import org.objectweb.proactive.extra.messagerouting.client.Tunnel;
import org.objectweb.proactive.extra.messagerouting.client.dc.client.DirectConnectionManager;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionRequestMessage;
import org.objectweb.proactive.extra.messagerouting.remoteobject.util.socketfactory.MessageRoutingPlainSocketFactory;

import unitTests.messagerouting.dc.scenarios.Infrastructure;


/**
 * Decorator of AgentImpl which offers extra access
 * to Agent implementation
 */
@Ignore
public class TestAgentImpl extends AgentImpl {

    private final Tunnel tunnel;
    private final DirectConnectionManager dcMgr;
    private final MessageReader mr;
    // these field names are subject to change with the implementation!
    private String TUNNEL_FIELD_NAME = "t";
    private String DC_MANAGER_FIELD_NAME = "dcManager";

    private String MESSAGE_READER_FIELD_NAME = "incomingHandler";

    private boolean tunnelReplaced = false;
    private Tunnel testTunnel;

    public TestAgentImpl(InetAddress routerAddr, int routerPort) throws ProActiveException,
            SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        this(routerAddr, routerPort, TestMessageHandler.class);
    }

    public TestAgentImpl(InetAddress routerAddr, int routerPort,
            Class<? extends RoutedMessageHandler> messageHandlerClass) throws ProActiveException,
            SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        super(routerAddr, routerPort, messageHandlerClass, new MessageRoutingPlainSocketFactory());
        Class<AgentImpl> agentClazz = AgentImpl.class;

        // get the hidden fields using reflection
        Field tunnelField = agentClazz.getDeclaredField(TUNNEL_FIELD_NAME);
        tunnelField.setAccessible(true);
        this.tunnel = (Tunnel) tunnelField.get(this);

        Field dcManField = agentClazz.getDeclaredField(DC_MANAGER_FIELD_NAME);
        dcManField.setAccessible(true);
        this.dcMgr = (DirectConnectionManager) dcManField.get(this);
        Field mrField = agentClazz.getDeclaredField(MESSAGE_READER_FIELD_NAME);
        mrField.setAccessible(true);
        this.mr = (MessageReader) mrField.get(this);
    }

    public DirectConnectionManager getDCManager() {
        return this.dcMgr;
    }

    public Tunnel getTheTunnel() {
        return this.tunnel;
    }

    // release the resources acquired by the Agent
    public void shutdown() {
        if (this.dcMgr.isEnabled())
            this.dcMgr.stop();
        if (this.tunnelReplaced)
            this.testTunnel.shutdown();
        this.mr.stop();
        this.tunnel.shutdown();
    }

    // send a direct connection request to the router for connecting to remoteAgent
    // wait for the router reply
    public byte[] sendDCRequest(AgentID remoteAgent) throws MessageRoutingException {

        long reqId = this.idGenerator.getAndIncrement();
        DirectConnectionRequestMessage dcReq = new DirectConnectionRequestMessage(reqId, this.getAgentID(),
            remoteAgent);

        return sendRoutingMessage(dcReq, false);

    }

    // send the shutdown signal && wait a bit, so that
    // it is processed by the receiving threads
    public void shutdownWait() {
        shutdown();
        new Sleeper(Infrastructure.TIMEOUT).sleep();
    }

    public void injectTestTunnel(InetAddress routerAddr, int routerPort) throws IOException,
            SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        this.testTunnel = new TestTunnel(routerAddr, routerPort);
        Class<AgentImpl> agentClazz = AgentImpl.class;
        Field tunnelField = agentClazz.getDeclaredField(TUNNEL_FIELD_NAME);
        tunnelField.setAccessible(true);
        tunnelField.set(this, this.testTunnel);
        this.tunnelReplaced = true;
    }

    // send an oneway message and wait until it arrives succesfully to the remote agent
    public boolean onewaySend(byte[] msg, TestAgentImpl remoteAgent, long timeout)
            throws MessageRoutingException {
        remoteAgent.registerMessageWait();
        this.sendMsg(remoteAgent.getAgentID(), msg, true);
        remoteAgent.waitForArrival(timeout);
        return remoteAgent.messageReceived();
    }

    // wait until the remote agent tries to send a reply for the request
    public boolean waitReply(TestAgentImpl remoteAgent, long timeout) {
        remoteAgent.registerMessageReply();
        remoteAgent.waitForReply(timeout);
        return remoteAgent.replySent();
    }

    private volatile boolean received = false;

    public void registerMessageWait() {
        received = false;
    }

    public void waitForArrival(long timeout) {
        synchronized (this) {
            if (!received) {
                try {
                    this.wait(timeout);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public boolean messageReceived() {
        return received;
    }

    private void notifyMessageArrival() {
        synchronized (this) {
            this.received = true;
            this.notifyAll();
        }
    }

    private volatile boolean replied = false;

    public void registerMessageReply() {
        replied = false;
    }

    public void waitForReply(long timeout) {
        synchronized (this) {
            if (!replied) {
                try {
                    this.wait(timeout);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public boolean replySent() {
        return replied;
    }

    private void notifyMessageReply(boolean replySent) {
        synchronized (this) {
            this.replied = replySent;
            this.notifyAll();
        }
    }

    public static class TestMessageHandler implements RoutedMessageHandler {

        private TestAgentImpl agent;

        public TestMessageHandler(AgentInternal agent) {
            this.agent = (TestAgentImpl) agent;
        }

        public void pushMessage(DataRequestMessage message) {
            byte[] data = message.getData();
            if (data.length == 0) {
                agent.notifyMessageArrival();
            } else {
                String request = Helpers.byteArrayToString(data);
                logger.debug("Got data request message with non-empty payload:" + request);
                try {
                    byte[] reply;
                    try {
                        long time = Long.parseLong(request);
                        // we are "working very hard" to calculate the reply
                        new Sleeper(time).sleep();
                        // we will reply with "reply"
                        reply = Helpers.stringToByteArray(Helpers.REPLY_PAYLOAD);
                    } catch (NumberFormatException e) {
                        reply = Helpers.stringToByteArray(Helpers.ERROR_PAYLOAD);
                    }
                    this.agent.sendReply(message, reply);
                    agent.notifyMessageReply(true);
                } catch (MessageRoutingException e) {
                    // could not send the reply
                    agent.notifyMessageReply(false);
                }
            }
        }
    }

}
