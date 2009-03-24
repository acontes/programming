package org.objectweb.proactive.core.body.dsi.propagation.policy;

import org.objectweb.proactive.core.UniqueID;

/**
 * New DSI Propagation Policy
 *
 * Create a new TAG value for each call to propagate
 */
public class NewDSI extends AbstractPolicyDSI {

    /**
     * Propagation : New tag value at each propagation
     */
    public void propagate() {
        this.tag.setValue(new UniqueID());
    }

}
