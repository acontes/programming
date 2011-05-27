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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.control.AbstractPAController;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactoryImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.sca.Constants;
import org.objectweb.proactive.extensions.sca.exceptions.NoSuchIntentHandlerException;


/**
 * Implementation of the {@link SCAIntentController} interface. 
 *
 * @author The ProActive Team
 * @see SCAIntentController
 */
public class SCAIntentControllerImpl extends AbstractPAController implements SCAIntentController {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_CONTROLLERS);

    /*
     * This HashMap offers the relationship between a given IntentHandler and component's
     * interfaces. First string key contains the name of IntentHandler, if the value corresponds to
     * null, it means this IntentHandler is applied to all interfaces. Else we have a list of valid
     * interfaces. Each interface information is put in a hashMap. The key corresponds to the name
     * of the interface, if the value of list is null, then the intentHandler is applied to
     * all methods of the interface, else the list of String contains the names of the methods which
     * are applied to the IntentHandler.
     */

    private List<IntentHelper> infomationPool;

    private HashMap<String, PAInterface> mapOfServerRef;

    public SCAIntentControllerImpl(Component owner) {
        super(owner);
        infomationPool = Collections.synchronizedList(new ArrayList<SCAIntentControllerImpl.IntentHelper>());
        mapOfServerRef = new HashMap<String, PAInterface>();
    }

    @Override
    protected void setControllerItfType() {
        try {
            setItfType(PAGCMTypeFactoryImpl.instance().createFcItfType(Constants.SCA_INTENT_CONTROLLER,
                    SCAIntentController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (InstantiationException ie) {
            throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName(), ie);
        }
    }

    public void addIntentHandler(IntentHandler intentHandler) throws NoSuchInterfaceException,
            IllegalLifeCycleException {
        InterfaceType[] itftps = ((ComponentType) owner.getFcItfType()).getFcInterfaceTypes();
        for (int i = 0; i < itftps.length; i++) {
            addIntentHandler(intentHandler, itftps[i].getFcItfName());
        }
    }

    public void addIntentHandler(IntentHandler intentHandler, String itfName)
            throws NoSuchInterfaceException, IllegalLifeCycleException {
        String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName)
                .getFcItfSignature();
        try {
            Method[] methodList = Class.forName(itfSignature).getMethods();
            for (int i = 0; i < methodList.length; i++) {
                try {
                    addIntentHandler(intentHandler, itfName, methodList[i].getName());
                } catch (NoSuchMethodException nsme) {
                    // Should never happen
                }
            }
        } catch (ClassNotFoundException cnfe) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                itfSignature + ": " + cnfe.getMessage());
            pare.initCause(cnfe);
            throw pare;
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        }
    }

    public void addIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException, IllegalLifeCycleException {
        LifeCycleController lcc = GCM.getGCMLifeCycleController(owner);
        if (lcc.getFcState().equals(LifeCycleController.STARTED)) {
            throw new IllegalLifeCycleException("Component is started, cannot add an intent handler");
        }

        String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName)
                .getFcItfSignature();
        try {
            Method[] methodList = Class.forName(itfSignature).getMethods();
            if (!methodExist(methodList, methodName)) {
                throw new NoSuchMethodException("Method " + methodName + " does not exist in interface " +
                    itfSignature);
            }
            addToBaseObject(intentHandler, itfName, methodName);
        } catch (ClassNotFoundException cnfe) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                itfSignature + ": " + cnfe.getMessage());
            pare.initCause(cnfe);
            throw pare;
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        }
    }

    /*
     * Adds an intent to a method of an interface only if this interface is a service interface.
     *
     * @param intentHandler The intent handler to add.
     * @param itfName The service or reference interface name.
     * @param methodName The method name.
     * @throws NoSuchInterfaceException If the service or reference interface does not exist.
     * @throws NoSuchMethodException If the method does not exist.
     */
    private void addToBaseObject(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException {
        Object obj = owner.getReferenceOnBaseObject();
        if (((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).isFcClientItf()) { // If it is a client itf
            BindingController bc = GCM.getBindingController(owner);
            Object Itf = bc.lookupFc(itfName);
            IntentHelper ith = getIntentHelper(itfName, methodName);
            if (ith == null) {
                IntentHelper tmp = new IntentHelper(itfName, methodName);
                tmp.addIntentIntoList(intentHandler);
                infomationPool.add(tmp);
            } else { // in order to avoid ConcurrentModificationException
                IntentHelper tmp = new IntentHelper(itfName, methodName);
                tmp.intentOrderList.addAll(ith.intentOrderList);
                tmp.intentOrderList.add(intentHandler);
                infomationPool.remove(ith);
                infomationPool.add(tmp);
            }
            if (Itf != null) { // Interface already bound
                obj = mapOfServerRef.get(itfName);
            } else {
                return;
            }
        }
        String invokingName = "addIntointentArray" + methodName;
        Class<?> cla = obj.getClass();
        Method mAdd = cla.getMethod(invokingName, IntentHandler.class);
        try {
            mAdd.invoke(obj, new Object[] { intentHandler });
        } catch (IllegalArgumentException iae) {
            throw addIntentError(itfName, methodName, iae);
        } catch (IllegalAccessException iae) {
            throw addIntentError(itfName, methodName, iae);
        } catch (InvocationTargetException ite) {
            throw addIntentError(itfName, methodName, ite);
        }
    }

    /*
     * Returns an exception for errors occurring when trying to add an intent on a method of an interface.
     *
     * @param itfName The interface name.
     * @param methodName The name of the method.
     * @param e Exception which raises the error.
     * @return ProActiveRuntimeException for errors occurring when trying to add an intent on method of an
     * interface.
     */
    private ProActiveRuntimeException addIntentError(String itfName, String methodName, Exception e) {
        ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot add intent on the method " +
            methodName + " of the interface " + itfName + ": " + e.getMessage());
        pare.initCause(e);
        return pare;
    }

    public boolean hasAtleastOneIntentHandler(String itfName) {
        return itfExistingIntentHandler(itfName);
    }

    public boolean hasIntentHandler() {
        return !listIntentHandler().isEmpty();
    }

    public boolean hasIntentHandler(String itfName) throws NoSuchInterfaceException {
        return !listIntentHandler(itfName).isEmpty();
    }

    public boolean hasIntentHandler(String itfName, String methodName) throws NoSuchInterfaceException,
            NoSuchMethodException {
        return !listIntentHandler(itfName, methodName).isEmpty();
    }

    public List<IntentHandler> listIntentHandler() {
        List<IntentHandler> res = new ArrayList<IntentHandler>();
        InterfaceType[] itftps = ((ComponentType) owner.getFcItfType()).getFcInterfaceTypes();
        if (itftps.length > 0) {
            try {
                res = listIntentHandler(itftps[0].getFcItfName());
                for (int i = 1; i < itftps.length; i++) {
                    List<IntentHandler> tmp = listIntentHandler(itftps[i].getFcItfName());
                    res = intersection(tmp, res);
                }
            } catch (NoSuchInterfaceException nsie) {
                // Should never happen
            }
        }
        return res;
    }

    public List<IntentHandler> listIntentHandler(String itfName) throws NoSuchInterfaceException {
        String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName)
                .getFcItfSignature();
        try {
            List<IntentHandler> res = new ArrayList<IntentHandler>();
            Method[] methodList = Class.forName(itfSignature).getMethods();
            if (methodList.length > 0) {
                try {
                    res = listIntentHandler(itfName, methodList[0].getName());
                    for (int i = 1; i < methodList.length; i++) {
                        List<IntentHandler> tmp = listIntentHandler(itfName, methodList[i].getName());
                        res = intersection(tmp, res);
                    }
                } catch (NoSuchMethodException nsme) {
                    // Should never happen
                }
            }
            return res;
        } catch (ClassNotFoundException cnfe) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                itfSignature + ": " + cnfe.getMessage());
            pare.initCause(cnfe);
            throw pare;
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        }
    }

    /*
     * Returns a new list containing all elements that are contained in both given lists.
     *
     * @param list1 The first list.
     * @param list2 The second list.
     * @return The intersection of those two lists.
     */
    private List<IntentHandler> intersection(List<IntentHandler> list1, final List<IntentHandler> list2) {
        List<IntentHandler> result = new ArrayList<IntentHandler>();

        List<IntentHandler> smaller = list1;
        List<IntentHandler> larger = list2;
        if (list1.size() > list2.size()) {
            smaller = list2;
            larger = list1;
        }
        List<IntentHandler> tmp = new ArrayList<IntentHandler>(smaller);
        for (IntentHandler e : larger) {
            if (tmp.contains(e)) {
                result.add(e);
                tmp.remove(e);
            }
        }
        return result;
    }

    public List<IntentHandler> listIntentHandler(String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException {
        String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName)
                .getFcItfSignature();
        try {
            Method[] methodList = Class.forName(itfSignature).getMethods();
            if (!methodExist(methodList, methodName)) {
                throw new NoSuchMethodException("Method " + methodName + " does not exist in interface " +
                    itfName);
            }
            return getListFromBaseObject(itfName, methodName);
        } catch (ClassNotFoundException cnfe) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                itfSignature + ": " + cnfe.getMessage());
            pare.initCause(cnfe);
            throw pare;
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        }
    }

    @SuppressWarnings("unchecked")
    private List<IntentHandler> getListFromBaseObject(String itfName, String methodName)
            throws SecurityException, NoSuchMethodException, NoSuchInterfaceException {
        String invokingName = "listintentArray" + methodName;
        Object obj = owner.getReferenceOnBaseObject();
        if (((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).isFcClientItf()) { // Client interface
            BindingController bc = GCM.getBindingController(owner);
            Object itf = bc.lookupFc(itfName);
            List<IntentHandler> res = new ArrayList<IntentHandler>();
            IntentHelper ith = getIntentHelper(itfName, methodName);
            if (ith != null) {
                res = ith.intentOrderList;
            }
            if (itf != null) { // Interface already bound
                obj = mapOfServerRef.get(itfName);
            } else { // Interface not bound yet, store everything in local hashmap
                return res;
            }
        }
        Class<?> clazz = obj.getClass();
        Method mList = clazz.getMethod(invokingName);
        try {
            return (List<IntentHandler>) mList.invoke(obj);
        } catch (IllegalArgumentException iae) {
            throw listIntentError(itfName, methodName, iae);
        } catch (IllegalAccessException iae) {
            throw listIntentError(itfName, methodName, iae);
        } catch (InvocationTargetException ite) {
            throw listIntentError(itfName, methodName, ite);
        }
    }

    /*
     * If in an interface at least one intent is present in one of method.
     *
     * @param itfName The interface name.
     * @return The intents of an interface.
     */
    private boolean itfExistingIntentHandler(String itfName) {
        for (IntentHelper intentHelp : infomationPool) {
            if (intentHelp.itfName.equals(itfName))
                return true;
        }
        return false;
    }

    /*
     * Returns an exception for errors occurring when trying to list intents of a method of an interface.
     *
     * @param itfName The interface name.
     * @param methodName The name of the method.
     * @param e Exception which raises the error.
     * @return ProActiveRuntimeException for errors occurring when trying to list intents of a method of an
     * interface.
     */
    private ProActiveRuntimeException listIntentError(String itfName, String methodName, Exception e) {
        ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot list intents of the method " +
            methodName + " of the interface " + itfName + ": " + e.getMessage());
        pare.initCause(e);
        return pare;
    }

    public void removeIntentHandler(IntentHandler intentHandler) throws NoSuchIntentHandlerException,
            NoSuchInterfaceException, IllegalLifeCycleException {
        List<IntentHandler> tmp = listIntentHandler();
        int intentExist = listContainsIntent(intentHandler, tmp);
        if (intentExist != -1) {
            InterfaceType[] itftps = ((ComponentType) owner.getFcItfType()).getFcInterfaceTypes();
            if (itftps.length > 0) {
                for (int i = 0; i < itftps.length; i++) {
                    removeIntentHandler(intentHandler, itftps[i].getFcItfName());
                }
            }
        } else {
            throw new NoSuchIntentHandlerException("Intent handler does not exist");
        }
    }

    public void removeIntentHandler(IntentHandler intentHandler, String itfName)
            throws NoSuchIntentHandlerException, NoSuchInterfaceException, IllegalLifeCycleException {
        List<IntentHandler> tmp = listIntentHandler(itfName);
        if (listContainsIntent(intentHandler, tmp) != -1) {
            String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName)
                    .getFcItfSignature();
            try {
                Method[] methodList = Class.forName(itfSignature).getMethods();
                if (methodList.length > 0) {
                    for (int i = 0; i < methodList.length; i++) {
                        try {
                            removeIntentHandler(intentHandler, itfName, methodList[i].getName());
                        } catch (NoSuchMethodException nsme) {
                            // Should never happen
                        }
                    }
                }
            } catch (ClassNotFoundException cnfe) {
                ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                    itfSignature + ": " + cnfe.getMessage());
                pare.initCause(cnfe);
                throw pare;
            } catch (SecurityException se) {
                ProActiveRuntimeException pare = new ProActiveRuntimeException(
                    "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
                pare.initCause(se);
                throw pare;
            }
        } else {
            throw new NoSuchIntentHandlerException("Intent handler does not exist");
        }
    }

    public void removeIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException, IllegalLifeCycleException,
            NoSuchIntentHandlerException {
        LifeCycleController lcc = GCM.getGCMLifeCycleController(owner);
        if (lcc.getFcState().equals(LifeCycleController.STARTED)) {
            throw new IllegalLifeCycleException("Component is started, cannot remove an intent handler");
        }

        String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName)
                .getFcItfSignature();
        List<IntentHandler> tmp = listIntentHandler(itfName, methodName);
        if (listContainsIntent(intentHandler, tmp) != -1) {
            try {
                Method[] methodList = Class.forName(itfSignature).getMethods();
                if (!methodExist(methodList, methodName)) {
                    throw new NoSuchMethodException("Method " + methodName + " does not exist in interface " +
                        itfName);
                } else {
                    removeFromBaseObject(itfName, methodName, intentHandler);
                }
            } catch (ClassNotFoundException cnfe) {
                ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                    itfSignature + ": " + cnfe.getMessage());
                pare.initCause(cnfe);
                throw pare;
            } catch (SecurityException se) {
                ProActiveRuntimeException pare = new ProActiveRuntimeException(
                    "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
                pare.initCause(se);
                throw pare;
            }
        } else {
            throw new NoSuchIntentHandlerException("Intent handler does not exist");
        }
    }

    /*
     * Returns the associated index of the given intent handler in the given list of intent handlers or -1 if not
     * exist.
     *
     * @param intentHandler The intent handler.
     * @param list The list of intent handlers.
     * @return The associated index of the given intent handler in the given list of intent handlers or -1 if not
     * exist.
     */
    private int listContainsIntent(IntentHandler intentHandler, List<IntentHandler> list) {
        int index = 0;
        for (IntentHandler ih : list) {
            if (intentHandler.getClass().getName().equals(ih.getClass().getName())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /*
     * Checks if the given method is present into the given array of methods.
     *
     * @param methods The array of methods.
     * @param methodName The name of the method.
     * @return True if the given method is present into the given array of methods, false otherwise.
     */
    private boolean methodExist(Method[] methods, String methodName) {
        for (int i = 0; i < methods.length; i++) {
            if (methodName.equals(methods[i].getName())) {
                return true;
            }
        }
        return false;
    }

    private void removeFromBaseObject(String itfName, String methodName, IntentHandler intentHandler)
            throws SecurityException, NoSuchMethodException, NoSuchInterfaceException {
        String invokingName = "removeFromintentArray" + methodName;
        Object obj = owner.getReferenceOnBaseObject();
        Class<?> clazz = obj.getClass();
        if (((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).isFcClientItf()) {
            BindingController bc = GCM.getBindingController(owner);
            Object Itf = bc.lookupFc(itfName);
            IntentHelper ith = getIntentHelper(itfName, methodName);
            if (ith != null) {
                ith.intentOrderList.remove(intentHandler);
                if (ith.intentOrderList.isEmpty()) {
                    infomationPool.remove(ith);
                }
            }
            if (Itf != null) { // Interface already bound
                obj = mapOfServerRef.get(itfName);
            } else { // Interface not bound yet, store everything in local hashmap
                return;
            }
        }
        Method mRemove = clazz.getMethod(invokingName, IntentHandler.class);
        try {
            mRemove.invoke(obj, new Object[] { intentHandler });
        } catch (IllegalArgumentException iae) {
            throw removeIntentError(itfName, methodName, iae);
        } catch (IllegalAccessException iae) {
            throw removeIntentError(itfName, methodName, iae);
        } catch (InvocationTargetException ite) {
            throw removeIntentError(itfName, methodName, ite);
        }
    }

    /*
     * Returns an exception for errors occurring when trying to remove an intent on a method of an interface.
     *
     * @param itfName The interface name.
     * @param methodName The name of the method.
     * @param e Exception which raises the error.
     * @return ProActiveRuntimeException for errors occurring when trying to remove an intent on a method of an
     * interface.
     */
    private ProActiveRuntimeException removeIntentError(String itfName, String methodName, Exception e) {
        ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot remove intent on the method " +
            methodName + " of the interface " + itfName + ": " + e.getMessage());
        pare.initCause(e);
        return pare;
    }

    private IntentHelper getIntentHelper(String itfName, String methodName) {
        for (Iterator<IntentHelper> iterator = infomationPool.iterator(); iterator.hasNext();) {
            IntentHelper ith = iterator.next();
            if (ith.idExist(itfName, methodName)) {
                return ith;
            }
        }
        return null;
    }

    /**
     * Notifies that a binding has been performed on a client interface.
     *
     * @param clientItfName The client interface name.
     * @param sItf The server interface which the client interface has been bound to.
     */
    public void notifyBinding(String clientItfName, PAInterface sItf) {
        mapOfServerRef.put(clientItfName, sItf);
    }

    /**
     * Notifies that a binding has been removed on a client interface.
     *
     * @param clientItfName The client interface name.
     */
    public void notifyUnbinding(String clientItfName) {
        mapOfServerRef.remove(clientItfName);
    }

    /**
     * This class helps to manipulate the presence in an interface and methods.
     */
    private class IntentHelper {
        public String itfName;
        public String methodName;
        public String id;
        public List<IntentHandler> intentOrderList;

        public IntentHelper(String itfName, String methodName) {
            this.itfName = itfName;
            this.methodName = methodName;
            this.id = itfName + "@" + methodName;
            intentOrderList = new ArrayList<IntentHandler>();
        }

        public boolean idExist(String itfName, String methodName) {
            return this.id.equals(itfName + "@" + methodName);
        }

        public void addIntentIntoList(IntentHandler ith) {
            intentOrderList.add(ith);
        }
    }
}
