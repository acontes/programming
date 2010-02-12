package org.objectweb.proactive.extensions.webservices.cxf.initialization;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.controller.PAGCMLifeCycleControllerImpl;


public class CXFProActiveLifeCycleControllerImpl extends PAGCMLifeCycleControllerImpl {

    public CXFProActiveLifeCycleControllerImpl(Component owner) {
        super(owner);
    }

    public void startFc() throws IllegalLifeCycleException {
        CXFInitializer.init();
        super.startFc();
    }

}
