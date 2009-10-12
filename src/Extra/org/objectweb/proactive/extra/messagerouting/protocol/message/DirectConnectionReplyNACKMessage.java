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


/** A {@link MessageType#DIRECT_CONNECTION_NACK} message.
 *
 * Sent by the router to the client when a direct connection
 * is not possible between the two endpoints
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionReplyNACKMessage extends DirectConnectionReplyMessage {

    /** Create a {@link MessageType#DIRECT_CONNECTION_NACK} message.
     *
     * @param messageId
     * 		The message ID of the message.
     */
    public DirectConnectionReplyNACKMessage(long messageId) {
        super(MessageType.DIRECT_CONNECTION_NACK, messageId);
        super.setLength(Message.Field.getTotalOffset());
    }

    /**
     * Construct a {@link MessageType#DIRECT_CONNECTION_NACK} message
     *    from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     * @throws MalformedMessageException if the buffer does not contain a valid {@link MessageType#DIRECT_CONNECTION_NACK} message
     */
    public DirectConnectionReplyNACKMessage(byte[] byteArray, int offset) throws MalformedMessageException {
        super(byteArray, offset, 0);

        if (this.getType() != MessageType.DIRECT_CONNECTION_NACK) {
            throw new MalformedMessageException("Malformed " + MessageType.DIRECT_CONNECTION_NACK +
                " message:" + "Invalid value for the " + Message.Field.MSG_TYPE + " field:" + this.getType());
        }
    }

    /* Marker message. Nothing special to do - just write the header! */
    @Override
    public byte[] toByteArray() {
        int length = super.getLength();
        byte[] buff = new byte[length];

        super.writeHeader(buff, 0);
        return buff;
    }

}
