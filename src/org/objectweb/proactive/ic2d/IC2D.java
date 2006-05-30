/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
package org.objectweb.proactive.ic2d;

import org.objectweb.proactive.ic2d.gui.IC2DFrame;

/**
 * <p>
 * This class is the main entry to the application IC2D allowing to start it with
 * a new JVM.
 * </p><p>
 * This class has a main method and can be used directly from the java command.<br>
 * &nbsp;&nbsp;&nbsp;java org.objectweb.proactive.ic2d.IC2D
 * </p>
 *
 * @author  ProActive Team
 * @version 1.0,  2002/03/21
 * @since   ProActive 0.9
 *
 */
public class IC2D {

	/**
	 * TODO comment
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO finish

		IC2DFrame frame = new IC2DFrame();
		
		frame.setVisible(true);
	}

}
