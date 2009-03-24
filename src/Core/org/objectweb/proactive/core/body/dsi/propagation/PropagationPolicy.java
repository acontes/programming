package org.objectweb.proactive.core.body.dsi.propagation;

import java.io.Serializable;

import org.objectweb.proactive.core.body.dsi.Tag;

/**
 * Interface to set the behavior of propagation policy
 */
public interface PropagationPolicy extends Serializable {

    /**
     * Propagation of the tag
     */
    public void propagate();

    /**
     * Set the tag attach to this policy
     * @param t - The Tag
     */
    public void setTag(Tag t);

}
