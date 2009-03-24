package org.objectweb.proactive.core.dsi.propagation;

import java.io.Serializable;

import org.objectweb.proactive.core.dsi.Tag;

public interface PropagationPolicy extends Serializable {

    public void propagate();

    public void setTag(Tag t);

}
