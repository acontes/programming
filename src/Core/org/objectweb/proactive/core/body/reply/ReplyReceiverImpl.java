/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.body.reply;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.body.future.FuturePool;
import org.objectweb.proactive.core.body.future.MethodCallResult;
import org.objectweb.proactive.core.body.tags.MessageTags;
import org.objectweb.proactive.core.body.tags.Tag;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class ReplyReceiverImpl implements ReplyReceiver, java.io.Serializable {
    
	final static protected Logger logger = ProActiveLogger.getLogger(Loggers.BODY);
	
	public ReplyReceiverImpl() {
    }

    public int receiveReply(Reply r, Body receiverBody, FuturePool futurePool) throws java.io.IOException {
    	int n = -1;
    	boolean awaited = false;
    	MethodCallResult mcr = r.getResult();
    	String className = "";
    	String name = PAActiveObject.getBodyOnThis().getName();
    	if(r.getResult() != null) {
    		Object res = r.getResult().getResult();
    		if(res != null) {
    			awaited = PAFuture.isAwaited(res);
    			if(!awaited) {
    				n = 0;
    			}
    			else {
    				n = 1;
    			}
    			className = r.getResult().getResult().getClass().getName();
    		}
    	}
    	    	
    	// Here, I know if the result is really available (awaited == false).
    	// If it is, then I should generate a notification realReplyReceived, and read it
    	// No, it is not here, ... it's in FutureProxy.receiveReply ... (TO DELETE)
    	if(n == 0) {
    		Body body = PAActiveObject.getBodyOnThis();
    		BodyWrapperMBean mbean = null;
//    		if(body != null) {
//    			mbean = body.getMBean();
//    			if(mbean != null) {
//    				String tagNotification = createTagNotification(r.getTags());
//    				RequestNotificationData requestNotificationData = new RequestNotificationData(
//    						body.getID(), body.getNodeURL(), r.getSourceBodyID(), body.getNodeURL(),
//    						r.getMethodName(), body.getRequestQueue().size() + 1, r.getSequenceNumber(),
//    						tagNotification);
//    				mbean.sendNotification(NotificationType.realReplyReceived, requestNotificationData);
//    			}
//    		}
    	}
    	logger.debug("[ReplyReceiv] receiveReply for Future ["+ r.getSequenceNumber()+"] from body ["+r.getSourceBodyID()+"] Receiver ["+ receiverBody +"] Method ["+ r.getMethodName() + "]");
    	int a = futurePool.receiveFutureValue(r.getSequenceNumber(), r.getSourceBodyID(), r.getResult(), r);
    	return a;
    }
    
    private String createTagNotification(MessageTags tags) {
        String result = "";
        if (tags != null) {
            for (Tag tag : tags.getTags()) {
                result += tag.getNotificationMessage();
            }
        }
        return result;
    }        
}
