package org.objectweb.proactive.core.component.componentcontroller.monitoring;

/**
 * This class represents an element in the path of a request.
 * It should, at least, include:
 * 		- Component Name
 * 		- Interface Name
 * 		- Method Name
 * Optionally, it can include statistics for each component 
 * 
 * @author cruz
 *
 */
public class PathItem {

	String componentName;
	String interfaceName;
	String methodName;
	
	public PathItem() {
		
	}
	
	
}
