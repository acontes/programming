package org.objectweb.proactive.extra.forwardingv2.protocol;

public class TypeHelper {
	
	public static int byteArrayToInt(byte[] a, int offset) {
		int l = 0;
		
	    l |= a[offset++] & 0x000000FF;
	    l |= a[offset++] & 0x0000FF00;
	    l |= a[offset++] & 0x00FF0000;
	    l |= a[offset++] & 0xFF000000;
	    
	    return l;
	}
	
	public static byte[] intToByteArray(int val) {
		byte[] b = new byte[4];
		
	    b[0] |= val & 0x000000FF;
	    b[1] |= val & 0x0000FF00;
	    b[2] |= val & 0x00FF0000;
	    b[3] |= val & 0xFF000000;
	    
	    return b;
	}

}
