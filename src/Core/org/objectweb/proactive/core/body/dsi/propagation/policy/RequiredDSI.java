package org.objectweb.proactive.core.body.dsi.propagation.policy;

import org.objectweb.proactive.core.UniqueID;

/**
 * Required DSI Propagation Policy
 *
 * If no tag value setted, create one,
 * else propagation of the existing one.
 */
public class RequiredDSI extends AbstractPolicyDSI {

    /**
     * Propagation : Create a tag value if none, else propagation of the current value
     */
    public void propagate() {
        if(this.tag.getValue() == null){
            this.tag.setValue(new UniqueID());
        }
    }

}
