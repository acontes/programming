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
package benchmark.rmi.functionscall.intreturn;

import java.net.InetAddress;

import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeImpl;

import benchmark.functionscall.FunctionCall;
import benchmark.rmi.functionscall.RMIFunctionCall;

/**
 * @author Alexandre di Costanzo
 *
 */
public class BenchRMIFuncCall01 extends RMIFunctionCall {
    public BenchRMIFuncCall01() {
    }

    public BenchRMIFuncCall01(NodeImpl node) {
        super(node, "RMI Functions Call --> int f()",
            "Mesure the time of a call Function who return int with no argument.");
    }
    
	/**
	   * @param node
	   * @param name
	   * @param description
	   * @param rmiObjectName
	   */
	  public BenchRMIFuncCall01(Node node, String name, String description,
		  String rmiObjectName) {
		  super(node, name, description, rmiObjectName);
	  }
	  
    public long action() throws Exception {
        
        
        BenchRMIFuncCall01 activeObject = (BenchRMIFuncCall01) getRmiObject();
        this.timer.start(); for(int i = 0 ; i < FunctionCall.MAX_CALL ; i++) {
        activeObject.f();
        } this.timer.stop();
        return this.timer.getCumulatedTime();
    }

    public int f() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(InetAddress.getLocalHost().getHostName());
        }
        return 1;
    }
}
