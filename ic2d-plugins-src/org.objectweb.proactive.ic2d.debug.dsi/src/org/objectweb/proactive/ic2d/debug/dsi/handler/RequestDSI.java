package org.objectweb.proactive.ic2d.debug.dsi.handler;

import org.objectweb.proactive.core.UniqueID;


public class RequestDSI {

    private UniqueID sender;
    private UniqueID destinator;
    
    public RequestDSI(UniqueID sender, UniqueID destinator) {
        this.sender = sender;
        this.destinator = destinator;
    }

    public UniqueID getSender() {
        return sender;
    }

    public UniqueID getDestinator() {
        return destinator;
    }

    
}
