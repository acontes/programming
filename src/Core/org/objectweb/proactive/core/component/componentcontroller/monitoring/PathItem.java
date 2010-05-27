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

	String callerComponentName;
	String calledComponentName;
	String interfaceName;
	String methodName;
	
	public PathItem(String c1, String c2, String i, String m) {
		this.callerComponentName = c1;
		this.calledComponentName = c2;
		this.interfaceName = i;
		this.methodName = m;
	}

	public String toString() {
		return callerComponentName +" --> "+ calledComponentName + "." + interfaceName + "." + methodName;
	}
	
}
