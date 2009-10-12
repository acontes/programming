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


/** Direct Connection generic message
 *
 * Used during the process of negotiation between the {@link Agent} and the {@link Router}
 * in order to see if it is possible for the Agent to connect directly to the Remote Agent,
 * without passing through the router.
 *
 * The general process of negotiation :
 * <ul>
 * 	<li>A client wishing to be directly connected sends a
 * 		{@link MessageType#DIRECT_CONNECTION_ADVERTISE} message to the router</li>
 *  <li>A client that wants to contact a remote client for the first time
 *  	sends a {@link MessageType#DIRECT_CONNECTION_REQUEST} to the router</li>
 *  <li>If the remote client allows direct connections, the router replies with a
 *  	{@link MessageType#DIRECT_CONNECTION_ACK} message </li>
 *  <li>If the remote client does not allow direct connections, the router
 *  	replies with a {@link MessageType#DIRECT_CONNECTION_NACK} </li>
 * </ul>
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public abstract class DirectConnectionMessage extends Message {

    /** Create a direct connection message.
     *
     * @param type
     * 		Type of the message {@link MessageType#DIRECT_CONNECTION_REQUEST} or
     * 		{@link MessageType#DIRECT_CONNECTION_ACK} or {@link MessageType#DIRECT_CONNECTION_NACK}
     * @param messageId
     * 		The message ID of the message.
     */
    public DirectConnectionMessage(MessageType type, long messageId) {
        super(type, messageId);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     * @param fieldsOffset the size of the additional fields added by the messages
     * @throws MalformedMessageException if the byte buffer does not contain a valid message
     */
    public DirectConnectionMessage(byte[] byteArray, int offset, int fieldsOffset)
            throws MalformedMessageException {
        super(byteArray, offset, fieldsOffset);
    }
}
