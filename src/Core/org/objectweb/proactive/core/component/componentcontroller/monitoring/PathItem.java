package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

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
public class PathItem implements Serializable {

	String componentName;
	String interfaceName;
	String methodName;
	
	public PathItem(String c, String i, String m) {
		this.componentName = c;
		this.interfaceName = i;
		this.methodName = m;
	}

	public String toString() {
		return componentName + "." + interfaceName + "." + methodName;
	}
	
}
