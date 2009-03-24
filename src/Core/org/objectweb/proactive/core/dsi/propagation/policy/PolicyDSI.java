package org.objectweb.proactive.core.dsi.propagation.policy;

import org.objectweb.proactive.core.dsi.Tag;
import org.objectweb.proactive.core.dsi.propagation.PropagationPolicy;

public abstract class PolicyDSI implements PropagationPolicy{

    protected Tag tag;

    abstract public void propagate();

    public void setTag(Tag t) {
        this.tag = t;
    }

}
