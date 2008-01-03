/**
 * 
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 * An implementation of an active object queue.
 * 
 * @author vasile
 * 
 */
public class Queue3D extends AbstractQueue3D {

	/**
	 * Creates a new queues
	 * 
	 * @param name
	 *            name of the queue
	 */
	public Queue3D(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor that has an empty string as the default name for the queue.
	 */
	public Queue3D() {
		super("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
	 */
	@Override
	public void arrangeSubFigures() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
	 */
	@Override
	protected Appearance createAppearance() {
		return AppearanceBasket.queueAppearance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
	 */
	@Override
	protected Geometry createGeometry() {
		return GeometryBasket.getDefaultQueueGeometry();
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
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(javax.vecmath.Vector3f,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected AbstractFigure3D setArrow(String name, Vector3f start,
			Vector3f stop) {
		// TODO Auto-generated method stub
		return null;
	}

}
