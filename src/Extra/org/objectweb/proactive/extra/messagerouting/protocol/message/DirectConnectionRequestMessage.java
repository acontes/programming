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
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.TypeHelper;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/** A {@link MessageType#DIRECT_CONNECTION_REQUEST} message
 * Will be sent by a client wishing to establish a direct connection with another client
 *
 * Format of the message:
 *
 * 			   DC_REQ
 *               |
 * 0      4    8 |  12   16   20
 *  +----+----+----+----+----+
 *  + LEN+PROT+TYPE+    ID   +
 *  +----+----+----+----+----+
 *  +  AG_ID  + R_AG_ID +
 *  +----+----+----+----+
 *
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionRequestMessage extends DirectConnectionMessage {

    /** Create a {@link MessageType#DIRECT_CONNECTION_REQUEST} message.
     *
     * @param messageId
     * 		The message ID of the message.
     * @param agentID
     * 		The agentID. Must NOT be null.
     * @param remoteAgentID
     * 		The AgentID of the remote agent which we are trying to establish a direct connection with. Must NOT be null.
     */
    public DirectConnectionRequestMessage(long messageId, AgentID agentID, AgentID remoteAgentID) {
        super(MessageType.DIRECT_CONNECTION_REQUEST, messageId);

        if (agentID == null)
            throw new IllegalArgumentException("Non-null value required for the agentID argument");

        if (remoteAgentID == null)
            throw new IllegalArgumentException("Non-null value required for the remoteAgentID argument");

        this.agentID = agentID;
        this.remoteAgentID = remoteAgentID;
        super.setLength(Message.Field.getTotalOffset() + Field.getTotalOffset());

    }

    /**
     * Construct a {@link MessageType#DIRECT_CONNECTION_REQUEST} message from
     *    the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     * @throws MalformedMessageException if the byte buffer does not contain a valid message
     */
    public DirectConnectionRequestMessage(byte[] byteArray, int offset) throws MalformedMessageException {
        super(byteArray, offset, Field.getTotalOffset());

        if (this.getType() != MessageType.DIRECT_CONNECTION_REQUEST) {
            throw new MalformedMessageException("Malformed " + MessageType.DIRECT_CONNECTION_REQUEST +
                " message:" + "Invalid " + Message.Field.MSG_TYPE + " field " + this.getType());
        }

        try {
            this.agentID = readAgentID(byteArray, offset);
            this.remoteAgentID = readRemoteAgentID(byteArray, offset);
        } catch (MalformedMessageException e) {
            throw new MalformedMessageException("Malformed " + this.getType() + " message:" + e.getMessage());
        }
    }

    /**
     * Fields of the {@link MessageType#DIRECT_CONNECTION_REQUEST} header.
     *
     * These fields are put after the {@link Message} header.
     */
    public enum Field {
        AGENT_ID(8, Long.class), REMOTE_AGENT_ID(8, Long.class);

        private int length;
        private Class<?> type;

        private Field(int length, Class<?> type) {
            this.length = length;
            this.type = type;
        }

        public long getLength() {
            return this.length;
        }

        public int getOffset() {
            int offset = 0;
            // No way to avoid this iteration over ALL the field
            // There is no such method than Field.getOrdinal(x)
            for (Field field : values()) {
                if (field.ordinal() < this.ordinal()) {
                    offset += field.getLength();
                }
            }
            return offset;
        }

        public String getType() {
            return this.type.toString();
        }

        static public int getTotalOffset() {
            // OPTIM: Can be optimized with caching if needed
            int totalOffset = 0;
            for (Field field : values()) {
                totalOffset += field.getLength();
            }
            return totalOffset;
        }

        @Override
        public String toString() {
            switch (this) {
                case AGENT_ID:
                    return "AGENT_ID";
                case REMOTE_AGENT_ID:
                    return "R_AGENT_ID";
                default:
                    return super.toString();
            }
        }
    }

    /** The {@link AgentID} of the agent sending the request message*/
    final private AgentID agentID;

    /** The {@link AgentID} of the target remote agent for the direct connection */
    final private AgentID remoteAgentID;

    public AgentID getAgentID() {
        return this.agentID;
    }

    public AgentID getRemoteAgentID() {
        return this.remoteAgentID;
    }

    @Override
    public byte[] toByteArray() {
        int length = super.getLength();
        byte[] buff = new byte[length];

        super.writeHeader(buff, 0);

        long id = this.agentID.getId();
        long remoteId = this.remoteAgentID.getId();

        TypeHelper.longToByteArray(id, buff, Message.Field.getTotalOffset() + Field.AGENT_ID.getOffset());
        TypeHelper.longToByteArray(remoteId, buff, Message.Field.getTotalOffset() +
            Field.REMOTE_AGENT_ID.getOffset());
        return buff;
    }

    /**
     * Reads the AgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the AgentID of the formatted message
     * @throws MalformedMessageException when an illegal value is encountered in the agentID field of the message
     */
    static public AgentID readAgentID(byte[] byteArray, int offset) throws MalformedMessageException {
        long id = TypeHelper.byteArrayToLong(byteArray, offset + Message.Field.getTotalOffset() +
            Field.AGENT_ID.getOffset());
        if (id >= 0)
            return new AgentID(id);
        else
            throw new MalformedMessageException("Invalid " + Field.AGENT_ID + " value:" + id);
    }

    /**
     * Reads the remote AgentID from a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the Router ID of the formatted message
     * @throws IllegalArgumentException when an illegal value is encountered in the agentID field of the message
     */
    static public AgentID readRemoteAgentID(byte[] byteArray, int offset) throws MalformedMessageException {
        long id = TypeHelper.byteArrayToLong(byteArray, offset + Message.Field.getTotalOffset() +
            Field.REMOTE_AGENT_ID.getOffset());
        if (id >= 0)
            return new AgentID(id);
        else
            throw new MalformedMessageException("Invalid " + Field.REMOTE_AGENT_ID + " value:" + id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((agentID == null) ? 0 : agentID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DirectConnectionRequestMessage other = (DirectConnectionRequestMessage) obj;
        if (agentID == null) {
            if (other.agentID != null)
                return false;
        } else if (!agentID.equals(other.agentID))
            return false;
        if (remoteAgentID == null) {
            if (other.remoteAgentID != null)
                return false;
        } else if (!remoteAgentID.equals(other.remoteAgentID))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + Field.AGENT_ID + ":" + this.agentID + ";" + Field.REMOTE_AGENT_ID + ":" +
            this.remoteAgentID + ";";
    }

}
