/*
 * Created on Jul 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.core.body.xmlhttp;

/**
 * @author vlegrand
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HTTPBasicMessage implements XMLHTTPMessage {

	private Object returnedObject;
	
	public HTTPBasicMessage (Object returnedObject) {
		this.returnedObject = returnedObject;
	}
	
	/* (non-Javadoc)
	 * @see org.objectweb.proactive.core.body.xmlhttp.XMLHTTPMessage#processMessage()
	 */
	public Object processMessage() {
		return this.returnedObject;
	}

}
