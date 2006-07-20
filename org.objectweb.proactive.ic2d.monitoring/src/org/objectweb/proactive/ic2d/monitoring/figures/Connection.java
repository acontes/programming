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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Connection  extends PolylineConnection{

	/**
	 * Symbolizes connections in waitings. The source object awaits the target object.
	 */
	private static HashMap<String, AOFigure> sources = new HashMap<String, AOFigure>();

	/**
	 * Symbolizes connections in waitings. The target object awaits the source object.
	 */
	private static HashMap<String, AOFigure> targets = new HashMap<String, AOFigure>();


	/**
	 * Established connections.
	 */
	private static HashMap<String, String> connections = new HashMap<String, String>();

	/**
	 * Decorates connection with an arrow.
	 */
	private RotatableDecoration targetDecoration;

	//
	// -- PUBLIC METHODS ----------------------------------------------
	//

	/**
	 * Creates a new connection.
	 */
	private Connection(){
		super();
		targetDecoration = getTargetDecoration();
		setTargetDecoration(targetDecoration);
		setLineWidth(1);
		setForegroundColor(new Color(Display.getCurrent(), 108, 108, 116));
		setLineStyle(Graphics.LINE_SOLID);
	}


	//
	// -- PUBLICS METHODS -----------------------------------------------
	//


	/**
	 * If the source is the present then a connection is made. Otherwise the request is stored until the arrival of the target.
	 * @param panel The panel containing the two figures.
	 * @param sourceID A String representing the source
	 * @param sourceFigure The source figure.
	 * @param targetID A String representing the target.(In order to find the target)
	 */
	public synchronized static void addSourceConnection(IFigure panel, String sourceID, AOFigure sourceFigure, String targetID){
		// If a connection already exists
		String targetUsed = connections.get(sourceID);
		if(targetUsed != null && targetUsed.compareTo(targetID) == 0)
			return;

		AOFigure targetFigure = targets.get(targetID);
		if(targetFigure!=null){
			targets.remove(targetID);
			connections.put(sourceID, targetID);
			connect(panel, sourceFigure, targetFigure);
		}
		else{
			sources.put(sourceID, sourceFigure);
		}
	}

	/**
	 * If the target is the present then a connection is made. Otherwise the request is stored until the arrival of the source.
	 * @param panel The panel containing the two figures.
	 * @param targetID A String representing the target.
	 * @param targetFigure The target figure.
	 * @param sourceID A String representing the source.(In order to find the source)
	 */
	public synchronized static void addTargetConnection(IFigure panel, String targetID, AOFigure targetFigure, String sourceID){
		// If a connection already exists
		String targetUsed = connections.get(sourceID);
		if( targetUsed != null && targetUsed.compareTo(targetID) == 0)
			return;

		AOFigure sourceFigure = sources.get(sourceID);
		if(sourceFigure!=null){
			sources.remove(sourceID);
			connections.put(sourceID, targetID);
			connect(panel, sourceFigure, targetFigure);
		}
		else{
			targets.put(targetID, targetFigure);
		}
	}

	//
	// -- PROTECTED METHODS -------------------------------------------
	//

	/**
	 * Returns a decoration containing an arrow.
	 */
	protected RotatableDecoration getTargetDecoration(){
		if (targetDecoration == null){
			PointList points = new PointList();
			points.addPoint(-1, 1);
			points.addPoint(0, 0);
			points.addPoint(-1, -1);
			targetDecoration = new PolygonDecoration();
			((PolygonDecoration) targetDecoration).setTemplate(points);
		}
		return targetDecoration;
	}

	//
	// -- PRIVATE METHODS -------------------------------------------
	//

	/**
	 * Connects two figure by a connection.
	 * @param panel The panel containing the two figures.
	 * @param source The source figure.
	 * @param target The target figure.
	 */
	private static void connect(IFigure panel, AOFigure source, AOFigure target){
		Connection connection = new Connection();
		connection.setSourceAnchor(source.getAnchor());
		connection.setTargetAnchor(target.getAnchor());

		BendpointConnectionRouter router = new BendpointConnectionRouter();

		List<RelativeBendpoint> bendPoints = new ArrayList<RelativeBendpoint>();
		Point sourceCenter = source.getLocation().getTranslated(source.getBounds().width/2, source.getBounds().height/2);
		Point targetCenter = target.getLocation().getTranslated(target.getBounds().width/2, target.getBounds().height/2);

		RelativeBendpoint middle = calculPoint(connection, sourceCenter, targetCenter);

		bendPoints.add(middle);
		router.setConstraint(connection,bendPoints);

		connection.setConnectionRouter(router);

		panel.add(connection);
	}

	private static RelativeBendpoint calculPoint(Connection connection, Point source, Point target){
		double distance = source.getDistance(target);
		RelativeBendpoint point = new RelativeBendpoint(connection);
		int value = (int)(0.05*distance);
		value*=value;
		point.setRelativeDimensions(new Dimension(value, 0), new Dimension(value, 0));
		return point;
	}
}
