package org.objectweb.proactive.p2p.service.messages;

import java.io.Serializable;
import java.util.Vector;

import org.objectweb.proactive.p2p.service.P2PService;
import org.objectweb.proactive.p2p.service.util.UniversalUniqueID;


public class AcquaintanceReply extends Message implements Serializable{
    protected String senderURL;
    protected Vector<String> urls;


    public AcquaintanceReply(int i, UniversalUniqueID uniqueID,
        P2PService sender, String activeObjectNodeUrl) {
        super(i, uniqueID, sender);
        this.senderURL = activeObjectNodeUrl;
    }

    public AcquaintanceReply(int i, UniversalUniqueID uniqueID,
        P2PService sender, String activeObjectNodeUrl, Vector<String> result) {
        super(i, uniqueID, sender);
        this.urls=result;
        this.senderURL = activeObjectNodeUrl;
    }

    public void execute(P2PService target) {
        if (urls == null) {
            target.acquaintanceManager.addFromReply(senderURL, this.sender);
        } else {
            target.acquaintanceManager.removeFromReply(senderURL, this.urls);
        }
    }

    /**
     * Do nothing
     */
    public void transmit(P2PService acq) {
    }
}
