package org.objectweb.proactive.core.component.adl.components;

import org.objectweb.fractal.adl.components.Component;

public interface PAComponent extends Component {
    String getDomain ();
    void setDomain (String arg);
}
