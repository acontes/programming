package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;


public abstract class AbstractFigure3DController implements Observer {
	private static final Logger logger = Logger.getLogger(AbstractFigure3DController.class.getName());
    /**
     * The figure of the parent controller
     */
    private AbstractFigure3D parentFigure;

    // private HashMap<String, AbstractFigure3D> figures =
    // new HashMap<String, AbstractFigure3D>();
    // model object for the 3D figure
    protected static Map<AbstractData, AbstractFigure3DController> registry = new ConcurrentHashMap<AbstractData, AbstractFigure3DController>();
    private AbstractData modelObject; // model object
    // 3D figure controlled
    private AbstractFigure3D figure; // 3dFigure
    /**
     * The parent controller
     */
    private AbstractFigure3DController parent;

    // children controllers
    private ArrayList<AbstractFigure3DController> childrenControllers = new ArrayList<AbstractFigure3DController>();

    public AbstractFigure3DController(AbstractData modelObject, AbstractFigure3D parentFigure3D,
            AbstractFigure3DController parent) {
        this.modelObject = modelObject;
        this.parent = parent;
        // set the parent figure
        this.parentFigure = parentFigure3D;

        // add the figure in the 3D world
        AbstractFigure3D abstractFig3d = createFigure(modelObject.getName());
        figure = abstractFig3d;
        parentFigure.addSubFigure(modelObject.getKey(), abstractFig3d);

        // set this as the observer for the figureModel
        modelObject.addObserver(this);
    }

    public void update(Observable o, Object arg) {
        // get the notification data
        MVCNotification notif = (MVCNotification) arg;
        MVCNotificationTag mvcNotif = notif.getMVCNotification();

        // check the posibilities
        switch (mvcNotif) {
            case ADD_CHILD: {
                // add new controller/figure
                // get key
                String figureKey = (String) notif.getData();

                // get data on the figure
                AbstractData childModelObject = ((AbstractData) o).getMonitoredChild(figureKey);

                // System.out.println("----------->> new host added: "+hostKey);
                AbstractFigure3DController controller = createChildController(childModelObject);
                this.addChildController(controller);
                AbstractFigure3DController.registry.put(childModelObject, controller);

                break;
            }
            case ADD_CHILDREN: {
                // HostObject hostObj=this.host;
                ArrayList<String> keys = (ArrayList<String>) notif.getData();

                for (int k = 0; k < keys.size(); k++) {
                    String modelObjectKey = keys.get(k);
                    AbstractData childModelObject = (AbstractData) modelObject.getChild(modelObjectKey);

                    AbstractFigure3DController controller = createChildController(childModelObject);
                    this.addChildController(controller);
                    AbstractFigure3DController.registry.put(childModelObject, controller);
                } // [for all keys]
                break;
            } // [case ADD_CHILDREN]
            case REMOVE_CHILD: {
                String figureKey = (String) notif.getData();
                AbstractFigure3DController childController = getChildControllerByKey(figureKey);
            	logger.debug("Removing child controller: " + "key [" + figureKey+ "]"
            			+ " controller [" + childController + "]...");
                if (childController == null) {
                    System.out.println("Child already removed " + figureKey);
                    return;
                }

                childController.remove();
                logger.debug("Child controller: " + "key [" + figureKey+ "]"
            			+ " controller [" + childController + "] removed");
                break;
            }
            case REMOVE_CHILD_FROM_MONITORED_CHILDREN: {
                String figureKey = (String) notif.getData();
                AbstractFigure3DController childController = getChildControllerByKey(figureKey);
                if (childController == null) {
                    System.out.println("Child already removed " + figureKey);
                    return;
                }
                System.out.println("Removing child:" + figureKey + ":" + childController);

                childController.remove();
                break;
            }
           
        }
    }

    /**
     * Unsubscribes itself from
     * the modelObject and  removes  its graphical representation
     * from the graphical tree. This is done after calling remove()
     * recursively on all the children.
     */
    public void remove() {
    	logger.debug("Trying to remove all the children for controller ["
    			+ this.toString() +"]");
        removeChildren();
    	logger.debug("All children removed for controller ["
    			+ this.toString() +"]");
    	logger.debug("Unsubscribing as a listener... ");
        
        //unsubscribe itself as observer
        this.modelObject.deleteObserver(this);
    	logger.debug("I'm removing myself from the controller registry... ");        
        //remove itself from the registry
        AbstractFigure3DController.registry.remove(this.modelObject);
        // System.out.println("Object model removed");

        // ---not right---
        //figure.getRootBranch().removeAllChildren();
        
        //figure.removeAllSubFigures();
        
        //        
        // //the parent of this figure (the one
        // //corresponding to the controller that called remove
        // //should remove this particular figure)
        // System.out.println(figure);
        
        //remove itself from the figure tree
    	logger.debug("I'm deleting my graphical representation ["
    			+this.figure+"]");        
        this.parentFigure.removeSubFigure(this.figure);
    	logger.debug("Controller ["+ this +"] and coresponding "
    			+ this.figure + "graphical figure removed");        
    }

    public void removeChildren() {
        for (AbstractFigure3DController c : this.childrenControllers) {
            c.remove();
        }
    }

    public void addChildController(AbstractFigure3DController figureController) {
        this.childrenControllers.add(figureController);
    }

    public AbstractFigure3D getFigure() {
        return figure;
    }

    public AbstractFigure3D getParentFigure() {
        return parentFigure;
    }

    public abstract void removeFigure(String key);

    public AbstractFigure3DController getParent() {
        return parent;
    }

    public AbstractFigure3DController getChildControllerByKey(String key) {
        if (key == null) {
            return null;
        }
        for (AbstractFigure3DController c : this.childrenControllers) {
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
    protected abstract AbstractFigure3D createFigure(String name);

    // use it to create the appropriate child controller
    protected abstract AbstractFigure3DController createChildController(AbstractData figure);

    public AbstractData getModelObject() {
        return modelObject;
    }
}
