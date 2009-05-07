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
package org.objectweb.proactive.extra.messagerouting.protocol.message;

import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;


/** A {@link MessageType#DATA_REQUEST} message
 * 
 * @since ProActive 4.1.0
 */
public class DataRequestMessage extends DataMessage {

    /** Create a {@link MessageType#DATA_REQUEST} message
     * 
     * @param sender
     * 		sender of the request
     * @param recipient
     * 		recipient of the request
     * @param msgID
     * 		an unique ID per sender.
     * @param data
     * 		the payload
     */
    public DataRequestMessage(AgentID sender, AgentID recipient, long msgID, byte[] data) {
        super(MessageType.DATA_REQUEST, sender, recipient, msgID, data);
    }

    /** Create a {@link MessageType#DATA_REQUEST} message from a byte array
     * 
     * @param buf 
     *		a buffer which contains a message
     * @param offset 
     * 		the offset at which the message begins  
     * @throws IllegalArgumentException
     * 		If the buffer does not match message requirements (proto ID, length etc.)
     */
    public DataRequestMessage(byte[] byteArray, int offset) throws IllegalArgumentException {
        super(byteArray, offset);

        if (this.getType() != MessageType.DATA_REQUEST) {
            throw new IllegalArgumentException("Invalid message type " + this.getType());
        }
    }
}
