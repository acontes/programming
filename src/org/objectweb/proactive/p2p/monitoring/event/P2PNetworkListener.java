package org.objectweb.proactive.p2p.monitoring.event;

import org.objectweb.proactive.p2p.monitoring.Link;
import org.objectweb.proactive.p2p.monitoring.P2PNode;


/**
 * Should be implemented by classes interested in
 * monitoring a P2P Network
 * @author fhuet
 */
public interface P2PNetworkListener {

	public void newPeer(P2PNode node);
	
	public void newLink(Link link);
	
	
}
