package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;

public abstract class AbstractRuntime3DController extends
		AbstractFigure3DController {
	public AbstractRuntime3DController(final AbstractData modelObject,
			final Figure3D figure3D, final Figure3DController parent) {
		super(modelObject, figure3D, parent);
		// TODO Auto-generated constructor stub
	}
}
