package org.objectweb.proactive.extensions.masterworker.core;

import net.jini.core.entry.Entry;


public class ShareMemoryEntry implements Entry {
    static final long serialVersionUID = -2099083666668626172L;

    public Object data;
    public String dataName;

    public ShareMemoryEntry() {
        data = null;
        dataName = null;
    }

    /*
     public ShareMemoryEntry(String dataName, Object data) {
     this.dataName = dataName;
     this.data = data;
     }
     */
}