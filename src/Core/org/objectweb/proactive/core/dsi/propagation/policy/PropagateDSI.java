package org.objectweb.proactive.core.dsi.propagation.policy;

/**
 * Default DSI Propagation Policy
 *
 * Propagate the existing TAG if existing
 */
public class PropagateDSI extends AbstractPolicyDSI {

    /**
     * Propagation : keep current tag value
     */
    public void propagate() {
        /* Do nothing => Propagation of current value of the tag */
    }

}
