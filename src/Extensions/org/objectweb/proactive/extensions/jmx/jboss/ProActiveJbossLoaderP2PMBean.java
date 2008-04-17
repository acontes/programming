/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extensions.jmx.jboss;

import org.jboss.system.ServiceMBean;

/**
 * Interface for the ProActive JBoss Service implementation
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public interface ProActiveJbossLoaderP2PMBean extends ServiceMBean {
	
	/**
	 * Gets the name of the JVM into which the ProActive runtime resides
	 * It is not supposed to contain the prefix PA_JVM, as it will be 
	 * automatically added when the PART is created  
	 * @return string containing a JVM name
	 */
	public String getvmName();
	
	/**
	 * Gets the name of the JVM into which the ProActive runtime resides
	 * It is not supposed to contain the prefix PA_JVM, as it will be 
	 * automatically added when the PART is created
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
	
	public String getPeersListFile();
	
	public void setPeersListFile(String peersListFile);
	
	public String getAcquisitionMethod();
	
	public void setAcquisitionMethod(String acquisitionMethod);
	
	public int getPortNumber();
	
	public void setPortNumber(int portNumber);
	
}
