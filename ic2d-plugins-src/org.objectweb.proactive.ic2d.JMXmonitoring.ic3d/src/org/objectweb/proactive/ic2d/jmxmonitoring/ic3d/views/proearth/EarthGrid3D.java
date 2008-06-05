/**
 * 
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.proearth;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.GeometryBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.PlacementBasket;

/**
 * An implementation of a grid figure. It uses a Earth texture and places the
 * hosts on a sphere. The arrows used to show communications are curved. <br/>
 * WORK IN PROGRESS
 * 
 * @author vasile
 * 
 */
public class EarthGrid3D extends AbstractGrid3D {

	/**
	 * @param name
	 *            name of the grid
	 */
	public EarthGrid3D(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.AbstractFigure3D#arrangeSubFigures()
	 */
	@Override
	public void arrangeSubFigures() {
		int i = 2;
		for (AbstractFigure3D host : this.getSubFigures().values()) {
			PlacementBasket.sphereArrangement(Math.random() * 4 * Math.PI, Math
					.random()
					* 4 * Math.PI, host);

			i++;
			host.arrangeSubFigures();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.AbstractFigure3D#createAppearance()
	 */
	@Override
	protected Appearance createAppearance() {
		// TODO Auto-generated method stub
		return AppearanceBasket.earthGridAppearance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.AbstractFigure3D#createGeometry()
	 */
	@Override
	protected Geometry createGeometry() {
		// TODO Auto-generated method stub
		return GeometryBasket.getEarthGridGeometry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.AbstractFigure3D#createTextBranch()
	 */
	@Override
	protected TransformGroup createTextBranch() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.AbstractFigure3D#animateCreation()
	 */
	@Override
	protected void animateCreation() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(java.lang.String,
	 *      javax.vecmath.Vector3f, javax.vecmath.Vector3f)
	 */
	@Override
	protected AbstractFigure3D setArrow(String name, Vector3f start,
			Vector3f stop) {
		// TODO Auto-generated method stub
		return null;
	}
}
