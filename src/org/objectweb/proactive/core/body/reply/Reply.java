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
package org.objectweb.proactive.core.body.reply;

import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.message.Message;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.exceptions.RenegotiateSessionException;

public interface Reply extends Message {

  public Object getResult();
  
  /**
   * Sends this reply to the body destination
   * @param destinationBody the body destination of this reply
   * @exception java.io.IOException if the reply fails to be sent
   */
  public void send(UniversalBody destinationBody) throws java.io.IOException;
  
  // SECURITY
  
   public boolean isCiphered();
  
   public long getSessionId() ;
  
   public boolean decrypt(ProActiveSecurityManager psm) throws RenegotiateSessionException;
}