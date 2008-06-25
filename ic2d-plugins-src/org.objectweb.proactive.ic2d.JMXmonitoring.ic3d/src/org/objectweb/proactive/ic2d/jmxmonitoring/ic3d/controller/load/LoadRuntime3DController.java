/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.load;

import java.util.Observable;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.MonitorRuntime3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.MonitoringTypes;
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
        return new MonitorRuntime3D(name);
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
                	((MonitorRuntime3D) this.getFigure()).setThreads(threads);
                	((MonitorRuntime3D) this.getFigure()).setMonitor(mvcNotif);                
                	//LoadRuntime3DController.logger.debug("The number of threads has changed: " + threads);
                	break;
            	}
            	case RUNTIME_HEAP_MEMORY_CHANGED : {
            		long heapUsed = (Long) notif.getData();
            		// TODO Use the getUsage() function of the memory MXBean, to scale this value
            		heapUsed /= 1024 * 1024 * 1024;
                	((MonitorRuntime3D) this.getFigure()).setHeapUsed(heapUsed);
                	((MonitorRuntime3D) this.getFigure()).setMonitor(mvcNotif);
                	//LoadRuntime3DController.logger.debug("The heap memory usage has changed: " + heapUsed);
                	break;
            	}
        	}
    	}
    }
}
