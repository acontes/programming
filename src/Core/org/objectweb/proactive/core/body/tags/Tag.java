package org.objectweb.proactive.core.body.tags;

import java.io.Serializable;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.BodyImpl;
import org.objectweb.proactive.core.config.PAProperties;

/**
 * Abstract Tag class for Request Tagging
 * 
 * You have to create a subclass implementing the abstract
 * method apply to create a tag doing what you want at each
 * propagation.
 */
public abstract class Tag implements Serializable{

    /** Identifier of the tag */
    protected String id;

    /** User Data attached to this tag */
    protected Object data;
    
    /**
     * Tag constructor
     * @param id     - Identifier of the tag
     * @param data   - User Data Content
     */
    public Tag(String id, Object data) {
        this.id = id;
        this.data = data;
    }
    
    /**
     * Tag constructor
     * @param id     - Identifier of the tag
     */
    public Tag(String id) {
        this(id, null);
    }

    /**
     * Do the current Tag jobs and then return the Tag for the next
     * propagation. It can be itself (this) or a new Tag or null to 
     * cancel the tag.
     * 
     * @return the next propagate TAG
     */
    abstract public Tag apply();

    /**
     * Return the local memory space of this Tag on the current Active Object
     * if the lease has not exceeded, null otherwise.
     * 
     * Each acces to the memory renew the lease.
     * 
     * @return the LocaLMemoryTag of this tag on the current ActiveObject if it exist
     */
    final public LocalMemoryTag getLocalMemory(){
        Body body = PAActiveObject.getBodyOnThis();
        if ( body instanceof BodyImpl) {
            return ((BodyImpl)body).getLocalMemoryTag(this.id);
        } //else
        return null;
    }

    /**
     * Create a local memory space for this Tag on the current Active Object
     * with the specified lease period if inferior to the max lease period of
     * the PAProperties.
     * @param lease - Lease Period
     * @return the LocaLMemoryTag of this tag on the current ActiveObject
     */
    final public LocalMemoryTag createLocalMemory(int lease){
        Body body = PAActiveObject.getBodyOnThis();
        if ( body instanceof BodyImpl) {
            return ((BodyImpl)body).createLocalMemoryTag(this.id, lease);
        } //else
        return null;
    }
       
    /**
     * Clear the local memory attached to this tag
     */
    final public void clearLocalMemory(){
        Body body = PAActiveObject.getBodyOnThis();
        if ( body instanceof BodyImpl) {
            ((AbstractBody)body).clearLocalMemoryTag(this.id);
        }
    }
    
    /**
     * To get the Id of this tag
     * @return Tag Id
     */
    public String getId(){
        return this.id;
    }
    
    /**
     * Return the User Data attached to this tag
     * @return - User Data
     */
    public Object getData() {
        return this.data;
    }

    /**
     * Attach a user data on this tag
     * @param data - The User Data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Display Tag Information
     */
    public String toString() {
        return "<TAG: id="+id+", data="+data+">";
    }

}