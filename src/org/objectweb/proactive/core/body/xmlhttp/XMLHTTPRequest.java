/*
 * Created on Apr 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.core.body.xmlhttp;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.ext.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.ext.webservices.utils.ProActiveXMLUtils;

import java.io.IOException;


/**
 * @author vlegrand
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLHTTPRequest implements XMLHTTPMessage {
    private Request request;
    private UniqueID IdBody;
 

    public XMLHTTPRequest(Request request, UniqueID idBody) {
        this.request = request;
        this.IdBody = idBody;
    }



    public Object  processMessage() {
        try {
        	Body body = ProActiveXMLUtils.getBody(IdBody);
        	if (this.request != null) 
                body.receiveRequest(this.request);
          
        } catch (IOException e) {
         
            e.printStackTrace();
        } catch (RenegotiateSessionException e) {
       
            e.printStackTrace();
        }
        return null;
    }
}
