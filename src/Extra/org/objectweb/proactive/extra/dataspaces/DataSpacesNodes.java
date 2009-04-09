/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.core.node.Node;


/**
 * resp:
	- stores NodeConfigurator for a node (map)
	- stores DataSpacesImpl obtained from NodeConfigurator
	  for a node (map)
	- provides methods for look up for them  
	- connects static API class with node specific instances
	- creates NodeConfigurator for a node (on request)
 *
 */
public class DataSpacesNodes {
    
    public NodeConfigurator getNodeConfigurator(Node node) {
        return null;
    }
    
    public DataSpacesImpl getDataSpacesImpl(Node node) {
        return null;
    }
}
