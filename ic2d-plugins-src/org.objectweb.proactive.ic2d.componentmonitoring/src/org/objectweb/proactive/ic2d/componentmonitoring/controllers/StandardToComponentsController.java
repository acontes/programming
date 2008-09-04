package org.objectweb.proactive.ic2d.componentmonitoring.controllers;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;


/**
 * This controller listens to the IC2D standard model and updates the component model
 * Then, is up to this model's controllers to update the view representing the components model. 
 * 
 * @version $Id$
 * @since 3.9
 * @author vjuresch, esalagea
 * 
 */
public interface StandardToComponentsController {

    /**
     * Unsubscribes itself from the modelObject and removes its graphical
     * representation from the graphical tree. This is done after calling
     * remove() recursively on all the children.
     */
    void remove();

    void removeChildren();

    void addChildController(StandardToComponentsController figureController);

    StandardToComponentsController getParent();

    StandardToComponentsController getChildControllerByKey(String key);

    AbstractData getModelObject();

}