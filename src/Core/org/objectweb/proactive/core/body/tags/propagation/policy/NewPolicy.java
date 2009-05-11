package org.objectweb.proactive.core.body.tags.propagation.policy;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.tags.Tag;

/**
 * New DSI Propagation Policy
 *
 * Create a new TAG value for each call to propagate
 */
public class NewPolicy extends AbstractPolicy {

    /**
     * Propagation : New tag value at each propagation
     */
    public void propagate(Tag tag) {
        tag.setValue(new UniqueID());
    }

}
