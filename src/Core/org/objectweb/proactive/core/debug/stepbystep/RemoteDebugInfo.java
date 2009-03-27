package org.objectweb.proactive.core.debug.stepbystep;

import java.io.Serializable;

import org.objectweb.proactive.core.UniqueID;

public class RemoteDebugInfo implements Serializable{

	private static final long serialVersionUID = -2088685052162076195L;
	/** Unique id of the active object */
    private UniqueID id;
    private String remoteRuntimeURL;
	
	public UniqueID getId() {
		return id;
	}

	public String toString(){
		return "ActiveObjectId: " + id + "\nremoteRuntimeURL: " + remoteRuntimeURL + "\n";
	}
	
	public String getRemoteRuntimeURL() {
		return remoteRuntimeURL;
	}

	public RemoteDebugInfo(UniqueID id, String remoteRuntimeURL) {
		this.id = id;
		this.remoteRuntimeURL = remoteRuntimeURL;
	}
}
