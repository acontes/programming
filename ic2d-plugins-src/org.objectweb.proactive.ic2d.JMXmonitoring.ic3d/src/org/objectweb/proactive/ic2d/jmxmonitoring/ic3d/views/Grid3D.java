/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 * @author vjuresch
 * 
 */
public class Grid3D extends AbstractGrid3D {
	public Grid3D() {
		super("");
	}

	// add a new host and translate its
	// position according to the coordinates
	// (add to a custom location)

	// TODO ***HORRIBLE PLACING ALGORITHM*** MUST BE CHANGED !!! the cost is n^2
	// * method calls in depth
	// for each host or removed placed, the entire scene gets rearranged

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
	 */
	@Override
	public void arrangeSubFigures() {
		int i = 2;
		for (AbstractFigure3D host : this.getSubFigures().values()) {
			PlacementBasket.spiralArrangement(i, host);
			i++;
			host.arrangeSubFigures();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
	 */
	@Override
	protected Geometry createGeometry() {
		// return GeometryBasket.getCoordinatesGeometry();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
	 */
	@Override
	protected TransformGroup createTextBranch() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
	 */
	@Override
	protected void animateCreation() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
	 */
	@Override
	protected Appearance createAppearance() {
		// return AppearanceBasket.coordinatesAppearance;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(javax.vecmath.Vector3f,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected AbstractFigure3D setArrow(String name, Vector3f start,
			Vector3f stop) {
		// TODO Auto-generated method stub
		return new Arrow3D("", start, stop);
	}
}
