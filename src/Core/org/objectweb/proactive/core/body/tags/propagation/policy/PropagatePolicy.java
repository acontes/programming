package org.objectweb.proactive.core.body.tags.propagation.policy;

import org.objectweb.proactive.core.body.tags.Tag;

/**
 * Default DSI Propagation Policy
 *
 * Propagate the existing TAG if existing
 */
public class PropagatePolicy extends AbstractPolicy {

    /**
     * Propagation : keep current tag value
     */
    public void propagate(Tag tag) {
        /* Do nothing => Propagation of current value of the tag */
    }

}
