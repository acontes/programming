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
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;


/**
 * Reassemble messages from data chunks
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class MessageAssembler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_SERVER_DC);

    final private DirectConnectionServer server;

    final private SocketChannel sc;

    /** The current incomplete message
     *
     * null when the length and the protocol id of the current message are
     * still unknown.
     */
    private ByteBuffer currentMessage;

    /** Length and protocol id of the current message
     *
     * null when a message has been assembled and no data is available
     */
    private LengthAndProto lengthAndProto;

    public MessageAssembler(DirectConnectionServer server, SocketChannel sc) {
        this.server = server;
        this.sc = sc;

        this.currentMessage = null;
        this.lengthAndProto = null;
    }

    synchronized public void pushBuffer(ByteBuffer buffer) throws IllegalStateException {

        while (buffer.remaining() != 0) {

            if (this.currentMessage == null) {

                if (this.lengthAndProto == null) {
                    this.lengthAndProto = new LengthAndProto();
                }

                while (buffer.remaining() > 0 && !lengthAndProto.isReady()) {
                    lengthAndProto.push(buffer.get());
                }

                if (lengthAndProto.isReady()) {

                    int proto = lengthAndProto.getProto();
                    int l = this.lengthAndProto.getLength();

                    // Check the protocol is correct. Otherwise something fucked up
                    // and the connection is closed to avoid a disaster
                    if (proto != Message.PROTOV1) {
                        logger.error("Invalid protocol ID received from " + sc.socket() + ": expected=" +
                            Message.PROTOV1 + " received=" + proto);
                        throw new IllegalStateException("Invalid protocol ID");
                    } else if (l < Message.Field.getTotalOffset()) {
                        logger.error("Invalid message length received from " + sc.socket() + ": " + l);
                        throw new IllegalStateException("Invalid message length");
                    }

                    // Allocate a buffer for the reassembled message
                    currentMessage = ByteBuffer.allocate(l);

                    // Buffer position is no more 0, we copy the data that have been read
                    // by the previous loop
                    currentMessage.putInt(l);
                    currentMessage.putInt(proto);
                } else {
                    // Length is still not available, it means that buffer.remaing() has been reached
                    // We can safely exit the loop
                    break;
                }
            }

            // This point can only be reached if length & proto have been read
            // currentMessage is not null

            // Number of bytes missing to complete the currentMessage
            int missingBytes = currentMessage.remaining();
            // Number of bytes available in the buffer
            int availableBytes = buffer.remaining();

            int toCopy = missingBytes > availableBytes ? availableBytes : missingBytes;

            // Don't use put(ByteBuffer) it does NOT use the limit
            currentMessage.put(buffer.array(), buffer.position(), toCopy);
            buffer.position(buffer.position() + toCopy);

            // Checks if current message is complete
            if (currentMessage.remaining() == 0) {
                if (logger.isDebugEnabled()) {
                    String dest = this.sc.socket().getRemoteSocketAddress().toString();
                    logger.debug("Assembled one message for remote endpoint " + dest);
                }

                this.server.dispatchMessage(currentMessage);
                this.currentMessage = null;
                this.lengthAndProto = null;
            }
        }
    }

    private static class LengthAndProto {
        static private int SIZE = Message.Field.LENGTH.getLength() + Message.Field.PROTO_ID.getLength();

        private byte[] buf;
        private int index;

        protected LengthAndProto() {
            buf = new byte[SIZE];
            index = 0;
        }

        protected void push(byte b) {
            buf[index++] = b;
        }

        protected boolean isReady() {
            return index == SIZE;
        }

        protected int getLength() {
            return Message.readLength(buf, 0);
        }

        protected int getProto() {
            return Message.readProtoID(buf, 0);
        }
    }
}
