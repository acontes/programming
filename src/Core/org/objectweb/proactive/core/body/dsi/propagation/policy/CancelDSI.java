package org.objectweb.proactive.core.body.dsi.propagation.policy;


/**
 * Cancel Propagation Policy
 *
 * Stop the propagation of the tag.
 */
public class CancelDSI extends AbstractPolicyDSI {

    /**
     * Propagation : Cancel propagation
     */
    public void propagate() {
        this.tag.setValue(null);
    }

}
