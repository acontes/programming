package org.objectweb.proactive.ic2d.debug.dsi.handler;

import org.objectweb.proactive.core.UniqueID;


public class RequestDSI {

    private UniqueID sender;
    private UniqueID destinator;
    private String  methodName;
    
    public RequestDSI(UniqueID sender, UniqueID destinator,String methodName) {
        this.sender = sender;
        this.destinator = destinator;
        this.methodName = methodName;
    }

    public UniqueID getSender() {
        return sender;
    }

    public UniqueID getDestinator() {
        return destinator;
    }

    public String getMethodName() {
        return methodName;
    }
    
}
