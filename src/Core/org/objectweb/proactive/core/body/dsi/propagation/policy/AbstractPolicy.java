package org.objectweb.proactive.core.body.dsi.propagation.policy;

import org.objectweb.proactive.core.body.dsi.Tag;
import org.objectweb.proactive.core.body.dsi.propagation.PropagationPolicy;

/**
 * Abstract class for commons members and methods of all policy implementation
 */
public abstract class AbstractPolicy implements PropagationPolicy{


    /**
     * Propagation strategy implemented by each policy
     */
    abstract public void propagate(Tag tag);

    /**
     * Display the name of the policy used
     */
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
