/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.load;

import java.io.IOException;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.chartit.data.resource.IResourceDescriptor;
import org.objectweb.proactive.ic2d.chartit.editor.ChartItDataEditor;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.deprecated.MonitorRuntime3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu.MenuAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.LoadRuntime3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;


/**
 * Controller for monitoring the runtime. Uses as 
 * 3D figure {@link MonitorRuntime3D}.
 * 
 * @author vjuresch
 * @version $Id$
 * @since 3.9 
 */
public class LoadRuntime3DController extends AbstractLoadRuntime3DController {
    private final static Logger logger = Logger.getLogger(AbstractLoadRuntime3DController.class.getName());

    public LoadRuntime3DController(final AbstractData modelObject, final Figure3D figure3D,
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
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(final String name) {
    	LoadRuntime3D runtime = new LoadRuntime3D(name);
    	runtime.addObserver(this);
    	return runtime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#removeFigure(java.lang.String)
     */
    @Override
    public void removeFigure(final String key) { }

    public void update(final Observable o, final Object arg) {
    	if ( o != null) {
    		super.update(o, arg);
        	final MVCNotification notif = (MVCNotification) arg;
        	// final Observable notificationSender = o;
        	final MVCNotificationTag mvcNotif = notif.getMVCNotification();
        	// check the posibilities
        	switch (mvcNotif) {
            	case RUNTIME_THREADS_CHANGED: {
                	final int threads = (Integer) notif.getData();
                	((LoadRuntime3D)this.getFigure()).setScale((double)threads/1000d);
                	break;
            	}
            	case RUNTIME_HEAP_MEMORY_CHANGED : {
            		long heapUsed = (Long) notif.getData();
            		// TODO Use the getUsage() function of the memory MXBean, to scale this value
            		heapUsed /= 1024 * 1024 * 1024;
                	((LoadRuntime3D)this.getFigure()).setScale((double)heapUsed);
                	break;
            	}
        	}
    	}
    	else {
    		MenuAction menuAction = (MenuAction)arg;
    		RuntimeObject runtime = (RuntimeObject)this.getModelObject();
    		switch (menuAction) {
				case RUNTIME_CHARTIT:
					try {
						IResourceDescriptor descriptor = new AbstractDataDescriptor(runtime);
						ChartItDataEditor.openNewFromResourceDescriptor(descriptor);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
