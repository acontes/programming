/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.Observable;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Grid3D;

/**
 * @author vjuresch
 * 
 */
public class Grid3DController extends AbstractGrid3DController {
	public Grid3DController(final AbstractData modelObject,
			final Figure3D figure3D, final Figure3DController parent) {
		super(modelObject, figure3D, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(final Observable o, final Object arg) {
		// TODO Auto-generated method stub
		super.update(o, arg);
	}

	@Override
	protected AbstractFigure3D createFigure(final String name) {
		// TODO Auto-generated method stub
		// the grid has no name in the current implementation
		return new Grid3D();
	}

	@Override
	public void removeFigure(final String key) {
		// TODO Auto-generated method stub
	}

	@Override
	protected AbstractFigure3DController createChildController(
			final AbstractData figure) {
		// TODO Auto-generated method stub
		return new Host3DController(figure, this.getFigure(), this);
	}
}
