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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This class is used to display a rounded line. This class works with RoundedLineConnection.
 */
public class RoundedLine extends Polyline {

	//
	// -- PROTECTED METHODS -------------------------------------------
	//

	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	@Override
	protected void outlineShape(Graphics g) {
		PointList pointList = getPoints();
		Point source = pointList.getFirstPoint();
		Point target = pointList.getLastPoint();

		//setLineWidth(2);
		setForegroundColor(new Color(Display.getCurrent(), 108, 108, 116));
		setLineStyle(Graphics.LINE_SOLID);

		drawArc(g, source, target);
	}

	//
	// -- PRIVATED METHODS -------------------------------------------
	//
	
	/**
	 * Draw an arc between the source and the target
	 * @param g The graphics
	 * @param source The source
	 * @param target The target
	 */
	private void drawArc(Graphics g, Point source, Point target){
		
		// Sets the anti-aliasing
		g.setAntialias(SWT.ON);
		
		int xSource = source.x;
		int ySource = source.y;

		int xTarget = target.x;
		int yTarget = target.y;

		// Shape changing...
		int distanceY = Math.abs(ySource - yTarget);
		int distanceX = Math.abs(xSource - xTarget);
		int shapeX =  distanceX/ 3;
		int shapeY =  distanceY/ 3;

		if ((xSource == xTarget) && (ySource == yTarget)) {//TODO not yet tested
			g.drawOval(xSource - shapeY - 25, ySource + 7, 30, 15);
		}
		else {
			if(xSource == xTarget){
				if(ySource > yTarget)
					g.drawArc(xTarget - shapeY, yTarget , shapeY*2 , Math.abs(ySource - yTarget), 90, -180);
				else
					g.drawArc(xSource - shapeY, ySource, shapeY * 2, Math.abs(ySource - yTarget), 90, 180);
			}
			else if(ySource == yTarget){
				if(xSource > xTarget)
					g.drawArc(xTarget, yTarget - (shapeX/4), Math.abs(xSource - xTarget) , shapeX/2, 0, 180);
				else
					g.drawArc(xSource, ySource - (shapeX/4), Math.abs(xSource - xTarget) ,shapeX/2,  0, -180);
			}
			else{
				g.drawLine(source, target);
			}
		}
	}
}