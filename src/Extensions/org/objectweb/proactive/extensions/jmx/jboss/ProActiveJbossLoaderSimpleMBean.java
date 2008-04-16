package org.objectweb.proactive.extensions.jmx.jboss;

import org.jboss.system.ServiceMBean;

/**
 * Interface for the ProActive JBoss Service implementation
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public interface ProActiveJbossLoaderSimpleMBean extends ServiceMBean {
	/**
	 * Gets the name of the JVM into which the ProActive runtime resides
	 * @return string containing a JVM name
	 */
	public String getvmName();
	
	/**
	 * Gets the name of the JVM into which the ProActive runtime resides
	 * @param the name which will be assigned to this JVM 
	 */
	public void setvmName(String vmName);
	
	/**
	 * Gets the name of the file used for log4j configuration for the ProActive classes
	 * @return the log4j config file
	 */
	public String getLog4jConfigFile();
	
	/**
	 * Sets the name of the log4j config file
	 * @param configFile the name of the log4j config file
	 */
	public void setLog4jConfigFile(String configFile);
	
	/**
	 * @return The stringified URL of the freshly-started PART
	 */
	public String getProActiveRuntimeURL();
	
	
	/**
	 * @return The stringified URL of the local node created in the PART
	 */
	public String getProActiveNodeUrl();
	
	public String getNodeName();
	
	public void setNodeName(String nodeName);
	
}
