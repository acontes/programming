/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed;

import java.util.Observable;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.HostObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu.MenuAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Host3D;


/**
 * @author vjuresch
 * 
 */
public class Host3DController extends AbstractHost3DController {
    public Host3DController(final AbstractData modelObject, final Figure3D figure3D,
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
        // Logger.getRootLogger().log(Priority.INFO_INT, "Creating figure for
        // host controller:"+name);
    	Host3D host = new Host3D(name);
    	host.addObserver(this);
        return host;
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
    protected AbstractFigure3DController createChildController(final AbstractData modelObject) {
        return new Runtime3DController(modelObject, this.getFigure(), this);
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
					// TODO understand why this part mess up all
					/* IResourceDescriptor resourceDescriptor;
					try {
						resourceDescriptor = new AbstractDataDescriptor(host);
						ChartItDataEditor.openNewFromResourceDescriptor(resourceDescriptor); 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} */
					break;
    		}
    	}
    }
}
