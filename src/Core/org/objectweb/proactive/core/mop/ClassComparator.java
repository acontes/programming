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
package org.objectweb.proactive.core.mop;

import java.io.ObjectStreamClass;

/**
 * Verifies if two classes are "the same" 
 * The verification is done based on the serialVersionUID field
 * We assume that the classes are serializable!
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class ClassComparator {

	public static boolean compare( Class<?> c1 , Class<?> c2 ) {
		ObjectStreamClass os1 = ObjectStreamClass.lookup(c1);
		if(os1 == null)
			throw new IllegalArgumentException("Class " + c1.getName() + " is not serializable!");
		
		ObjectStreamClass os2 = ObjectStreamClass.lookup(c2);
		if(os2 == null)
			throw new IllegalArgumentException("Class " + c1.getName() + " is not serializable!");
		
		return os1.getSerialVersionUID() == os2.getSerialVersionUID();
		
	}
}
