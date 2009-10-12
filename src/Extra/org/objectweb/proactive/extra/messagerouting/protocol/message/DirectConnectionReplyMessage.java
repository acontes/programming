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
package org.objectweb.proactive.extra.messagerouting.protocol.message;

import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/** A DirectConnectionReply message
 *
 * Sent back by the router to the client in response to a previous
 * {@link MessageType#DIRECT_CONNECTION_REQUEST} received from the client
 *
 * If the direct connection can be established, the router will reply
 * with a {@link MessageType#DIRECT_CONNECTION_ACK} message.
 * Otherwise, the router will reply with a
 * {@link MessageType#DIRECT_CONNECTION_NACK} message
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public abstract class DirectConnectionReplyMessage extends DirectConnectionMessage {

    /** Create a DirectConnectionReply message
     *
     * @param type
     * 		Type of the message {@link MessageType#DIRECT_CONNECTION_ACK}
     * 			or {@link MessageType#DIRECT_CONNECTION_NACK}
     * @param messageId
     * 		The message ID of the message.
     */
    public DirectConnectionReplyMessage(MessageType type, long messageId) {
        super(type, messageId);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     * @param fieldsOffset the size of the additional fields added by the messages
     * @throws MalformedMessageException for malformed messages
     */
    public DirectConnectionReplyMessage(byte[] byteArray, int offset, int fieldsOffset)
            throws MalformedMessageException {
        super(byteArray, offset, fieldsOffset);
    }

}
