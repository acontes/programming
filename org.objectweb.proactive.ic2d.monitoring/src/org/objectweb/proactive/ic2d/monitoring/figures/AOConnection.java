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
import java.util.Hashtable;
import java.util.List;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

public class AOConnection {
	
	/**
	 * Symbolizes connections in waitings. The source object awaits the target object.
	 */
	private static Hashtable<String, AOFigure> sources = new Hashtable<String, AOFigure>();

	/**
	 * Symbolizes connections in waitings. The target object awaits the source object.
	 */
	private static Hashtable<String, AOFigure> targets = new Hashtable<String, AOFigure>();

	/**
	 * Established connections.
	 */
	private static Hashtable<String, RoundedLineConnection> connections = new Hashtable<String, RoundedLineConnection>();
	
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
		AOFigure targetFigure = targets.get(targetID);
		if(targetFigure!=null){
			targets.remove(targetID);
			connect(panel, sourceFigure, targetFigure, sourceID, targetID);
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
		AOFigure sourceFigure = sources.get(sourceID);
		if(sourceFigure!=null){
			sources.remove(sourceID);
			connect(panel, sourceFigure, targetFigure, sourceID, targetID);
		}
		else{
			targets.put(targetID, targetFigure);
		}
	}

	//
	// -- PRIVATE METHODS -------------------------------------------
	//

	/**
	 * Connects two figure by a connection, only if there isn't already a connection.
	 * @param panel The panel containing the two figures.
	 * @param source The source figure.
	 * @param target The target figure.
	 * @param sourceID The source ID.
	 * @param targetID The target ID.
	 */
	public static void connect(IFigure panel, AOFigure source, AOFigure target, String sourceID, String targetID){
		RoundedLineConnection connection = null;
		//PolylineConnection connection = new PolylineConnection();
		synchronized (connections) {
			connection = connections.get(sourceID+"#"+targetID);
			//String targetUsed = .getTargetName();
			// If a connection already exists
			if(connection != null){
				connection.addOneCommunication();
				return;
			}
			// Store a new connection in the Hashtable [sourceID#targetID, targetID]
			// We used "sourceID#targetID" for the key, because a source can have several targets
			connection = new RoundedLineConnection();
			connections.put(sourceID+"#"+targetID, connection);
		}

		Point sourceCenter = source.getLocation().getTranslated(source.getBounds().width/2, source.getBounds().height/2);
		Point targetCenter = target.getLocation().getTranslated(target.getBounds().width/2, target.getBounds().height/2);

		int position = sourceCenter.getPosition(targetCenter);

		Anchor sourceAnchor = (Anchor) source.getAnchor();
		Anchor targetAnchor = (Anchor) target.getAnchor();

		sourceAnchor.useRelativePosition(position);
		targetAnchor.useRelativePosition(position);	

		connection.setSourceAnchor(sourceAnchor);
		connection.setTargetAnchor(targetAnchor);

		BendpointConnectionRouter router = new BendpointConnectionRouter();

		List<RelativeBendpoint> bendPoints = new ArrayList<RelativeBendpoint>();

		RelativeBendpoint middle = calculPoint(connection, sourceCenter, targetCenter, position);
		bendPoints.add(middle);

		router.setConstraint(connection,bendPoints);

		connection.setConnectionRouter(router);

		panel.add(connection);
	}


	/**
	 * Calculate a relative point in order to display the arc of circle
	 * @param connection The connection
	 * @param source The source of connection
	 * @param target The target of connection
	 * @param position The relative position of the target to the source
	 */
	private static RelativeBendpoint calculPoint(Connection connection, Point source, Point target, int position){
		double distance = source.getDistance(target);
		RelativeBendpoint point = new RelativeBendpoint(connection);
		int value = (int)(0.4*distance);
		point.setWeight(1);
		if(source==target){// If the source and the target are the same point
			position=PositionConstants.NORTH;
			value = 90;
		}
		switch (position) {
		case PositionConstants.NORTH:
			point.setRelativeDimensions(new Dimension(value, 0), new Dimension(value, 0));
			break;
		case PositionConstants.SOUTH:
			point.setRelativeDimensions(new Dimension(-value, 0), new Dimension(-value, 0));
			break;
		case PositionConstants.EAST:
			point.setRelativeDimensions(new Dimension(0, value/2+50), new Dimension(0, value/2+50));
			break;
		case PositionConstants.WEST:
			point.setRelativeDimensions(new Dimension(0, -(value/2+50)), new Dimension(0, -(value/2+50)));
			break;
		default:
			break;
		}
		return point;
	}
}
