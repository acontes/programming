package org.objectweb.proactive.extensions.nativeinterface.application;




public interface NativeMessageAdapter {

	public NativeMessage deserialize(byte [] serializedMsg);

	public byte[] buildInitMessage(int nativeRank, int nativeJobID, int nbJob);
	
}
