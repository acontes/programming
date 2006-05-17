package org.objectweb.proactive.p2p.service.messages;

import org.objectweb.proactive.p2p.service.P2PService;
import org.objectweb.proactive.p2p.service.util.UniversalUniqueID;

public abstract class RandomWalkMessage extends Message {

	public RandomWalkMessage(){}
	
	public RandomWalkMessage(int ttl, UniversalUniqueID id, P2PService sender) {
		super(ttl, id, sender);
	}

	@Override
	public void transmit(P2PService acq) {
	    	System.out.println("RequestSingleNodeMessage.transmit()");
	        acq.randomPeer().message(this);
	}

}
