package org.objectweb.proactive.extensions.sca.control;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.control.PAGCMLifeCycleControllerImpl;


public class SCALifeCycleControllerImpl extends PAGCMLifeCycleControllerImpl {

    public SCALifeCycleControllerImpl(Component owner) {
        super(owner);
    }

    public void startFc() throws IllegalLifeCycleException {
        super.startFc();
    }

}
