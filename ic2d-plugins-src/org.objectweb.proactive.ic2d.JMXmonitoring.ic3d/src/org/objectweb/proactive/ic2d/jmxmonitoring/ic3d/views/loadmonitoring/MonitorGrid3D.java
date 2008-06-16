package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.PlacementBasket;

public class MonitorGrid3D extends AbstractGrid3D {

	public MonitorGrid3D() {
		super("");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void animateCreation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void arrangeSubFigures() {
		// TODO Auto-generated method stub
		int i = 2;
        for (AbstractFigure3D host : this.getSubFigures().values()) {
            PlacementBasket.matrixArrangement(i, host);
            i++;
            host.arrangeSubFigures();
        }
	}

	@Override
	protected Appearance createAppearance() {
		// TODO Auto-generated method stub
		return AppearanceBasket.defaultNodeAppearance;
	}

	@Override
	protected Geometry createGeometry() {
		return GeometryBasket.getDefaultHostGeometry();
	}

	@Override
	protected TransformGroup createTextBranch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractFigure3D setArrow(String name, Vector3f start,
			Vector3f stop) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void translateGrid(Vector3d position) {
		TransformGroup myTranslateTG = getTranslateScaleTransform();
		Transform3D myTranslation = new Transform3D();
		myTranslation.setTranslation(position);
		myTranslateTG.setTransform(myTranslation);
	}

}
