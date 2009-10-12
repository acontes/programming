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
import org.objectweb.proactive.extra.messagerouting.client.Agent;
import org.objectweb.proactive.extra.messagerouting.client.AgentImpl;
import org.objectweb.proactive.extra.messagerouting.client.MessageHandler;
import org.objectweb.proactive.extra.messagerouting.client.Tunnel;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataRequestMessage;


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

    private boolean tunnelStopped = false;

    public TestAgentImpl(InetAddress routerAddr, int routerPort) throws ProActiveException,
            SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        this(routerAddr, routerPort, TestMessageHandler.class);
    }

    public TestAgentImpl(InetAddress routerAddr, int routerPort,
            Class<? extends MessageHandler> messageHandlerClass) throws ProActiveException,
            SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        super(routerAddr, routerPort, messageHandlerClass);
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
        if (!this.tunnelStopped) {
            this.mr.stop();
            this.tunnel.shutdown();
        }
    }

    // shutdown the Router communication channel
    public void tunnelShutdown() {
        this.mr.stop();
        this.tunnel.shutdown();
        this.tunnelStopped = true;
    }

    public boolean onewaySend(byte[] msg, TestAgentImpl remoteAgent, long timeout)
            throws MessageRoutingException {
        remoteAgent.registerMessageWait();
        this.sendMsg(remoteAgent.getAgentID(), msg, true);
        remoteAgent.waitForArrival(timeout);
        return remoteAgent.messageReceived();
    }

    public byte[] bidirectionalSend(DataRequestMessage msg, TestAgentImpl remoteAgent, long timeout)
            throws MessageRoutingException {
        remoteAgent.registerMessageReply();
        return this.sendMsg(remoteAgent.getAgentID(), msg.toByteArray(), true); // TODO this blocks; if not unblocked, how to test that the reply was really sent???
    }

    private volatile boolean received = false;

    public void registerMessageWait() {
        received = false;
    }

    public void waitForArrival(long timeout) {
        synchronized (this) {
            while (!received) {
                try {
                    this.wait(timeout);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private boolean messageReceived() {
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
            while (!replied) {
                try {
                    this.wait(timeout);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private boolean replySent() {
        return replied;
    }

    private void notifyMessageReply(boolean replySent) {
        synchronized (this) {
            this.replied = replySent;
            this.notifyAll();
        }
    }

    public static class TestMessageHandler implements MessageHandler {

        private TestAgentImpl agent;

        public TestMessageHandler(Agent agent) {
            this.agent = (TestAgentImpl) agent;
        }

        public void pushMessage(DataRequestMessage message) {
            byte[] data = message.getData();
            if (data.length == 0) {
                agent.notifyMessageArrival();
            } else {
                logger.debug("Got data request message with non-empty payload:" +
                    Helpers.byteArrayToString(data));
                try {
                    // we are "working very hard" to calculate the reply
                    new Sleeper(1000).sleep();
                    // we will reply with null
                    this.agent.sendReply(message, null);
                    agent.notifyMessageReply(true);
                } catch (MessageRoutingException e) {
                    // could not send the reply
                    agent.notifyMessageReply(false);
                }
            }
        }
    }

}
