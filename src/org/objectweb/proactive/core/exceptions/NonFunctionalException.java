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
package org.objectweb.proactive.core.exceptions;

import org.objectweb.proactive.core.ProActiveException;

/**
 * An interface for non functional exceptions 
 * Should implement Serializable but Exception class do it
 * @author  ProActive Team
 * @version 1.0,  2003/04/01
 * @since   ProActive 1.0.2
 *
 */
public class NonFunctionalException extends ProActiveException {

	/**
	* separator for exception description
	 */
	static protected String separator = "::";
		
	/**
	 * self description of the non functional exception
	 */
	protected String description;	
	
	
	/**
	 * Constructs a <code>NonFunctionalException</code> with no specified
	 * detail message.
	 */
	public NonFunctionalException() {
		super();
		description = "NFE" + separator;
    }

	  
	/**
	  * Constructs a <code>NonFunctionalException</code> with the specified
	  * detail message and nested exception.
	  * @param s the detail message
	  */
	public NonFunctionalException(String s) {
		super(s);
		description = "NFE" + separator;
	  }

	  
	/**
	  * Constructs a <code>NonFunctionalException</code> with the specified
	  * detail message and nested exception.
	  * @param s the detail message
	  * @param ex the nested exception
	  */
	public NonFunctionalException(String s, Throwable ex) {
		super(s, ex);
		description = "NFE" + separator;
	  }


	  /**
	  * Constructs a <code>NonFunctionalException</code> with the specified
	  * detail message and nested exception.
	  * @param ex the nested exception
	  */
	  public NonFunctionalException(Throwable ex) {
		super(ex);
		description = "NFE" + separator;
	  }
	  
	  
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

}

   