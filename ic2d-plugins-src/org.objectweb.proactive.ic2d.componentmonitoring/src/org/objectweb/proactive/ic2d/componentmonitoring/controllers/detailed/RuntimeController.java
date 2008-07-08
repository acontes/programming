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
public class RuntimeController extends AbstractStandardToComponentsController {
    public RuntimeController(final AbstractData modelObject,
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
        return new NodeController(modelObject,  this);
    }
    
    @Override
    public void update(final Observable o, final Object arg) {
    	System.out.println("RuntimeController.update()");
    	super.update(o, arg);
    }
}
