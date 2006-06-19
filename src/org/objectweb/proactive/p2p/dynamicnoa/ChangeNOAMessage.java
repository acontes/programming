package org.objectweb.proactive.p2p.dynamicnoa;

import org.objectweb.proactive.p2p.service.P2PService;
import org.objectweb.proactive.p2p.service.messages.Message;
import org.objectweb.proactive.p2p.service.util.UniversalUniqueID;

public class ChangeNOAMessage extends Message {

	protected int noa; 

	public ChangeNOAMessage(int ttl, int noa) {
		super(ttl);
		this.noa=noa;
		// TODO Auto-generated constructor stub
	}



	@Override
	public void execute(P2PService target) {
	    target.getAcquaintanceManager().setNOA(this.noa);
	    
	}

	/**
	 * Nothing to do, the message should not be transmited
	 */	
	public void transmit(P2PService acq) {
	

	}

}
