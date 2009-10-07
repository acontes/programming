package org.objectweb.proactive.extensions.nativeinterface.application;

import org.objectweb.proactive.extensions.nativeinterface.coupling.ProActiveNativeInterface;

public interface NativeMessageHandler {
	
	public boolean handleMessage(ProActiveNativeInterface callback, NativeMessage message);

}
