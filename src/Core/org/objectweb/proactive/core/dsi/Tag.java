package org.objectweb.proactive.core.dsi;

import java.io.Serializable;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.dsi.propagation.PropagationPolicy;
import org.objectweb.proactive.core.dsi.propagation.policy.PropagateDSI;

/**
 * Tag for Request Tagging
 */
public class Tag implements Serializable{

    /** Value of the tag */
    protected UniqueID value;

    /** Propagation policy of the value of this tag */
    protected PropagationPolicy policy;

    /** User Data attached to this tag */
    protected Object data;


    /**
     * Tag constructor
     * @param value  - Value of the tag
     * @param policy - Propagation policy
     * @param data   - User Data Content
     */
    public Tag(UniqueID value, PropagationPolicy policy, Object data) {
        this.value = value;
        this.policy = policy;
        this.data = data;
        policy.setTag(this);
    }

    /**
     * Tag constructor with default propagation policy
     * and a user data content.
     * @param value  - Tag Value
     * @param data   - User Data Content
     */
    public Tag(UniqueID value, Object data){
        this(value, new PropagateDSI(), data);
    }

    /**
     * Tag constructor with default policy
     * @param value - Tag Value
     */
    public Tag(UniqueID value){
        this(value, null);
    }

    /**
     * Set a propagation policy to this tag
     * @param policy - a Propagation policy
     */
    public void setPolicy(PropagationPolicy policy) {
        this.policy = policy;
    }

    /**
     * Return the User Data attached to this tag
     * @return - User Data
     */
    public Object getData() {
        return data;
    }

    /**
     * Attach a user data on this tag
     * @param data - The User Data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Propagation of the value of this tag depending on the policy setted.
     */
    public void propagate(){
        this.policy.propagate();
    }

    /**
     * Display Tag Information
     */
    public String toString() {
        return "<TAG: value="+value+", policy="+policy+", data="+data+">";
    }

    /**
     * Return the Value of this TAG
     * @return UniqueID
     */
    public UniqueID getValue() {
        return value;
    }

    /**
     * Set the value of the Tag
     * @param value - new tag value
     */
    public void setValue(UniqueID value) {
        this.value = value;
    }

}
