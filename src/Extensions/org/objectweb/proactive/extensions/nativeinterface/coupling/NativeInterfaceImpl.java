package org.objectweb.proactive.extensions.nativeinterface.coupling;



public class NativeInterfaceImpl implements NativeInterface {

	public int init() {
		return init_native();
	}
	
	public int terminate() {
		return terminate_native();
	}
	
	public void sendMessage(byte [] data) {
		send_message_native(data);
	}
	
	public byte [] recvMessage() {
		return recv_message_native();
	}
	
	public void debug(byte [] data) {
		debug_native(data);
	}

	public native int init_native();
	
	public native int terminate_native();
	
	public native void send_message_native(byte [] data);
	
	public native byte [] recv_message_native();
	
	public native void debug_native(byte [] data);

}
