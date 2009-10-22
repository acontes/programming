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
package org.objectweb.proactive.extra.messagerouting.client.dc.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.Sleeper;
import org.objectweb.proactive.core.util.SweetCountDownLatch;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.client.AgentInternal;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionReplyACKMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/**
 * The DirectConnectionNegotiator is responsible for establishing
 * 	new outgoing direct connections to other remote agents  
 * 
 * It constantly queries for new agents and tries to establish
 * new Direct Connections with them.
 * 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionNegotiator implements Runnable {

    public final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT_DC);

    // the local agent
    private final AgentInternal localAgent;
    // true if we have done resource cleanup
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final SweetCountDownLatch isStopped = new SweetCountDownLatch(1);
    private final AtomicReference<Thread> negotiatorThread = new AtomicReference<Thread>();
    private static final long CLEANUP_WAITING_TIME = 100L;
    // provide a shutdown hook for users
    private final Thread shutdownHook;

    private final DirectConnectionManager dcManager;

    public DirectConnectionNegotiator(DirectConnectionManager dcMan, AgentInternal currentAgent) {
        this.dcManager = dcMan;
        this.localAgent = currentAgent;
        this.shutdownHook = new Thread(new ShutdownHook(this));
    }

    public Thread getShutdownHook() {
        return this.shutdownHook;
    }

    private class ShutdownHook implements Runnable {

        private final DirectConnectionNegotiator dcNegotiator;

        public ShutdownHook(DirectConnectionNegotiator dcNegotiator) {
            this.dcNegotiator = dcNegotiator;
        }

        @Override
        public void run() {
            if (dcNegotiator.stopped.get() == false)
                this.dcNegotiator.stop();
        }

    }

    @Override
    public void run() {

        boolean r = this.negotiatorThread.compareAndSet(null, Thread.currentThread());
        if (r == false) {
            logger
                    .error(
                            "A direct connections negotiator thread has already been started, aborting the current thread ",
                            new Exception());
            return;
        }

        while (this.stopped.get() == false) {
            try {
                AgentID seenAgent = dcManager.waitForNewAgent();
                // negotiate direct connection for the taken agent
                negotiateDirectConnection(seenAgent);
            } catch (InterruptedException e) {
                // interrupted => give time to check the stopped field
            }
        }

        // clean up the mess 
        this.cleanup();
    }

    /*
     * Negotiate with the remote router for obtaining 
     * 	a direct connection to the given agent
     * Negotiating means sending a DC_REQ message to the router
     * 	and waiting for the router reply. If the router replies with
     *  DC_ACK then direct communication is allowed. 
     * In all other cases - problems while sending the message
     * 	to the router, router replies with DC_NACK,
     *  direct connection cannot be established with the
     *  remote agent - it is supposed that direct connection
     *  is not possible with the remote agent. In this case, 
     *  the agent is put in the known (black)list  
     */
    private void negotiateDirectConnection(AgentID seenAgent) {
        try {
            byte[] result = this.sendDCRequest(seenAgent);
            DirectConnectionReplyACKMessage replyMsg = (DirectConnectionReplyACKMessage) Message
                    .constructMessage(result, 0);
            InetAddress inetAddr = replyMsg.getInetAddress();
            int port = replyMsg.getPort();
            DirectConnection connection = tryConnection(seenAgent, inetAddr, port);
            this.dcManager.directConnectionAllowed(seenAgent, connection);
            return;
        } catch (MessageRoutingException e) {
            logger.info("Direct connection refused for agent " + seenAgent + " reason:" + e.getMessage());
            this.dcManager.directConnectionRefused(seenAgent);
        } catch (MalformedMessageException e) {
            logger.error(" Cannot reconstruct a " + MessageType.DIRECT_CONNECTION_ACK +
                " message from its raw byte representation, reason: " + e.getMessage());
            logger.error("This is probably a bug. Check out also the stacktrace:", e);
            this.dcManager.directConnectionRefused(seenAgent);
        } catch (ClassCastException e) {
            logger.error(" Cannot reconstruct a " + MessageType.DIRECT_CONNECTION_ACK +
                " message from its raw byte representation, reason: " + e.getMessage());
            logger.error("This is probably a bug. Check out also the stacktrace:", e);
            this.dcManager.directConnectionRefused(seenAgent);
        } catch (IOException e) {
            logger.info("All attempts to directly connect to remote router " + seenAgent + " failed." +
                "Direct connection will be disallowed for agent " + seenAgent);
            this.dcManager.directConnectionRefused(seenAgent);
        }
    }

    private byte[] sendDCRequest(AgentID seenAgent) throws MessageRoutingException {
        long reqId = this.localAgent.getIDGenerator().getAndIncrement();
        DirectConnectionRequestMessage dcReq = new DirectConnectionRequestMessage(reqId, this.localAgent
                .getAgentID(), seenAgent);

        return this.localAgent.sendRoutingMessage(dcReq, false);
    }

    // try to establish a direct connection
    // it will try two times in a interval of 3s
    // if both attempts will fail, will throw an IOException
    private DirectConnection tryConnection(AgentID remoteAgent, InetAddress inetAddr, int port)
            throws IOException {
        try {
            return new DirectConnection(inetAddr, port);
        } catch (IOException e) {
            logger.warn("First attempt to establish a direct connection with agent " + remoteAgent +
                " failed, because " + e.getMessage());
            logger.debug("Stacktrace is:", e);
            logger.warn("Trying again in 3 seconds...");
            new Sleeper(3000).sleep();
            try {
                return new DirectConnection(inetAddr, port);
            } catch (IOException e1) {
                logger.warn("Second attempt to establish a direct connection with agent " + remoteAgent +
                    " failed, because " + e.getMessage());
                logger.debug("Stacktrace is:", e);
                logger.warn("Agent " + remoteAgent + " will be considered unreachable");
                throw e1;
            }
        }
    }

    private void cleanup() {
        // NOTE additional cleanup here

        this.isStopped.countDown();
    }

    public void stop() {
        if (this.stopped.get() == true)
            throw new IllegalStateException("Already stopped");

        this.stopped.set(true);

        Thread t = this.negotiatorThread.get();
        if (t != null) {
            t.interrupt();
            this.isStopped.await(CLEANUP_WAITING_TIME, TimeUnit.MILLISECONDS);
        }
    }

}