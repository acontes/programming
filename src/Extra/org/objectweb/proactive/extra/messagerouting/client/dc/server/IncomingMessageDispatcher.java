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
package org.objectweb.proactive.extra.messagerouting.client.dc.server;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.client.AgentImpl;
import org.objectweb.proactive.extra.messagerouting.client.AgentImpl.MessageReader;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;


/** Incoming message handler for Direct Connection Server
 * It just passes the processing of the received {@link Message}
 * to the incoming message handler attached to the local {@link AgentImpl}
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
class IncomingMessageDispatcher implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_SERVER_DC);

    /** The message to process */
    final private ByteBuffer rawMessage;

    /** The incoming messages handler */
    final private MessageReader handler;

    public IncomingMessageDispatcher(ByteBuffer message, MessageReader handler) {
        this.rawMessage = message;
        this.handler = handler;
    }

    public void run() {

        try {
            Message message = Message.constructMessage(this.rawMessage.array(), 0);

            if (logger.isTraceEnabled()) {
                logger.trace("Asynchronous handling of " + message);
            }
            // pass the handling to the local handler for incoming messages
            handler.handleMessage(message);

        } catch (MalformedMessageException e) {
            // TODO : Send an ERR_
            logger.error("Dropping message " + rawMessage + ", reason:" + e.getMessage(), e);
        }
    }
}
