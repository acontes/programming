package org.objectweb.proactive.extensions.component.sca.control;

import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.control.PABindingControllerImpl;
import org.objectweb.proactive.extensions.component.sca.Utils;


public class SCAPABindingControllerImpl extends PABindingControllerImpl {

    public SCAPABindingControllerImpl(Component owner) {
        super(owner);
    }

    @Override
    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        super.bindFc(clientItfName, serverItf);
        PAInterface sItf = null;
        SCAIntentController scaic = Utils.getSCAIntentController(getFcItfOwner());
        List<IntentHandler> intentHandlers = scaic.listFcIntentHandler(null);
        System.err.println("BIND");
        if (serverItf instanceof PAInterface) {
            sItf = (PAInterface) serverItf;
            System.err.println("is instance!!!");
        }
        System.err.println("service : " +
            getFcItfOwner().getFcInterface(sItf.getFcItfName()).getClass().getName());
        String fItfName = ((InterfaceType) ((Interface) sItf).getFcItfType()).getFcItfSignature();//.cast(getFcItfOwner().getFcInterface(sItf.getFcItfName()));
        Class<?> fItf = null;
        try {
            fItf = Class.forName(fItfName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        java.lang.reflect.Method[] meds = serverItf.getClass().getMethods();
        for (int i = 0; i < meds.length; i++) {
            Class<?>[] paramType = meds[i].getParameterTypes();
            String params = "(";
            for (int j = 0; j < paramType.length; j++) {
                params += paramType[j].getName() + " " + paramType[j].getSimpleName() + Integer.toString(j) +
                    " ";
            }
            params += ")\n";
            System.err.println("public " + meds[i].getReturnType().getName() + " " + meds[i].getName() +
                params);
        }

        //System.err.println(serverName);
    }

}
