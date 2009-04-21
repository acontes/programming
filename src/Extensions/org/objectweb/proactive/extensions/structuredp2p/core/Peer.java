package org.objectweb.proactive.extensions.structuredp2p.core;

import java.io.Serializable;

import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.PingResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class Peer implements Serializable {
    private StructuredOverlay structuredOverlay;
    private DataStorage dataStorage;

    public Peer() {

    }

    /**
     * 
     * @param type
     */
    public Peer(PeerType type) {
        // TODO
    }

    /**
     * 
     * @param peer
     * @param msg
     */
    public void sendMessageTo(Peer peer, Message msg) {
        // TODO
    }

    /**
     * 
     * @param peer
     */
    public void join(Peer peer) {
        this.structuredOverlay.join(peer);
    }

    /**
     * 
     */
    public void leave() {
        this.structuredOverlay.leave();
    }

    /**
     * 
     * @param msg
     * @return
     */
    public ResponseMessage receiveMessage(Message msg) {
        // TODO
        return null;
    }

    /**
     * 
     * @param msg
     * @return
     */
    public PingResponseMessage handlePingMessage(PingMessage msg) {
        // TODO
        return null;
    }

    /**
     * @param dataStorage
     *            the dataStorage to set
     */
    public void setDataStorage(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * @return the dataStorage
     */
    public DataStorage getDataStorage() {
        return this.dataStorage;
    }
}
