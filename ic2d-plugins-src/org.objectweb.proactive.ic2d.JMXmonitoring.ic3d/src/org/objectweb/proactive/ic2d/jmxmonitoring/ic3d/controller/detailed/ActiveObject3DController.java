/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed;

import java.util.Observable;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.objectweb.proactive.ic2d.chartit.data.resource.IResourceDescriptor;
import org.objectweb.proactive.ic2d.chartit.editor.ChartItDataEditor;
import org.objectweb.proactive.ic2d.jmxmonitoring.action.ChartItAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.FigureType;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu.MenuAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.ActiveObject3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * @author esalagea
 * 
 */
public class ActiveObject3DController extends AbstractActiveObject3DController {
    private static final Logger logger = Logger.getLogger(ActiveObject3DController.class.getName());

    /**
     * @param modelObject
     * @param figure3D
     * @param parent
     */
    public ActiveObject3DController(final AbstractData modelObject, final Figure3D figure3D,
            final Figure3DController parent) {
        super(modelObject, figure3D, parent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createChildController(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
     */
    @Override
    protected AbstractFigure3DController createChildController(final AbstractData figure) {
        ActiveObject3DController.logger.debug("No children have been implemented for ActiveObjects");
        throw new NotImplementedException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController#createFigure(java.lang.String)
     */
    @Override
    protected AbstractFigure3D createFigure(final String name) {
    	ActiveObject3D ao = new ActiveObject3D(name);
    	ao.addObserver(this);
        return ao;
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
            	case STATE_CHANGED: {
                	// new Thread() {
                	// public void run() {
                	// Thread also has a State enum, careful if using a new thread, the
                	// full package name must be specified
                	((ActiveObject3D) this.getFigure()).setState((State) notif.getData());
                	// }
                	// }.start();
                	break;
            	}
            	case ACTIVE_OBJECT_REQUEST_QUEUE_LENGHT_CHANGED: {
                	final int queueSize = (Integer) notif.getData();
                	((ActiveObject3D) this.getFigure()).setQueueSize(queueSize);
                	break;
            	}
            	case ACTIVE_OBJECT_ADD_COMMUNICATION: {
            		//Never executed because ACTIVE_OBJECT_ADD_COMMUNICATION never notified
//            					System.out.println("ADD_COMMUNICATION");
//                				//remove because the global registry of figures has been removed
//                				//FIXME create alternative implementation
//                				// new Thread(new Runnable() {
//            					// public void run() {
//                				final ActiveObject aoSource = (ActiveObject) notif.getData();
//                	
//                				// final ActiveObject aoDestination = (ActiveObject)
//                				// notificationSender;
//                				if ((aoSource == null)) {
//                					ActiveObject3DController.logger
//                							.warn("No source object found for commmunication");
//                					return;
//            					}
//                	
//                				final Figure3DController srcController = registry.get(aoSource);
//                	
//                				if (srcController == null) {
//                					return;
//                				}
//                				
//                				final ActiveObject3D source3d = (ActiveObject3D) srcController
//                						.getFigure();
//                	
//                				// the destination is this figure
//                				final ActiveObject3D dest3d = (ActiveObject3D) ActiveObject3DController.this
//                						.getFigure();
//                	
//                				if (source3d == null) {
//                					ActiveObject3DController.logger
//                							.warn("No figures found for source commmunication "
//                									+ aoSource.getKey());
//                					return;
//                				}
//                	
//                				if (dest3d == null) {
//                					ActiveObject3DController.logger
//                							.warn("No figures found for destination commmunication ");
//                					return;
//                				}
//                				
//                				final Figure3DController rootGridController = ActiveObject3DController.this
//                						.getParent(). // node
//                						getParent(). // runtime
//                						getParent(). // host
//                						getParent();// grid
//                				// get the figure for the grid
//                				final Figure3D rootGrid = rootGridController.getFigure();
//                				
//                				//rootGrid.drawCommunication(UUID.randomUUID().toString(), name, timeToLive, startAO, stopAO)
//                				// TODO remove constant
//                				// draw the communications on the grid with the given starting and
//                				// stopping points
//                				rootGrid.drawCommunication(UUID.randomUUID().toString(), "", 5,
//                						source3d, dest3d);
//                				// }
//            					// }).start();
//            					// else
//            					// Logger.getRootLogger().log(
//            					// Priority.INFO,
//                				// "Communication from " + aoSource + ":" + source3d + " to "
//                				// + this + ":" + dest3d);
//
                	break;
            	} // switch
            	default:
            		super.update(o, arg);
        	}
    	}
    	
    	else {
    		logger.trace("Active Object controller receving message from the view");
    		System.out.println("Active Object controller receving message from the view");
    		// Context menu
    		if(arg instanceof MenuAction) {
    			MenuAction menuAction = (MenuAction)arg;
    			ActiveObject ao = (ActiveObject)this.getModelObject();
    			switch (menuAction) {
					case AO_CHARTIT:
						try {
							final IResourceDescriptor descriptor = new AbstractDataDescriptor(ao);
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									try {
										ChartItDataEditor.openNewFromResourceDescriptor(descriptor,ChartItAction.PARUNTIME_CHARTIT_CONFIG_FILENAME);
									} catch (PartInitException e) {									
										e.printStackTrace();
									}
								}
							});											
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
    			}
    		}
    		// ActiveObject Migration
    		else if(arg instanceof AbstractFigure3D && ((AbstractFigure3D)arg).getType() == FigureType.NODE) {
    			System.out.println(this.getModelObject() + "Dropping on a node");
    			// When a node receive an active object he migrates it to itself
    			((AbstractFigure3D)arg).notifyObservers(this.getModelObject());
    			// TODO retrieve the URL of the target Node and migrate the active object
    		}
    	}
    }
}