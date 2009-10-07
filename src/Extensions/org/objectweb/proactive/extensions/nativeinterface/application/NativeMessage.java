package org.objectweb.proactive.extensions.nativeinterface.application;

public interface NativeMessage {
    public String toString(String prefix);

	public byte [] getSerializedMessage();

	public int getDestRank();
	
	public int getDestJobId();
	
	public int getSrcRank();
		
	public int getSrcJobId();
}
