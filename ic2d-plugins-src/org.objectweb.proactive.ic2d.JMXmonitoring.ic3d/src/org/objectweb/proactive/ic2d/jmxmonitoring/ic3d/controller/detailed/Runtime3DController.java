/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed;

import java.util.Observable;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu.MenuAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Runtime3D;


/**
 * @author esalagea
 * 
 */
public class Runtime3DController extends AbstractRuntime3DController {
    private final static Logger logger = Logger.getLogger(AbstractRuntime3DController.class.getName());

    public Runtime3DController(final AbstractData modelObject, final Figure3D figure3D,
            final Figure3DController parent) {
        super(modelObject, figure3D, parent);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createChildController(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
     */
    @Override
    protected AbstractFigure3DController createChildController(final AbstractData modelObject) {
        return new Node3DController(modelObject, this.getFigure(), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(final String name) {
    	Runtime3D runtime = new Runtime3D(name);
    	runtime.addObserver(this);
        return runtime;
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
    
    @Override
    public void update(final Observable o, final Object arg) {
    	if ( o != null) {
    		super.update(o, arg);
    	}
    	else {
    		MenuAction menuAction = (MenuAction)arg;
    		RuntimeObject runtime = (RuntimeObject)this.getModelObject();
    		switch (menuAction) {
				case RUNTIME_CHARTIT:
					break;
				case RUNTIME_KILL:
					runtime.killRuntime();
					break;
				case RUNTIME_REFRESH:
					runtime.explore();
					break;
				case RUNTIME_STOP_MONITORING:
					runtime.stopMonitoring(true);
					break;
    		}
    	}
    }

}
