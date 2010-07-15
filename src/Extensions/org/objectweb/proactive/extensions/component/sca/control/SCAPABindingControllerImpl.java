package org.objectweb.proactive.extensions.component.sca.control;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.control.PABindingControllerImpl;
import org.objectweb.proactive.extensions.component.sca.Utils;
import org.objectweb.proactive.extensions.component.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.extensions.component.sca.gen.IntentServiceItfGenerator;


public class SCAPABindingControllerImpl extends PABindingControllerImpl {

    public SCAPABindingControllerImpl(Component owner) {
        super(owner);
    }

    protected void primitiveBindFc(String clientItfName, PAInterface serverItf)
            throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException {
        serverItf = PAFuture.getFutureValue(serverItf);
        PAInterface sItf = serverItf;
        Component owner = getFcItfOwner();
        int numberOfIntents = Utils.getSCAIntentController(owner).listFcIntentHandler(null).size();
        if (numberOfIntents > 0) {
            try {
                sItf = (PAInterface) IntentServiceItfGenerator.instance().generateClass(sItf, owner,
                        numberOfIntents);
            } catch (ClassGenerationFailedException e) {
                e.printStackTrace();
            }
        }
        //System.err.println("ready to bind " + sItf.getClass().getSimpleName());
        super.primitiveBindFc(clientItfName, sItf);
    }

}
