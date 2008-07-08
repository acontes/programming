package org.objectweb.proactive.ic2d.componentmonitoring.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;


public abstract class AbstractStandardToComponentsController implements Observer, StandardToComponentsController {
    private static final Logger logger = Logger.getLogger(AbstractStandardToComponentsController.class.getName());
    /**
     * The figure of the parent controller
     */
    

   
    private final AbstractData modelObject; // model object

    /**
     * The parent controller
     */
    private final StandardToComponentsController parent;

    // children controllers
    private final Collection<StandardToComponentsController> childrenControllers = new ArrayList<StandardToComponentsController>();

    /**
     * Creates a controller that observes the modelObject,
     * has a parent figure, and a parent controller. 
     * 
     * @param modelObject
     * @param parentFigure3D
     * @param parent
     */
    public AbstractStandardToComponentsController(final AbstractData modelObject, 
            final StandardToComponentsController parent) {
        this.modelObject = modelObject;
        this.parent = parent;
        // set this as the observer for the figureModel
        modelObject.addObserver(this);
    }

    public void update(final Observable observable, final Object arg) {
    	
    	
        // get the notification data
        final MVCNotification mvcNotif = (MVCNotification) arg;
        final MVCNotificationTag mvcNotifTag = mvcNotif.getMVCNotification();

        // check the posibilities
        switch (mvcNotifTag) {
            case ADD_CHILD: {
                // add new controller/figure
                // get key
                final String figureKey = (String) mvcNotif.getData();

                // get data on the figure
                final AbstractData childModelObject = ((AbstractData) observable)
                        .getMonitoredChild(figureKey);

                // System.out.println("----------->> new host added: "+hostKey);
                final StandardToComponentsController controller = this.createChildController(childModelObject);
                this.addChildController(controller);
                //			AbstractFigure3DController.registry.put(childModelObject,
                //					controller);
                

                break;
            }
            case ADD_CHILDREN: {
                // HostObject hostObj=this.host;
                final List<String> keys = (ArrayList<String>) mvcNotif.getData();

                for (int k = 0; k < keys.size(); k++) {
                    final String modelObjectKey = keys.get(k);
                    final AbstractData childModelObject = this.modelObject.getChild(modelObjectKey);

                    final StandardToComponentsController controller = this.createChildController(childModelObject);
                    this.addChildController(controller);
                    //				AbstractFigure3DController.registry.put(childModelObject,
                    //						controller);
                } // [for all keys]
                
                System.out.println("in AbstractStandardToComponentsController get Notification : add Children");
                break;
            } // [case ADD_CHILDREN]
            case REMOVE_CHILD: {
                final String figureKey = (String) mvcNotif.getData();
                final StandardToComponentsController childController = this.getChildControllerByKey(figureKey);
                AbstractStandardToComponentsController.logger.debug("Removing child controller: " + "key [" + figureKey +
                    "]" + " controller [" + childController + "]...");
                if (childController == null) {
                    AbstractStandardToComponentsController.logger.debug("Child already removed " + figureKey);
                    return;
                }

                childController.remove();
                AbstractStandardToComponentsController.logger.debug("Child controller: " + "key [" + figureKey + "]" +
                    " controller [" + childController + "] removed");
                break;
            }
            //TODO: see if we need this in the component view 
            case REMOVE_CHILD_FROM_MONITORED_CHILDREN: {
                final String figureKey = (String) mvcNotif.getData();
                final StandardToComponentsController childController = this.getChildControllerByKey(figureKey);
                if (childController == null) {
                    AbstractStandardToComponentsController.logger.debug("Child already removed " + figureKey);
                    return;
                }
                AbstractStandardToComponentsController.logger
                        .debug("Removing child:" + figureKey + ":" + childController);

                childController.remove();
                break;
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#remove()
     */
    public void remove() {
        AbstractStandardToComponentsController.logger.debug("Trying to remove all the children for controller [" 
        		+ this.toString() + "]");
        this.removeChildren();
        AbstractStandardToComponentsController.logger.debug("All children removed for controller [" 
        		+ this.toString() + "]");
        AbstractStandardToComponentsController.logger.debug("Unsubscribing as a listener... ");

        // unsubscribe itself as observer
        this.modelObject.deleteObserver(this);
        AbstractStandardToComponentsController.logger.debug("I'm removing myself from the controller registry... ");
      

       }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#removeChildren()
     */
    public void removeChildren() {
        for (final StandardToComponentsController c : this.childrenControllers) {
            if(c != null)
            	c.remove();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#addChildController(org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController)
     */
    public void addChildController(final StandardToComponentsController figureController) {
        this.childrenControllers.add(figureController);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#removeFigure(java.lang.String)
     */
    public abstract void removeFigure(String key);

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#getParent()
     */
    public StandardToComponentsController getParent() {
        return this.parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#getChildControllerByKey(java.lang.String)
     */
    public StandardToComponentsController getChildControllerByKey(final String key) {
        if (key == null) {
            return null;
        }
        for (final StandardToComponentsController c : this.childrenControllers) {
            if( c == null)
            	return null;
        	if (key.equals(c.getModelObject().getKey())) {
                return c;
            }
        }
        return null;
    }


    // use it to create the appropriate child controller
    protected abstract StandardToComponentsController createChildController(AbstractData figure);

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#getModelObject()
     */
    public AbstractData getModelObject() {
        return this.modelObject;
    }
}
