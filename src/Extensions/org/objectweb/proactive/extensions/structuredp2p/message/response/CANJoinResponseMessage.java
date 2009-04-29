package org.objectweb.proactive.extensions.structuredp2p.message.response;

/**
 * A chord response message gives a CAN peer for routing.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANJoinResponseMessage extends JoinResponseMessage {

	/**
	 * Constructor.
	 * 
	 * @param isSuccess
	 *            indicates if the join has succeeded.
	 */
	public CANJoinResponseMessage(boolean isSuccess) {
		super(isSuccess);
	}
}
