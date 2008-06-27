/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.load;

import java.util.Observable;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.HostObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu.MenuAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.AbstractLoadHost3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.EmptyLoadHost3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.LoadHost3D;


/**
 * @author vjuresch
 * 
 */
public class LoadHost3DController extends AbstractLoadHost3DController {
    public LoadHost3DController(final AbstractData modelObject, final Figure3D figure3D,
            final Figure3DController parent) {
        super(modelObject, figure3D, parent);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#addFigureByKey(java.lang.String,
     *      java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(final String name) {
    	AbstractLoadHost3D loadHost = null;
    	switch (((LoadGrid3DController)this.getParent()).getGridMode()) {
    		case RUNTIME_HEAP_MEMORY_USED:
    		case RUNTIME_THREADS:
    			loadHost = new EmptyLoadHost3D(name);		
    			break;
    		default:
    			loadHost = new LoadHost3D(name);
		}
    	return loadHost;
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
    protected Figure3DController createChildController(AbstractData figure) {
    	LoadRuntime3DController loadRuntime3DController = null;
    	switch (((LoadGrid3DController)this.getParent()).getGridMode()) {
    		case RUNTIME_HEAP_MEMORY_USED:
    		case RUNTIME_THREADS:
    			loadRuntime3DController = new LoadRuntime3DController(figure, this.getFigure(), this);
    	}
        return loadRuntime3DController;
    }
    
    @Override
    public void update(final Observable o, final Object arg) {
    	if ( o != null) {
    		super.update(o, arg);
    	}
    	else {
    		MenuAction menuAction = (MenuAction)arg;
    		HostObject host = (HostObject)this.getModelObject();
    		switch (menuAction) {
				case HOST_REFRESH:
					host.explore();
					break;
				case HOST_STOP_MONITORING:
					host.stopMonitoring(true);
					break;
				case HOST_CHARTIT:
					break;
    		}
    	}
    }
}
