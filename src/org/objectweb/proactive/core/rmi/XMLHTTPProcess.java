/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.rmi;

import java.io.DataInputStream;
import java.io.IOException;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.xmlhttp.XMLHTTPMessage;
import org.objectweb.proactive.core.runtime.xmlhttp.RuntimeReply;
import org.objectweb.proactive.ext.webservices.utils.ProActiveXMLUtils;


/**
 * @author vlegrand
 */
public class XMLHTTPProcess {
    protected DataInputStream in;
    protected RequestInfo info;

    /**
     *
     * @param in
     * @param info
     */
    public XMLHTTPProcess(DataInputStream in, RequestInfo info) {
        this.info = info;
        this.in = in;
    }

    /**
     *
     */
    public MSG getBytes() {
        Object result = null;
        byte[] replyMessage = null;
        String action = null;
        try {
            byte[] source = new byte[info.contentLength];
            in.readFully(source);
            
            /* Get what is in the  request */
            result = ProActiveXMLUtils.unwrapp(source, info.action);
            Object returnedObject = null;
            if (result instanceof XMLHTTPMessage ) {
                returnedObject = ( (XMLHTTPMessage) result).processMessage();
                if (returnedObject != null) {
                	replyMessage = ProActiveXMLUtils.getMessage(returnedObject);//, ProActiveXMLUtils.MESSAGE);
                	action = ProActiveXMLUtils.MESSAGE;
                }
            } else if (result instanceof RuntimeReply) {
                
                replyMessage = ProActiveXMLUtils.getMessage(result);//, ProActiveXMLUtils.RUNTIME_REPLY);
                action = ProActiveXMLUtils.RUNTIME_REPLY;
            }
            if (replyMessage == null) {
             
                replyMessage = ProActiveXMLUtils.getMessage("");//, ProActiveXMLUtils.OK);
                action = ProActiveXMLUtils.OK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
        
        return new MSG(replyMessage, action);
    }
}
