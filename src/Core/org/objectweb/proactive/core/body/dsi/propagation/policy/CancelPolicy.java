package org.objectweb.proactive.core.body.dsi.propagation.policy;

import org.objectweb.proactive.core.body.dsi.Tag;


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
