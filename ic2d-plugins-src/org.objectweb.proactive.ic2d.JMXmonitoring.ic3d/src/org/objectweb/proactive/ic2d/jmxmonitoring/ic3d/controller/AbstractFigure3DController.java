package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Figure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;


public abstract class AbstractFigure3DController implements Observer, Figure3DController {
    private static final Logger logger = Logger.getLogger(AbstractFigure3DController.class.getName());
    /**
     * The figure of the parent controller
     */
    private final Figure3D parentFigure;

    // private HashMap<String, AbstractFigure3D> figures =
    // new HashMap<String, AbstractFigure3D>();
    // model object for the 3D figure
    //removed because it breaks load monitoring and earth view  - global variables are bad
    //	protected static final Map<AbstractData, Figure3DController> registry = new ConcurrentHashMap<AbstractData, Figure3DController>();
    private final AbstractData modelObject; // model object
    // 3D figure controlled
    private final Figure3D figure; // 3dFigure
    /**
     * The parent controller
     */
    private final Figure3DController parent;

    // children controllers
    private final Collection<Figure3DController> childrenControllers = new ArrayList<Figure3DController>();

    /**
     * Creates a controller that observes the modelObject,
     * has a parent figure, and a parent controller. 
     * 
     * @param modelObject
     * @param parentFigure3D
     * @param parent
     */
    public AbstractFigure3DController(final AbstractData modelObject, final Figure3D parentFigure3D,
            final Figure3DController parent) {
        this.modelObject = modelObject;
        this.parent = parent;
        // set the parent figure
        this.parentFigure = parentFigure3D;

        // add the figure in the 3D world
        final Figure3D abstractFig3d = this.createFigure(modelObject.getName());
        this.figure = abstractFig3d;
        this.parentFigure.addSubFigure(modelObject.getKey(), abstractFig3d);

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
                final Figure3DController controller = this.createChildController(childModelObject);
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

                    final Figure3DController controller = this.createChildController(childModelObject);
                    this.addChildController(controller);
                    //				AbstractFigure3DController.registry.put(childModelObject,
                    //						controller);
                } // [for all keys]
                break;
            } // [case ADD_CHILDREN]
            case REMOVE_CHILD: {
                final String figureKey = (String) mvcNotif.getData();
                final Figure3DController childController = this.getChildControllerByKey(figureKey);
                AbstractFigure3DController.logger.debug("Removing child controller: " + "key [" + figureKey +
                    "]" + " controller [" + childController + "]...");
                if (childController == null) {
                    AbstractFigure3DController.logger.debug("Child already removed " + figureKey);
                    return;
                }

                childController.remove();
                AbstractFigure3DController.logger.debug("Child controller: " + "key [" + figureKey + "]" +
                    " controller [" + childController + "] removed");
                break;
            }
            case REMOVE_CHILD_FROM_MONITORED_CHILDREN: {
                final String figureKey = (String) mvcNotif.getData();
                final Figure3DController childController = this.getChildControllerByKey(figureKey);
                if (childController == null) {
                    AbstractFigure3DController.logger.debug("Child already removed " + figureKey);
                    return;
                }
                AbstractFigure3DController.logger
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
        AbstractFigure3DController.logger.debug("Trying to remove all the children for controller [" 
        		+ this.toString() + "]");
        this.removeChildren();
        AbstractFigure3DController.logger.debug("All children removed for controller [" 
        		+ this.toString() + "]");
        AbstractFigure3DController.logger.debug("Unsubscribing as a listener... ");

        // unsubscribe itself as observer
        this.modelObject.deleteObserver(this);
        AbstractFigure3DController.logger.debug("I'm removing myself from the controller registry... ");
        // remove itself from the registry
        //		AbstractFigure3DController.registry.remove(this.modelObject);
        // System.out.println("Object model removed");

        // ---not right---
        // figure.getRootBranch().removeAllChildren();

        // figure.removeAllSubFigures();

        //        
        // //the parent of this figure (the one
        // //corresponding to the controller that called remove
        // //should remove this particular figure)
        // System.out.println(figure);

        // remove itself from the figure tree
        AbstractFigure3DController.logger.debug("I'm deleting my graphical representation [" + this.figure +
            "]");
        this.parentFigure.removeSubFigure(this.figure);
        AbstractFigure3DController.logger.debug("Controller [" + this + "] and coresponding " + this.figure +
            "graphical figure removed");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#removeChildren()
     */
    public void removeChildren() {
        for (final Figure3DController c : this.childrenControllers) {
            if(c != null)
            	c.remove();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#addChildController(org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractFigure3DController)
     */
    public void addChildController(final Figure3DController figureController) {
        this.childrenControllers.add(figureController);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#getFigure()
     */
    public Figure3D getFigure() {
        return this.figure;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#getParentFigure()
     */
    public Figure3D getParentFigure() {
        return this.parentFigure;
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
    public Figure3DController getParent() {
        return this.parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#getChildControllerByKey(java.lang.String)
     */
    public Figure3DController getChildControllerByKey(final String key) {
        if (key == null) {
            return null;
        }
        for (final Figure3DController c : this.childrenControllers) {
            if( c == null)
            	return null;
        	if (key.equals(c.getModelObject().getKey())) {
                return c;
            }
        }
        return null;
    }

    // --------- TO BE IMPLEMENTED IN THE ACTUAL CONTROLLERS

    /**
     * use it to call addFigure with the appropriate figure
     * 
     */
    protected abstract Figure3D createFigure(String name);

    // use it to create the appropriate child controller
    protected abstract Figure3DController createChildController(AbstractData figure);

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Figure3DController#getModelObject()
     */
    public AbstractData getModelObject() {
        return this.modelObject;
    }
}
