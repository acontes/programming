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
package org.objectweb.proactive.core.process.rsh;

import org.objectweb.proactive.core.process.AbstractExternalProcessDecorator;
import org.objectweb.proactive.core.process.SimpleExternalProcess;
import org.objectweb.proactive.core.process.ExternalProcess;

/**
 * <p>
 * The RSHProcess class is able to start any class, of the ProActive library, 
 * using rsh protocol.
 * </p><p>
 * For instance:
 * </p><pre>
 * ..........
 * RSHProcess rsh = new RSHProcess(new SimpleExternalProcess("ls -lsa"));
 * rsh.setHostname("hostname.domain.fr");
 * rsh.startProcess();
 * .......... or 
 * RSHProcess rsh = new RSHProcess(new JVMProcessImpl(new StandardOutputMessageLogger()));
 * ssh.setHostname("hostname.domain.fr");
 * ssh.startProcess();
 * .....
 * </pre>
 * @author  ProActive Team
 * @version 1.0,  2002/09/20
 * @since   ProActive 0.9.4
 */
 
public class RSHProcess extends AbstractExternalProcessDecorator {
  
  //
  // -- CONSTRUCTORS -----------------------------------------------
  //
  /**
   * Creates a new RSHProcess
   * Used with XML Descriptors
   */
  public RSHProcess() {
    super();
  }
  
  
  /**
   * Creates a new RSHProcess
   * @param targetProcess The target process associated to this process. The target process 
   * represents the process that will be launched after logging remote host with rsh protocol
   */
  public RSHProcess(ExternalProcess targetProcess) {
    super(targetProcess);
  }
    

  //
  // -- PUBLIC METHODS -----------------------------------------------
  //
    
  public static void main(String[] args) {
    try {
      RSHProcess rsh = new RSHProcess(new SimpleExternalProcess("ls -lsa"));
      rsh.setHostname("solida");
      rsh.startProcess();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  //
  // -- PROTECTED METHODS -----------------------------------------------
  //

  protected String internalBuildCommand() {
    return buildRSHCommand()+buildEnvironmentCommand();
  }
  
  
  protected String buildRSHCommand() {
    if (IS_WINDOWS_SYSTEM) {
      return buildWindowsRSHCommand();
    } else {
      return buildUnixRSHCommand();
    }
  }
  
  
  protected String buildUnixRSHCommand() {
    StringBuffer command = new StringBuffer();
    command.append("rsh");
    // append username
    if (username != null) {
      command.append(" -l ");
      command.append(username);
    }
    // append host
    command.append(" ");
    command.append(hostname);
    command.append(" ");   
    return command.toString();
  }
  
  
  protected String buildWindowsRSHCommand() {
    StringBuffer command = new StringBuffer();
    command.append("rsh");
    command.append(" ");
    command.append(hostname);
    // append username
    if (username != null) {
      command.append(" -l ");
      command.append(username);
    }
    // append host
    command.append(" ");
    return command.toString();
  }
  
  
  //
  // -- PRIVATE METHODS -----------------------------------------------
  //

}
