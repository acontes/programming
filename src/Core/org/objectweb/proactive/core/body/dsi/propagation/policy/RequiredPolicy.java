package org.objectweb.proactive.core.body.dsi.propagation.policy;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.dsi.Tag;

/**
 * Required DSI Propagation Policy
 *
 * If no tag value setted, create one,
 * else propagation of the existing one.
 */
public class RequiredPolicy extends AbstractPolicy {

    /**
     * Propagation : Create a tag value if none, else propagation of the current value
     */
    public void propagate(Tag tag) {
        if(tag.getValue() == null){
            tag.setValue(new UniqueID());
        }
    }

}
