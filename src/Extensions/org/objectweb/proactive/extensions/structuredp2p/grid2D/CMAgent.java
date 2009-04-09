package org.objectweb.proactive.extensions.structuredp2p.grid2D;

public class CMAgent implements java.io.Serializable {

	// empty constructor is required by Proactive
	public CMAgent() {
	}

	public State getCurrentState() {
		return new State();
    }
}
