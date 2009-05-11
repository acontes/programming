package org.objectweb.proactive.core.body.tags.propagation;

import java.io.Serializable;

import org.objectweb.proactive.core.body.tags.Tag;

/**
 * Interface to set the behavior of propagation policy
 */
public interface PropagationPolicy extends Serializable {

    /**
     * Propagation of the tag
     */
    public void propagate(Tag tag);

}
