package org.objectweb.proactive.core.body.tags.propagation.policy;

import org.objectweb.proactive.core.body.tags.Tag;
import org.objectweb.proactive.core.body.tags.propagation.PropagationPolicy;

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
