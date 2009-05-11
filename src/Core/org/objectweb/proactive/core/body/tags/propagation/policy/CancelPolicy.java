package org.objectweb.proactive.core.body.tags.propagation.policy;

import org.objectweb.proactive.core.body.tags.Tag;


/**
 * Cancel Propagation Policy
 *
 * Stop the propagation of the tag.
 */
public class CancelPolicy extends AbstractPolicy {

    /**
     * Propagation : Cancel propagation
     */
    public void propagate(Tag tag) {
        tag.setValue(null);
    }

}
