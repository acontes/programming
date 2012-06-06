/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
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
	final static protected Logger futureLogger = ProActiveLogger.getLogger(Loggers.FUTURE);
	
	public ReplyReceiverImpl() {
    }

    public int receiveReply(Reply r, Body receiverBody, FuturePool futurePool) throws java.io.IOException {
    	boolean awaited = false;
    	if(r.getResult() != null) {
    		Object res = r.getResult().getResult();
    		if(res != null) {
    			awaited = PAFuture.isAwaited(res);
    		}
    	}
    	logger.debug("[ReplyReceiv] receiveReply for Future ["+ r.getSequenceNumber()+"] from body ["+r.getSourceBodyID()+"] Receiver ["+ receiverBody +"] Method ["+ r.getMethodName() + "], isAwaited? "+ awaited + " tags "+ r.getTags());
    	futureLogger.debug("[ReplyReceiv] receiveReply for Future ["+ r.getSequenceNumber()+"] from body ["+r.getSourceBodyID()+"] Receiver ["+ receiverBody +"] Method ["+ r.getMethodName() + "], isAwaited? "+ awaited + " tags "+ r.getTags());
    	
    	int a = futurePool.receiveFutureValue(r.getSequenceNumber(), r.getSourceBodyID(), r.getResult(), r);
    	return a;
    }
            
}
