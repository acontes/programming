/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Node3D;

/**
 * @author esalagea
 * 
 */
public class Node3DController extends AbstractNode3DController {
	public Node3DController(final AbstractData modelObject,
			final Figure3D figure3D, final Figure3DController parent) {
		super(modelObject, figure3D, parent);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createChildController(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
	 */
	@Override
	protected AbstractFigure3DController createChildController(
			final AbstractData modelObject) {
		return new ActiveObject3DController(modelObject, this.getFigure(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
	 */
	@Override
	protected AbstractFigure3D createFigure(final String name) {
		return new Node3D(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#removeFigure(java.lang.String)
	 */
	@Override
	public void removeFigure(final String key) {
	}
}
