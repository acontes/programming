/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.sca.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.PAInterfaceImpl;
import org.objectweb.proactive.core.component.control.PABindingController;
import org.objectweb.proactive.core.component.control.PABindingControllerImpl;
import org.objectweb.proactive.extensions.sca.Utils;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.extensions.sca.gen.IntentClassGenerator;


/**
 * Extension of the {@link PABindingController} interface to take care of SCA intents.
 *
 * @author The ProActive Team
 */
public class SCAPABindingControllerImpl extends PABindingControllerImpl {
    public SCAPABindingControllerImpl(Component owner) {
        super(owner);
    }

    protected void primitiveBindFc(String clientItfName, PAInterface serverItf)
            throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException {
        super.primitiveBindFc(clientItfName, handleIntentManagement(clientItfName, serverItf));
        addIntentsToEveryMethod(clientItfName, getIntentsOfEveryMethod(clientItfName));
    }

    protected void compositeBindFc(String clientItfName, InterfaceType clientItfType, Interface serverItf)
            throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException {
        super.compositeBindFc(clientItfName, clientItfType, handleIntentManagement(clientItfName,
                ((PAInterface) serverItf)));
        addIntentsToEveryMethod(clientItfName, getIntentsOfEveryMethod(clientItfName));
    }

    /*
     * Handles the management of intents positioned on a client interface.
     *
     * @param clientItfName The client interface name.
     * @param serverItf The reference on the server interface to bind to.
     * @return An extension of the reference of the server interface which handles the management of intents.
     */
    private PAInterface handleIntentManagement(String clientItfName, PAInterface serverItf) {
        PAInterface sItf = serverItf;
        try {
            Component ownerLocal = this.getFcItfOwner();
            if (Utils.getSCAIntentController(ownerLocal).hasAtleastOneIntentHandler(clientItfName)) {
                try {
                    String sItfSignature = IntentClassGenerator.instance().generateClass(
                            sItf.getClass().getName(), sItf.getClass().getName());
                    try {
                        PAInterfaceImpl reference = (PAInterfaceImpl) Class.forName(sItfSignature)
                                .getConstructor().newInstance();
                        reference.setFcItfOwner(sItf.getFcItfOwner());
                        reference.setFcItfName(sItf.getFcItfName());
                        reference.setFcType(sItf.getFcItfType());
                        reference.setFcIsInternal(sItf.isFcInternalItf());
                        reference.setProxy(sItf.getProxy());
                        sItf = reference;
                        ((SCAIntentControllerImpl) ((PAInterface) Utils.getSCAIntentController(ownerLocal))
                                .getFcItfImpl()).notifyBinding(clientItfName, reference);
                    } catch (ClassNotFoundException cnfe) {
                        ProActiveRuntimeException pare = new ProActiveRuntimeException(
                            "Cannot access to class " + sItfSignature + ": " + cnfe.getMessage());
                        pare.initCause(cnfe);
                        throw pare;
                    } catch (NoSuchMethodException nsme) {
                        ProActiveRuntimeException pare = new ProActiveRuntimeException(
                            "Cannot access to constructor of class " + sItfSignature + ": " +
                                nsme.getMessage());
                        pare.initCause(nsme);
                        throw pare;
                    } catch (SecurityException se) {
                        ProActiveRuntimeException pare = new ProActiveRuntimeException(
                            "Cannot access to constructor of class " + sItfSignature + ": " + se.getMessage());
                        pare.initCause(se);
                        throw pare;
                    } catch (IllegalArgumentException iae) {
                        ProActiveRuntimeException pare = new ProActiveRuntimeException(
                            "Cannot invoke constructor of class " + sItfSignature + ": " + iae.getMessage());
                        pare.initCause(iae);
                        throw pare;
                    } catch (InstantiationException ie) {
                        ProActiveRuntimeException pare = new ProActiveRuntimeException(
                            "Cannot invoke constructor of class " + sItfSignature + ": " + ie.getMessage());
                        pare.initCause(ie);
                        throw pare;
                    } catch (IllegalAccessException iae) {
                        ProActiveRuntimeException pare = new ProActiveRuntimeException(
                            "Cannot invoke constructor of class " + sItfSignature + ": " + iae.getMessage());
                        pare.initCause(iae);
                        throw pare;
                    } catch (InvocationTargetException ite) {
                        ProActiveRuntimeException pare = new ProActiveRuntimeException(
                            "Cannot invoke constructor of class " + sItfSignature + ": " + ite.getMessage());
                        pare.initCause(ite);
                        throw pare;
                    }
                } catch (ClassGenerationFailedException cgfe) {
                    controllerLogger
                            .error("could not generate intent interceptor for reference (client interface) " +
                                clientItfName + ": " + cgfe.getMessage());
                }
            }
        } catch (NoSuchInterfaceException nsie) {
            // The component does not have an intent controller, nothing to do.
        }
        return sItf;
    }

    /*
     * Gets all intents of each methods before binding procedure, thus before the generation of the proxy object.
     *
     * @param clientItfName The client interface name.
     * @return All intents of each methods of the client interface.
     * @throws NoSuchInterfaceException If there is no SCAIntentController or no such client interface.
     */
    private List<List<IntentHandler>> getIntentsOfEveryMethod(String clientItfName)
            throws NoSuchInterfaceException {
        List<List<IntentHandler>> res = new ArrayList<List<IntentHandler>>();
        String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(clientItfName)
                .getFcItfSignature();
        SCAIntentController scaic = Utils.getSCAIntentController(owner);
        try {
            Method[] methodList = Class.forName(itfSignature).getDeclaredMethods();
            if (methodList.length > 0) {
                try {
                    for (int i = 0; i < methodList.length; i++) {
                        List<IntentHandler> tmp = scaic.listIntentHandler(clientItfName, methodList[i]
                                .getName());
                        res.add(tmp);
                    }
                } catch (NoSuchMethodException nsme) {
                    // Should never happen
                }
            }
        } catch (ClassNotFoundException cnfe) {
            // Should never happen
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        }
        return res;
    }

    /*
     * Adds intents that have been added before binding procedure into proxy object.
     *
     * @param clientItfName The client interface name.
     * @param listOfIntents The list of intents.
     * @throws NoSuchInterfaceException If there is no SCAIntentController or no such client interface.
     * @throws IllegalLifeCycleException If the component is not stopped.
     */
    private void addIntentsToEveryMethod(String clientItfName, List<List<IntentHandler>> listOfIntents)
            throws NoSuchInterfaceException, IllegalLifeCycleException {
        String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(clientItfName)
                .getFcItfSignature();
        SCAIntentController scaic = Utils.getSCAIntentController(owner);
        try {
            Method[] methodList = Class.forName(itfSignature).getDeclaredMethods();
            if (methodList.length > 0) {
                try {
                    for (int i = 0; i < methodList.length; i++) {
                        List<IntentHandler> tmp = listOfIntents.get(i);
                        for (IntentHandler intentHandler : tmp) {
                            scaic.addIntentHandler(intentHandler, clientItfName, methodList[i].getName());
                        }
                    }
                } catch (NoSuchMethodException nsme) {
                    // Should never happen
                }
            }
        } catch (ClassNotFoundException cnfe) {
            // Should never happen
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        }

    }

    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        super.unbindFc(clientItfName);
        ((SCAIntentControllerImpl) ((PAInterface) Utils.getSCAIntentController(owner)).getFcItfImpl())
                .notifyUnbinding(clientItfName);
    }
}
