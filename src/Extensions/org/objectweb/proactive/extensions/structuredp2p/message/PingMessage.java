package org.objectweb.proactive.extensions.structuredp2p.message;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.message.response.PingResponseMessage;


/**
 * A PingMessage is a concrete message for ping
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class PingMessage implements Message {

    /**
     * create a PingMessage
     */
    public PingMessage() {

    }

    /**
     * @param a
     *            peer to which the message will be send
     * @return a PingResponseMessage for routing
     * 
     */
    public PingResponseMessage handle(Peer peer) {

        return peer.handlePingMessage(this);

    }

}
