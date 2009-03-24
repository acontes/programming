package org.objectweb.proactive.core.dsi.propagation.policy;

import org.objectweb.proactive.core.dsi.Tag;
import org.objectweb.proactive.core.dsi.propagation.PropagationPolicy;

/**
 * Abstract class for commons members and methods of all policy implementation
 */
public abstract class AbstractPolicyDSI implements PropagationPolicy{

    /** Tag attached to this policy */
    protected Tag tag;

    /**
     * Propagation strategy implemented by each policy
     */
    abstract public void propagate();

    /**
     * Set the tag using this policy
     */
    public void setTag(Tag t) {
        this.tag = t;
    }

    /**
     * Display the name of the policy used
     */
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
