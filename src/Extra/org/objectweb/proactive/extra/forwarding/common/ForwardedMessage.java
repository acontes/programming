package org.objectweb.proactive.extra.forwarding.common;

import java.io.Serializable;

/**
 * The forwarded Message is the data which is sent from and to the tunnels.
 * It can encapsulate control messages (connect, abort...) or data.
 */
@SuppressWarnings("serial")
public class ForwardedMessage implements Serializable{
	public static final int DEFAULT_TTL = 255;
	public static int currentPort = 1000;

	protected int ttl;
	protected ForwardedMessageType type;
	protected Object senderID;
	protected Object targetID;
	protected int senderPort;
	protected int targetPort;
	protected Object data;

	public enum ForwardedMessageType { 
		REGISTRATION, // Registration to the registry, providing the localID of the sender
		CONNECTION_REQUEST, // Connection request from a client to a server
		CONNECTION_ACCEPTED, // response to a connection request
		CONNECTION_ABORTED, // response to a connection request
		DATA // data message
	}

	// You can create here a custom message
	public ForwardedMessage(ForwardedMessageType type, Object senderID, int senderPort, Object targetID, int targetPort, Object data, int ttl) {
		this.ttl = ttl;
		this.type = type;
		this.senderID = senderID;
		this.targetID = targetID;
		this.targetPort = targetPort;
		this.senderPort = senderPort;
		this.data = data;
	}

	@Override
	public String toString() {
		switch(type) {
		case REGISTRATION :
			return ("REGISTRATION [from="+senderID+"]");
		case CONNECTION_REQUEST :
			return ("CONNECTION_REQUEST [from="+senderID+":"+senderPort+"; to="+targetID+":"+targetPort+"]");
		case CONNECTION_ACCEPTED :
			return ("CONNECTION_ACCEPTED [from="+senderID+":"+senderPort+"; to="+targetID+":"+targetPort+"]");
		case CONNECTION_ABORTED :
			return ("CONNECTION_ABORTED [from="+senderID+":"+senderPort+"; to="+targetID+":"+targetPort+"; cause="+data+"]");
		case DATA :
//			return ("DATA [from="+senderID+":"+senderPort+"; to="+targetID+":"+targetPort+"; size="+((byte[]) data).length+"]");
			return ("DATA [from="+senderID+":"+senderPort+"; to="+targetID+":"+targetPort+"; size="+((byte[]) data).length+"; data="+(new String((byte[]) data))+"]");
		default: //should not occur
			return ("UNKNOWN MESSAGE");
		}
	}

	public static ForwardedMessage registrationMessage(Object senderID) {
		return new ForwardedMessage(ForwardedMessageType.REGISTRATION, senderID, 0, null, 0, null, DEFAULT_TTL);
	}

	public static ForwardedMessage connectionMessage(Object senderID, Object targetID, int targetPort) {
		return new ForwardedMessage(ForwardedMessageType.CONNECTION_REQUEST, senderID, getSourcePort(), targetID, targetPort, null, DEFAULT_TTL);
	}

	public static ForwardedMessage acceptMessage(Object senderID, int senderPort, Object targetID, int targetPort) {
		return new ForwardedMessage(ForwardedMessageType.CONNECTION_ACCEPTED, senderID, senderPort, targetID, targetPort, null, DEFAULT_TTL);
	}

	public static ForwardedMessage abortMessage(Object senderID, int senderPort, Object targetID, int targetPort, String cause) {
		return new ForwardedMessage(ForwardedMessageType.CONNECTION_ABORTED, senderID, senderPort, targetID, targetPort, cause, DEFAULT_TTL);
	}

	public static ForwardedMessage dataMessage(Object senderID, int senderPort, Object targetID, int targetPort, byte[] data) {
		return new ForwardedMessage(ForwardedMessageType.DATA, senderID, senderPort, targetID, targetPort, data, DEFAULT_TTL);
	}

	/**
	 * This private method generates a port number. 
	 * We don't care if this port is already in use or not, it is only for a routing purpose.
	 * It is here to differentiate two connection between two same hosts.
	 * @return
	 */
	private static int getSourcePort() {
		// TODO Auto-generated method stub
		return currentPort++;
	}

	public int getTtl() {
		return ttl;
	}

	public ForwardedMessageType getType() {
		return type;
	}

	public Object getSenderID() {
		return senderID;
	}

	public int getSenderPort() {
		return senderPort;
	}

	public Object getTargetID() {
		return targetID;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public Object getData() {
		return data;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

}
