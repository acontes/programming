package org.objectweb.proactive.core.dsi.propagation.policy;

import org.objectweb.proactive.core.dsi.Tag;
import org.objectweb.proactive.core.dsi.propagation.PropagationPolicy;

public class CancelDSI implements PropagationPolicy {

    @Override
    public void propagate() {
        /* do nothing, cancel the propagation of the current tag */
    }

    @Override
    public void setTag(Tag t) {
        // TODO Auto-generated method stub

    }

}
