package org.objectweb.proactive.extensions.structuredp2p.core;

import org.objectweb.proactive.extensions.structuredp2p.message.Message;


/**
 * 
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public interface StructuredOverlay {

    public void join(Peer peer);

    public void leave();

    public void update();

    public void checkNeighbors();

    public void sendMessageTo(Peer peer, Message msg);
}
