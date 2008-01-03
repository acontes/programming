package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 * An implementation of a host figure. The geometry used is a flat 1x1 square
 * 
 * @author vasile
 * 
 */
public class Host3D extends AbstractHost3D {
	/**
	 * @param name
	 *            the host name to be displayed
	 */
	public Host3D(String name) {
		super(name);
	}

	/*
	 * The subfigure is attached to the transform of the figure itself,
	 * therefore any placement takes place in the local coordinate system of the
	 * figure.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
	 */
	@Override
	public void arrangeSubFigures() {
		int i = 1; // to count the runtimes
		for (AbstractFigure3D runtime : this.getSubFigures().values()) {
			PlacementBasket.xArrangement(0.1, 0.1, 0.1, i, this.getSubFigures()
					.size(), runtime, this);
			i++;
			runtime.arrangeSubFigures();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
	 */
	@Override
	protected Geometry createGeometry() {
		return GeometryBasket.getDefaultHostGeometry();
	}

	// code to create default appearance of visual object
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
	 */
	@Override
	protected Appearance createAppearance() {
		return AppearanceBasket.defaultHostAppearance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
	 */
	@Override
	protected TransformGroup createTextBranch() {
		return TextStylesBasket.hostText(getShortenedName(15));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
	 */
	@Override
	protected void animateCreation() {
		// new AnimationBasket().fadeInto(this, 2000);
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