package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;

/**
 * Common interface for all the figure controller that defines all the basic
 * action the controllers should perform: adding/removing children,
 * setting/removing figures, and getting parent/child controllers .
 * 
 * @version $Id$
 * @since 3.9
 * @author vjuresch
 * 
 */
public interface Figure3DController {

	/**
	 * Unsubscribes itself from the modelObject and removes its graphical
	 * representation from the graphical tree. This is done after calling
	 * remove() recursively on all the children.
	 */
	void remove();

	void removeChildren();

	void addChildController(Figure3DController figureController);

	Figure3D getFigure();

	Figure3D getParentFigure();

	void removeFigure(String key);

	Figure3DController getParent();

	Figure3DController getChildControllerByKey(String key);

	AbstractData getModelObject();

}