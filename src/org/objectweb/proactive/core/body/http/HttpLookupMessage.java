/*
 * Created on Jul 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.core.body.http;

import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.ext.webservices.utils.ProActiveXMLUtils;

/**
 * @author vlegrand
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HttpLookupMessage implements HttpMessage {

	private String urn;
	private Object returnedObject;  
	 
	public HttpLookupMessage (String urn) {    
		this.urn = urn;
	}

	public Object processMessage() {
		if (this.urn != null) {
			UniversalBody ub = RemoteBodyAdapter.getBodyFromUrn(urn);
			if (ub != null)
			this.returnedObject = ub;
		else
			this.returnedObject = ProActiveXMLUtils.NO_SUCH_OBJECT;
		this.urn = null;
		
		return this;
		} else {
			return this.returnedObject;
		}
	}

}
