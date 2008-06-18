/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 * An implementation for a node figure. The figure is a cube 1x1x1 that gets
 * resized when placed on the runtime.
 * 
 * @author vjuresch
 * 
 */
public class Node3D extends AbstractNode3D {
	public Node3D(final String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
	 */
	@Override
	public void arrangeSubFigures() {
		int i = 1; // to count the nodes
		// TODO remove constants
		// iterate through all the ao of the node
		// and place them vertically
		for (final Figure3D ao : this.getSubFigures().values()) {
			PlacementBasket.sphericalAOVerticalArrangement(0.05, 1.5, i, this
					.getSubFigures().size(), ao, this);
			i++;
			ao.arrangeSubFigures();
		}
		// //change the height of the node to fit the number of active objects
		// TransformGroup scaleHeight =
		// (TransformGroup)this.getParent().getParent();
		// Transform3D height = new Transform3D();
		// scaleHeight.getTransform(height);
		// height.setScale(new Vector3d(1,1,i * 1.5 + 0.1));

		// scaleHeight.setTransform(height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
	 */
	@Override
	protected Geometry createGeometry() {
		return GeometryBasket.getDefaultNodeGeometry();
	}

	// code to create default appearance of visual object
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
	 */
	@Override
	protected Appearance createAppearance() {
		return AppearanceBasket.defaultNodeAppearance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
	 */
	@Override
	protected TransformGroup createTextBranch() {
		return TextStylesBasket.nodeText(this.getShortenedName(10));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
	 */
	@Override
	public void animateCreation() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(javax.vecmath.Vector3f,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected Figure3D setArrow(final String name, final Vector3f start,
			final Vector3f stop) {
		// TODO Auto-generated method stub
		return null;
	}
}
