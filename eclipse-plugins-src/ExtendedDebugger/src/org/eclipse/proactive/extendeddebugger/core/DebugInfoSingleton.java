package org.eclipse.proactive.extendeddebugger.core;

import java.net.URI;

public class DebugInfoSingleton {

	private static DebugInfoSingleton singleton = null;
	private static Object lock = new Object();
	private URI runtimeURL = null;

	public static DebugInfoSingleton getInstance(){
		if (singleton == null) {
			synchronized(lock) {
				if (singleton == null) {
					singleton = new DebugInfoSingleton();
				}
			}
		}
		return singleton;
	}
	
	public URI getRuntimURL(){
		return runtimeURL;
	}

	public void setRuntimURL(URI runtimeURL){
		this.runtimeURL = runtimeURL;
	}
}
