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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
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
    private HashMap<IntentHandler, HashMap<String, List<String>>> informationPool;
    
    private HashMap<String,PAInterface> mapOfServerRef;

    public SCAIntentControllerImpl(Component owner) {
        super(owner);
        informationPool = new HashMap<IntentHandler, HashMap<String, List<String>>>();
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

    public void addIntentHandler(IntentHandler intentHandler) throws NoSuchInterfaceException {
        InterfaceType[] itftps = ((ComponentType) owner.getFcItfType()).getFcInterfaceTypes();
        for (int i = 0; i < itftps.length; i++) {
            addIntentHandler(intentHandler, itftps[i].getFcItfName());
        }
    }

    public void addIntentHandler(IntentHandler intentHandler, String itfName)
            throws NoSuchInterfaceException{
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
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        } catch (ClassNotFoundException cnfe) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                itfSignature + ": " + cnfe.getMessage());
            pare.initCause(cnfe);
            throw pare;
        }
    }

    public void addIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException {
        String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName)
                .getFcItfSignature();
        try {
            Method[] methodList = Class.forName(itfSignature).getMethods();
            if (!methodExist(methodList, methodName)) {
                throw new NoSuchMethodException("Method " + methodName + " does not exist in interface " +
                    itfSignature);
            }
            addToBaseObject(intentHandler, itfName, methodName);
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        } catch (ClassNotFoundException cnfe) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                itfSignature + ": " + cnfe.getMessage());
            pare.initCause(cnfe);
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
        if (((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).isFcClientItf()) { // if it is a client itf
        	BindingController bctr = GCM.getBindingController(owner);
			Object Itf = bctr.lookupFc(itfName);
			HashMap<String, List<String>> itfPool;
            List<String> methods;
            if (!informationPool.containsKey(intentHandler)) // Intent handler does not exist
            {
                itfPool = new HashMap<String, List<String>>();
                methods = new ArrayList<String>();
                methods.add(methodName);
                itfPool.put(itfName, methods);
                informationPool.put(intentHandler, itfPool);;
            } else {
            	itfPool = informationPool.get(intentHandler);
                if ((!itfPool.containsKey(itfName)) || itfPool == null) {
                    methods = new ArrayList<String>();
                    methods.add(methodName);
                    itfPool.put(itfName, methods);
                } else // Possibility to have several same intent on one method
                {
                    methods = itfPool.get(itfName);
                    methods.add(methodName);
                }
            }
			if (Itf != null) { // interface already binded
				obj=mapOfServerRef.get(itfName);
			}	
			else
			{
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
     * Returns an exception for errors occurring when trying to add an intent on a method of a service interface.
     *
     * @param itfName The service interface name.
     * @param methodName The name of the method.
     * @param e Exception which raises the error.
     * @return ProActiveRuntimeException for errors occurring when trying to add an intent on method of service
     * interface.
     */
    private ProActiveRuntimeException addIntentError(String itfName, String methodName, Exception e) {
        ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot add intent on the method " +
            methodName + " of the interface " + itfName + ": " + e.getMessage());
        pare.initCause(e);
        return pare;
    }

    public boolean hasAtleastOneIntentHandler(String itfName) {
        return !listExistingIntentHandlerOfItf(itfName).isEmpty();
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

    public List<IntentHandler> listAllIntentHandler() {
        return new ArrayList<IntentHandler>(informationPool.keySet());//listOfIntent; 
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
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        } catch (ClassNotFoundException cnfe) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                itfSignature + ": " + cnfe.getMessage());
            pare.initCause(cnfe);
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
        } catch (SecurityException se) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException(
                "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
            pare.initCause(se);
            throw pare;
        } catch (ClassNotFoundException cnfe) {
            ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                itfSignature + ": " + cnfe.getMessage());
            pare.initCause(cnfe);
            throw pare;
        }
    }

    private List<IntentHandler> getListFromBaseObject(String itfName, String methodName)
	throws SecurityException, NoSuchMethodException, NoSuchInterfaceException 
	{
		String invokingName = "listintentArray"+methodName;
		Object obj = owner.getReferenceOnBaseObject();
		if(((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).isFcClientItf()){ // client interface
			BindingController bctr = GCM.getBindingController(owner);
			Object Itf = bctr.lookupFc(itfName);
			List<IntentHandler> res = new ArrayList<IntentHandler>();
            for (Iterator<IntentHandler> iterator = listExistingIntentHandlerOfItf(itfName).iterator(); iterator
                    .hasNext();) {
                IntentHandler key = iterator.next();
                List<String> tmp = informationPool.get(key).get(itfName);
                for (Iterator<String> listIter = tmp.iterator(); listIter.hasNext();) { // Possibility to have several same intent on one method
                    String mName = listIter.next();
                    if (mName.equals(methodName)) {
                        res.add(key);
                    }
                }
            }
			if (Itf != null) { // interface already binded
				obj=mapOfServerRef.get(itfName);
			}
			else // interface not binding yet, store everything in local hashmap
			{
	            return res;
			}
		}
		Class cla  = obj.getClass();
		Method mList = cla.getMethod(invokingName);
		List res = null;
		try {
			res = (List) mList.invoke(obj);
		} catch (Exception e) {
			System.err.println("problem on invoking add intent into base object");
			e.printStackTrace();
		} 
		return res;
	}
    
    /*
     * Lists the intents of an interface.
     *
     * @param itfName The interface name.
     * @return The intents of an interface.
     */
    private List<IntentHandler> listExistingIntentHandlerOfItf(String itfName) {
        List<IntentHandler> res = new ArrayList<IntentHandler>();
        for (Iterator<IntentHandler> iterator = informationPool.keySet().iterator(); iterator.hasNext();) {
        	IntentHandler key = iterator.next();
            if (informationPool.get(key).containsKey(itfName)) {
            	res.add(key);
            }
        }
        return res;
    }

    public void removeIntentHandler(IntentHandler intentHandler) throws NoSuchIntentHandlerException,
            NoSuchInterfaceException {
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
            throws NoSuchIntentHandlerException, NoSuchInterfaceException {
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
            } catch (SecurityException se) {
                ProActiveRuntimeException pare = new ProActiveRuntimeException(
                    "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
                pare.initCause(se);
                throw pare;
            } catch (ClassNotFoundException cnfe) {
                ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                    itfSignature + ": " + cnfe.getMessage());
                pare.initCause(cnfe);
                throw pare;
            }
        } else {
            throw new NoSuchIntentHandlerException("Intent handler does not exist");
        }
    }

    public void removeIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException,
            NoSuchIntentHandlerException {
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
            } catch (SecurityException se) {
                ProActiveRuntimeException pare = new ProActiveRuntimeException(
                    "Cannot access to methods of interface " + itfSignature + ": " + se.getMessage());
                pare.initCause(se);
                throw pare;
            } catch (ClassNotFoundException cnfe) {
                ProActiveRuntimeException pare = new ProActiveRuntimeException("Cannot access to interface " +
                    itfSignature + ": " + cnfe.getMessage());
                pare.initCause(cnfe);
                throw pare;
            }
        } else {
            throw new NoSuchIntentHandlerException("Intent handler does not exist");
        }
    }
    
    private void removeFromBaseObject(String itfName,String methodName, IntentHandler intentHandler)throws SecurityException, NoSuchMethodException, NoSuchInterfaceException 
	{
		String invokingName = "removeFromintentArray"+methodName;
		Object obj = owner.getReferenceOnBaseObject();
		Class cla  = obj.getClass();
		if(((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).isFcClientItf()){
			BindingController bctr = GCM.getBindingController(owner);
			Object Itf = bctr.lookupFc(itfName);
            IntentHandler intentIndex = intentHandler;
			informationPool.get(intentIndex).get(itfName).remove(methodName);
            if (informationPool.get(intentIndex).get(itfName).isEmpty()) {
                informationPool.get(intentIndex).remove(itfName);
                if (informationPool.get(intentIndex).isEmpty()) {
                    informationPool.remove(intentIndex);
                }
            }
			if (Itf != null) { // interface already binded
				obj=mapOfServerRef.get(itfName);
			}
			else // interface not binding yet, store everything in local hashmap
			{
                return;
			}
		}
		Method mRemove = cla.getMethod(invokingName, IntentHandler.class);
		try {
			mRemove.invoke(obj, new Object[]{intentHandler});
		} catch (Exception e) {
			System.err.println("problem on invoking remove intent into base object");
			e.printStackTrace();
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
            // if(intentHandler.ID == it.ID)
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

	public void addServerReference(String clientItfName, PAInterface sItf) {
		mapOfServerRef.put(clientItfName, sItf);
	}
	
	public void removeServerReference(String clientItfName) {
		mapOfServerRef.remove(clientItfName);
	}
	
}
