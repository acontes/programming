/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.Map;
import java.util.Observable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractActiveObject3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.ActiveObject3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Grid3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractHost3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.ActiveObject3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Grid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


/**
 * @author esalagea
 * 
 */
public class ActiveObject3DController extends AbstractActiveObject3DController {
    /**
     * @param modelObject
     * @param figure3D
     * @param parent
     */
    public ActiveObject3DController(AbstractData modelObject, AbstractFigure3D figure3D,
            AbstractFigure3DController parent) {
        super(modelObject, figure3D, parent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createChildController(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
     */
    @Override
    protected AbstractFigure3DController createChildController(AbstractData figure) {
        System.out.println("No children have been implemented for ActiveObjects");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(String name) {
        return new ActiveObject3D(name);
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
            case STATE_CHANGED: {
                // new Thread() {
                // public void run() {
                // Thread also has  a State enum, careful if using	 a new thread, the full package name must be specified
                ((ActiveObject3D) getFigure()).setState((State) notif.getData());
                // }
                // }.start();
                break;
            }
            case ACTIVE_OBJECT_ADD_COMMUNICATION: {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ActiveObject aoSource = (ActiveObject) notif.getData();

                        // final ActiveObject aoDestination = (ActiveObject)
                        // notificationSender;
                        if ((aoSource == null)) {
                            System.out.println("no source object found for com");
                            return;
                        }

                        AbstractFigure3DController srcController = ActiveObject3DController.registry
                                .get(aoSource);

                        if (srcController == null) {
                            return;
                        }
                        ActiveObject3D source3d = (ActiveObject3D) srcController.getFigure();

                        //the destination is this figure
                        ActiveObject3D dest3d = (ActiveObject3D) ActiveObject3DController.this.getFigure();

                        if (source3d == null) {
                            System.out.println("no figures found for source com " + aoSource.getKey());
                            return;
                        }

                        if (dest3d == null) {
                            System.out.println("no figures found for dest com ");
                            return;
                        }
                        //get the grid controller
                        AbstractFigure3DController rootGridController = (AbstractFigure3DController) ActiveObject3DController.this
                                .getParent(). // node
                                getParent(). // runtime
                                getParent(). // host
                                getParent();// grid
                        //get the figure for the grid
                        AbstractFigure3D rootGrid = (AbstractFigure3D) rootGridController.getFigure();
                        //TODO remove constant
                        //draw the communications on the grid with the given starting and stopping points
                        rootGrid.drawCommunication(UUID.randomUUID().toString(), "", 100, source3d, dest3d);
                    }
                }).start();
                // else
                //			Logger.getRootLogger().log(
                //					Priority.INFO,
                //					"Communication from " + aoSource + ":" + source3d + " to "
                //							+ this + ":" + dest3d);

                break;
            } // switch
            default:
                super.update(o, arg);
        }
    }
}
