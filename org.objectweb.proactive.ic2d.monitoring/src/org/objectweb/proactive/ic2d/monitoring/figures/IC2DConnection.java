package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;

public class IC2DConnection  extends PolylineConnection{

	private RotatableDecoration targetDecoration;
	
	public IC2DConnection(){
		super();
		
		targetDecoration = getTargetDecoration();
		setTargetDecoration(targetDecoration);
		setLineWidth(1);
		setForegroundColor(ColorConstants.black);
		setLineStyle(Graphics.LINE_SOLID);
	}
	
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
