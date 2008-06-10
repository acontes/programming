package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


/**
 * All representation of active objects should extend this
 * class.
 * @author vasile
 *
 */
public abstract class AbstractActiveObject3D extends AbstractFigure3D {
    public AbstractActiveObject3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    /**
     * The implementation should change the 
     * appearance or do something to represent 
     * the queue (perhaps add another queue figure).
     * @param size queue size
     */
    public abstract void setQueueSize(int size);

    /**
     * Sets the appearance for migrating objects.
     */
    public abstract void setStateMigrating();

    /**
     * Sets the appearance for active objects serving requests.
     */
    public abstract void setStateServingRequest();

    /**
     * //TODO what is this used for again?
     * Sets the appearance for active objects (????)
     */
    public abstract void setStateActive();

    /**
     * Sets the appearance for active
     * objects waiting for requests.
     */
    public abstract void setStateWaitingForRequest();

    /* (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setState(org.objectweb.proactive.ic2d.jmxmonitoring.util.State)
     */
    @Override
    public void setState(State state) {
        switch (state) {
            case SERVING_REQUEST:
                setStateServingRequest();
                break;
            case MIGRATING:
                setStateMigrating();
                break;
            case ACTIVE:
                setStateActive();
                break;
            case WAITING_FOR_REQUEST:
                setStateWaitingForRequest();
                break;
            default:
                this.setStateUnkown();
                break;
        }
    }
}
