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
package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;

public class IC2DConnection  extends PolylineConnection{

	private RotatableDecoration targetDecoration;
	
	//
	// -- PUBLIC METHODS ----------------------------------------------
	//
	
	public IC2DConnection(){
		super();
		
		targetDecoration = getTargetDecoration();
		setTargetDecoration(targetDecoration);
		setLineWidth(1);
		setForegroundColor(ColorConstants.black);
		setLineStyle(Graphics.LINE_SOLID);
	}
	
	//
	// -- PROTECTED METHODS -------------------------------------------
	//
	
	protected RotatableDecoration getTargetDecoration()
	{
		if (targetDecoration == null)
		{
			PointList points = new PointList();
			points.addPoint(-2, 2);
			points.addPoint(0, 0);
			points.addPoint(-2, -2);
			targetDecoration = new PolygonDecoration();
			((PolygonDecoration) targetDecoration).setTemplate(points);
		}
		return targetDecoration;
	}
	 
}
