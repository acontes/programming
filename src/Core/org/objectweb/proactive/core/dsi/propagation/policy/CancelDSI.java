package org.objectweb.proactive.core.dsi.propagation.policy;


/**
 * Cancel Propagation Policy
 *
 * Stop the propagation of the tag.
 */
public class CancelDSI extends AbstractPolicyDSI {

    /**
     * Propagation : Cancel propagation
     */
    @Override
    public void propagate() {
        this.tag.setValue(null);
    }

}
