/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.Observable;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractRuntime3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Node3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.ActiveObject3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Runtime3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


/**
 * @author esalagea
 *
 */
public class Runtime3DController extends AbstractRuntime3DController {
    public Runtime3DController(AbstractData modelObject, AbstractFigure3D figure3D,
            AbstractFigure3DController parent) {
        super(modelObject, figure3D, parent);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createChildController(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
     */
    @Override
    protected AbstractFigure3DController createChildController(AbstractData modelObject) {
        return new Node3DController(modelObject, getFigure(), this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(String name) {
        return new Runtime3D(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#removeFigure(java.lang.String)
     */
    @Override
    public void removeFigure(String key) {
        // TODO Auto-generated method stub
    }
    
    
    
    
    
    
    
    public void update(Observable o, Object arg) {
        final MVCNotification notif = (MVCNotification) arg;
        // final Observable notificationSender = o;
        MVCNotificationTag mvcNotif = notif.getMVCNotification();
        // check the posibilities
        switch (mvcNotif) {
            case RUNTIME_THREADS_CHANGED: {
            	int threads = (Integer) notif.getData();
                ((Runtime3D) getFigure()).setThreads(threads);
                System.out.println("Threads changed");
            	break;
            }
            default:
                super.update(o, arg);
        }
    }

}
