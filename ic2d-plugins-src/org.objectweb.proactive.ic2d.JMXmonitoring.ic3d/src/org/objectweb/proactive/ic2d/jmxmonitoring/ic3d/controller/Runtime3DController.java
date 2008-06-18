/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.Observable;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Runtime3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;

/**
 * @author esalagea
 * 
 */
public class Runtime3DController extends AbstractRuntime3DController {
	private final static Logger logger = Logger
			.getLogger(AbstractRuntime3DController.class.getName());

	public Runtime3DController(final AbstractData modelObject,
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
		return new Node3DController(modelObject, this.getFigure(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
	 */
	@Override
	protected AbstractFigure3D createFigure(final String name) {
		return new Runtime3D(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#removeFigure(java.lang.String)
	 */
	@Override
	public void removeFigure(final String key) {
		// TODO Auto-generated method stub
	}

	public void update(final Observable observable, final Object arg) {
		final MVCNotification notif = (MVCNotification) arg;
		// final Observable notificationSender = o;
		final MVCNotificationTag mvcNotif = notif.getMVCNotification();
		// check the posibilities
		switch (mvcNotif) {
		case RUNTIME_THREADS_CHANGED: {
			final int threads = (Integer) notif.getData();
			((Runtime3D) this.getFigure()).setThreads(threads);
			Runtime3DController.logger
					.debug("The number of threads has changed " + threads);
			break;
		}
		default:
			super.update(observable, arg);
		}
	}

}
