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
package org.objectweb.proactive.extra.messagerouting.router.processor;

import java.nio.ByteBuffer;

import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.ErrorMessage.ErrorType;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.messagerouting.router.Client;
import org.objectweb.proactive.extra.messagerouting.router.RouterImpl;


/** Asynchronous handler for {@link MessageType#DATA_REQUEST}
 * 
 * @since ProActive 4.1.0
 */
public class ProcessorDataRequest extends Processor {

    public ProcessorDataRequest(ByteBuffer messageAsByteBuffer, RouterImpl router) {
        super(messageAsByteBuffer, router);
    }

    @Override
    public void process() {
        AgentID recipient = DataMessage.readRecipient(rawMessage.array(), 0);
        Client destClient = this.router.getClient(recipient);

        if (destClient != null) {
            /* The recipient is known. Try to forward the message.
             * If an error occurs while sending the message, notify the sender
             */
            try {
                destClient.sendMessage(this.rawMessage);
            } catch (Exception e) {
                /* Notify the sender of the failure.
                 * If the error message cannot be send, the message is cached to be re-send
                 * later. If this message is lost, the caller will be blocked forever.
                 */
                AgentID sender = DataMessage.readSender(rawMessage.array(), 0);
                long messageId = Message.readMessageID(rawMessage.array(), 0);
                ErrorMessage error = new ErrorMessage(ErrorType.ERR_NOT_CONNECTED_RCPT, sender, recipient,
                    messageId);

                Client srcClient = router.getClient(sender);
                srcClient.sendMessageOrCache(error.toByteArray());
            }
        } else {
            /* The recipient is unknown.
             * If the sender is known an error message is sent (or cached) to unblock it.
             * Otherwise the message is dropped (unknown sender & recipient: game over)
             */
            AgentID sender = DataMessage.readSender(rawMessage.array(), 0);
            Client client = router.getClient(sender);
            if (client != null) {
                long messageId = Message.readMessageID(rawMessage.array(), 0);
                ErrorMessage error = new ErrorMessage(ErrorType.ERR_UNKNOW_RCPT, sender, recipient, messageId);
                // Cache on error to avoid a blocked a sender
                client.sendMessageOrCache(error.toByteArray());
                logger.warn("Received invalid data request: unknown recipient: " + recipient +
                    ". Sender notified");
            } else {
                // Something is utterly broken: Unknown sender & recipient
                try {
                    Message message;
                    message = new DataRequestMessage(rawMessage.array(), 0);
                    logger.error("Dropped invalid data request: unknown sender and recipient. " + message);
                } catch (IllegalArgumentException e) {
                    ProActiveLogger.logImpossibleException(logger, e);
                }
            }
        }

    }
}
