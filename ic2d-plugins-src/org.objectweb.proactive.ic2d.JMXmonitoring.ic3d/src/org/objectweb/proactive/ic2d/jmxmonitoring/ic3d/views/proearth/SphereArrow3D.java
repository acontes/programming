/**
 * 
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.proearth;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractArrow3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.GeometryBasket;

/**
 * Implements a curved arrow.
 * 
 * @author vasile
 * 
 */
public class SphereArrow3D extends AbstractArrow3D {

	/**
	 * @param name
	 *            name of the arrow
	 * @param start
	 *            the start angles on the sphere
	 * @param stop
	 *            the stop angles on the sphere
	 */
	public SphereArrow3D(final String name, final Tuple2d start,
			final Tuple2d stop) {
		super(name);
		// TODO Auto-generated constructor stub
		this.setGeometry(this.createGeometry(start, stop));
	}

	/**
	 * @param start
	 *            the start angles on the sphere
	 * @param stop
	 *            the stop angles on the sphere
	 * @return the arrow geometry
	 */
	private Geometry createGeometry(final Tuple2d start, final Tuple2d stop) {
		// TODO remove constant segments and radius
		return GeometryBasket.sphereArrowGeometry(start, stop, 50, 17);
	}

	/**
	 * @param name
	 */
	public SphereArrow3D(final String name) {
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
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.AbstractFigure3D#createAppearance()
	 */
	@Override
	protected Appearance createAppearance() {
		return AppearanceBasket.defaultLineAppearance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.AbstractFigure3D#createGeometry()
	 */
	@Override
	protected Geometry createGeometry() {
		// TODO Auto-generated method stub
		return null;
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
	public void animateCreation() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(java.lang.String,
	 *      javax.vecmath.Vector3f, javax.vecmath.Vector3f)
	 */
	@Override
	protected Figure3D setArrow(final String name, final Vector3f start,
			final Vector3f stop) {
		// TODO Auto-generated method stub
		return null;
	}

}
