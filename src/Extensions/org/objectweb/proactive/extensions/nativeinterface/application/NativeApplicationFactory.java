package org.objectweb.proactive.extensions.nativeinterface.application;

import java.io.Serializable;


public interface NativeApplicationFactory extends Serializable {
	NativeMessageAdapter createMsgAdapter();
	NativeMessageHandler createMsgHandler();
}    
