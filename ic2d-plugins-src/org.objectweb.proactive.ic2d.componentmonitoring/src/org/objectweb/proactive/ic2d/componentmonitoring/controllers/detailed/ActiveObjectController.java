/**
 *
 */
package org.objectweb.proactive.ic2d.componentmonitoring.controllers.detailed;

import java.util.Observable;

import org.objectweb.proactive.ic2d.componentmonitoring.controllers.AbstractStandardToComponentsController;
import org.objectweb.proactive.ic2d.componentmonitoring.controllers.StandardToComponentsController;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;



/**
 * @author vjuresch
 * 
 */
public class ActiveObjectController extends AbstractStandardToComponentsController {
    public ActiveObjectController(final AbstractData modelObject,
            final StandardToComponentsController parent) {
        super(modelObject,  parent);
        // TODO Auto-generated constructor stub
    }



    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractStandardToComponentsControllerController#removeFigure(java.lang.String)
     */
    @Override
    public void removeFigure(final String key) {
        // TODO Auto-generated method stub
    }

    @Override
    protected AbstractStandardToComponentsController createChildController(final AbstractData modelObject) {
        return null;
    }
    
    @Override
    public void update(final Observable o, final Object arg) {
    	super.update(o, arg);
    }
}
