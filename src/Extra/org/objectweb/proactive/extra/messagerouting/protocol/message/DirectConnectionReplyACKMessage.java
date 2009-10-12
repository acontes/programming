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

import java.net.Inet4Address;
import java.net.InetAddress;

import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.protocol.TypeHelper;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;


/** A {@link MessageType#DIRECT_CONNECTION_ACK} message.
 *
 * Sent by the router to the client when a direct connection
 * is possible between the two endpoints
 *
 * Format of the message:
 *
 * 			   DC_ACK
 *               |
 * 0      4    8 |  12   16   20
 *  +----+----+----+----+----+
 *  + LEN+PROT+TYPE+    ID   +
 *  +----+----+----+----+----+
 *  + IP +PO+
 *  +----+----+
 *
 *  Legend:
 *  <ul>
 *  <li>IP = IP_ADDR field; contains the IP address of the remote endpoint; length is 4 bytes</li>
 *  <li>PO = port; contains the port of the remote endpoint; length is 2 bytes</li>
 *  </ul>
 *
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionReplyACKMessage extends DirectConnectionReplyMessage {

    /** Create a {@link MessageType#DIRECT_CONNECTION_ACK} message.
     *
     * @param messageId
     * 		The message ID of the message.
     * @param inetAddr
     * 		The ip address of the remote endpoint of the direct connection. Must NOT be null.
     *           Up until now, only IPv4 addresses are supported
     * @param port
     * 		The port of the remote endpoint of the direct connection
     */
    public DirectConnectionReplyACKMessage(long messageId, InetAddress inetAddr, int port) {
        super(MessageType.DIRECT_CONNECTION_ACK, messageId);

        if (inetAddr == null)
            throw new IllegalArgumentException("Non-null value required for the inetAddr argument");

        if (!(inetAddr instanceof Inet4Address))
            throw new IllegalArgumentException("Only IPv4 addresses are supported up to this moment");

        if (!(0 <= port && port < 65536))
            throw new IllegalArgumentException("Invalid port value: " + port);

        this.endpointAddress = (Inet4Address) inetAddr;
        this.endpointPort = port;
        super.setLength(Message.Field.getTotalOffset() + Field.getTotalOffset());

    }

    /**
     * Construct a {@link MessageType#DIRECT_CONNECTION_ACK} message from
     *    the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     * @throws MalformedMessageException if the byte buffer does not contain a valid {@link MessageType#DIRECT_CONNECTION_ACK} message
     */
    public DirectConnectionReplyACKMessage(byte[] byteArray, int offset) throws MalformedMessageException {
        super(byteArray, offset, Field.getTotalOffset());

        if (this.getType() != MessageType.DIRECT_CONNECTION_ACK) {
            throw new MalformedMessageException("Malformed " + MessageType.DIRECT_CONNECTION_ACK +
                " message:" + "Invalid " + Message.Field.MSG_TYPE + " field " + this.getType());
        }

        try {
            this.endpointAddress = readIPAddr(byteArray, offset);
            this.endpointPort = readPort(byteArray, offset);
        } catch (MalformedMessageException e) {
            throw new MalformedMessageException("Malformed " + this.getType() + " message:" + e.getMessage());
        }
    }

    /**
     * Fields of the {@link MessageType#DIRECT_CONNECTION_ACK} header.
     *
     * These fields are put after the {@link Message} header.
     *
     */
    public enum Field {

        /** The IP address of the direct connection endpoint
         *
         * The value is a 4-byte raw representation of the IP address.
         * The format is similar to the one used by {@link InetAddress#getAddress()}
         */
        IP_ADDR(4, Inet4Address.class),
        /** The port which identifies the direct connection endpoint
         *
         * The value is a 2-byte unsigned value, limiting the value range
         * to the usual port numbers range: 0-65535
         */
        PORT(2, Short.class);

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
                case IP_ADDR:
                    return "IP_ADDR";
                case PORT:
                    return "PORT";
                default:
                    return super.toString();
            }
        }
    }

    /** The IP address of the endpoint agent with which a Direct Connection should be established*/
    final private Inet4Address endpointAddress;

    /** The port of the endpoint agent with which a Direct Connection should be established*/
    final private int endpointPort;

    public InetAddress getInetAddress() {
        return this.endpointAddress;
    }

    public int getPort() {
        return this.endpointPort;
    }

    @Override
    public byte[] toByteArray() {
        int length = super.getLength();
        byte[] buff = new byte[length];

        super.writeHeader(buff, 0);

        short sPort = TypeHelper.intToShort(this.endpointPort);

        TypeHelper.inetAddrToByteArray(this.endpointAddress, buff, Message.Field.getTotalOffset() +
            Field.IP_ADDR.getOffset());
        TypeHelper.shortToByteArray(sPort, buff, Message.Field.getTotalOffset() + Field.PORT.getOffset());
        return buff;
    }

    /**
     * Reads the IP Address from a formatted message beginning at a certain offset inside a buffer.
     * @param byteArray the buffer in which to read
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the remote endpoint Inet4Address
     * @throws MalformedMessageException when an illegal value is encountered in the {@link Field#IP_ADDR} field of the message
     */
    static public Inet4Address readIPAddr(byte[] byteArray, int offset) throws MalformedMessageException {
        Inet4Address inetAddr = TypeHelper.byteArrayToInetAddr(byteArray, offset +
            Message.Field.getTotalOffset() + Field.IP_ADDR.getOffset());

        // validate
        if (validIPAddr(inetAddr))
            return inetAddr;
        else
            throw new MalformedMessageException("Invalid " + Field.IP_ADDR + " value:" +
                inetAddr.getHostAddress());
    }

    /**
     * Validate an IP address read from a {@link MessageType#DIRECT_CONNECTION_ACK} message
     * Validation phase consists of the following steps
     * * test if the IP address identifies an <b>unique</b> communication endpoint.
     *  For instance, a multicast address does not identify a single communication endpoint.
     * @param inetAddr - the IP address to validate
     * @return true if "valid"; false if something weird is detected
     */
    private static boolean validIPAddr(Inet4Address inetAddr) {
        // no multicast address
        if (inetAddr.isMulticastAddress())
            return false;
        // no anylocal address
        if (inetAddr.isAnyLocalAddress())
            return false;
        // TODO other tests?
        return true;
    }

    /**
     * Reads the endpoint port from a formatted message beginning at a certain offset inside a buffer.
     * @param byteArray the buffer in which to read
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the port, converted to an positive int value.
     * @throws MalformedMessageException if the port value is invalid
     */
    static public int readPort(byte[] byteArray, int offset) throws MalformedMessageException {
        short sPort = TypeHelper.byteArrayToShort(byteArray, offset + Message.Field.getTotalOffset() +
            Field.PORT.getOffset());
        int port = TypeHelper.shortToInt(sPort);

        if (port == 0)
            throw new MalformedMessageException("Invalid value for the " + Field.PORT + " field:" + port);

        return port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.endpointAddress.hashCode() + this.endpointPort;
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
        DirectConnectionReplyACKMessage other = (DirectConnectionReplyACKMessage) obj;
        if (!endpointAddress.equals(other.endpointAddress))
            return false;
        if (endpointPort != other.endpointPort)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + Field.IP_ADDR + ":" + this.endpointAddress.getHostAddress() + ";" +
            Field.PORT + ":" + this.endpointPort + ";";
    }
}
