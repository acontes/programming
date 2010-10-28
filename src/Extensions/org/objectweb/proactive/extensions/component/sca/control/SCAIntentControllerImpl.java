/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.component.sca.control;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.control.AbstractPAController;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactoryImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.component.sca.Constants;
import org.objectweb.proactive.extensions.component.sca.exceptions.NoSuchIntentHandlerException;


/**
 * Implementation of the {@link SCAIntentController} interface. 
 *
 * @author The ProActive Team
 * @see SCAIntentController
 */
public class SCAIntentControllerImpl extends AbstractPAController implements SCAIntentController {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_CONTROLLERS);

    //private List<IntentHandler> intentHandlers;
    /*
     * this HashMap offer the relationship between a given intenthandler and components' service Itf.
     * first string key contain the name of IntentHandler, 
     * if the value corresponds to it is null, it means 
     * this IntentHandler is applied to all service Itf.
     * else we have a list of valid service Itf.
     * each service Itf information is been put in a hashMap. 
     * the key corresponds to the Name of service Itf, 
     * if the value of list is null, then the intentHandler is applied to all
     * methods of Itf, else the list of String contain names of methods which are applied to intentHandler 
     */
    private HashMap<IntentHandler, HashMap<String, List<String>>> informationPool;

    public SCAIntentControllerImpl(Component owner) {
        super(owner);
        informationPool = new HashMap<IntentHandler, HashMap<String, List<String>>>();
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

    // we build the information pool recursively 
    public void addIntentHandler(IntentHandler intentHandler) throws IllegalLifeCycleException,
            IllegalBindingException, NoSuchInterfaceException, NoSuchMethodException {
        String[] listFc = GCM.getBindingController(owner).listFc();
        for (int i = 0; i < listFc.length; i++) {
            addIntentHandler(intentHandler, listFc[i]);
        }
    }

    public void addIntentHandler(IntentHandler intentHandler, String itfName)
            throws NoSuchInterfaceException, IllegalLifeCycleException, IllegalBindingException,
            NoSuchMethodException {
        String tmp = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).getFcItfSignature();
        Method[] methodList = null;
        try {
            methodList = Class.forName(tmp).getMethods();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new NoSuchInterfaceException(e.getMessage());
        }
        for (int i = 0; i < methodList.length; i++) {
            addIntentHandler(intentHandler, itfName, methodList[i].getName());
        }
    }

    private boolean methodExist(Method[] methodList, String methodName) {
        for (int i = 0; i < methodList.length; i++) {
            if (methodName.equals(methodList[i].getName())) {
                return true;
            }
        }
        return false;
    }

    public void addIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException, IllegalLifeCycleException,
            IllegalBindingException {
        String tmp = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).getFcItfSignature(); // can't use lookupFC before binding :'(
        Method[] methodList = null;
        try {
            methodList = Class.forName(tmp).getMethods();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new NoSuchInterfaceException(e.getMessage());
        }
        if (!methodExist(methodList, methodName)) {
            throw new NoSuchMethodException("method " + methodName + " doesn't exist!");
            //return;
        }
        HashMap<String, List<String>> itfPool;
        List<String> methods;
        if (!intentHandlerExist(intentHandler)) // intentHandler not exist
        {
            itfPool = new HashMap<String, List<String>>();
            methods = new ArrayList<String>();
            methods.add(methodName);
            itfPool.put(itfName, methods); // all methods in "itfName" interfaces
            informationPool.put(intentHandler, itfPool);
            //System.err.println("added1 "+methodName);
        } else {
            itfPool = informationPool.get(intentHandler);
            if (!itfPool.containsKey(itfName)) {
                methods = new ArrayList<String>();
                methods.add(methodName);
                itfPool.put(itfName, methods); // all methods in "itfName" interfaces
                //System.err.println("added2"+methodName);
            } else //possibility to have several same intent on one method
            {
                methods = itfPool.get(itfName);
                methods.add(methodName);
                //System.err.println("added3"+methodName);
            }
        }
    }

    private boolean intentHandlerExist(IntentHandler ithandler) {
        return informationPool.containsKey(ithandler);
    }

    public boolean hasIntentHandler() {
        return !informationPool.isEmpty();
    }

    public boolean hasIntentHandler(String ItfName) throws NoSuchInterfaceException {
        return !listIntentHandler(ItfName).isEmpty();
    }

    public boolean hasIntentHandler(String ItfName, String methodName) throws NoSuchInterfaceException,
            NoSuchMethodException {
        return !listIntentHandler(ItfName, methodName).isEmpty();
    }

    public List<IntentHandler> listIntentHandler() {
        return new ArrayList<IntentHandler>(informationPool.keySet());
    }

    public List<IntentHandler> listIntentHandler(String ItfName) throws NoSuchInterfaceException {
        List<IntentHandler> res = new ArrayList<IntentHandler>();
        for (Iterator iterator = informationPool.keySet().iterator(); iterator.hasNext();) {
            IntentHandler key = (IntentHandler) iterator.next();
            if (informationPool.get(key).containsKey(ItfName)) {
                res.add(key);
            }
        }
        return res;
    }

    //    public List<IntentHandler> listIntentHandler2(String itfName) throws NoSuchInterfaceException {
    //    	List<IntentHandler> res = new ArrayList<IntentHandler>();
    //    	String ItfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).getFcItfSignature(); // can't use lookupFC before binding :'(
    //    	Method[] methodList=null;
    //		try {
    //			methodList = Class.forName(ItfSignature).getMethods();
    //		} catch (SecurityException e) {
    //			e.printStackTrace();
    //		} catch (ClassNotFoundException e) {
    //			throw new NoSuchInterfaceException(e.getMessage());
    //		}
    //    	//if(informationPool.)
    //    }

    public List<IntentHandler> listIntentHandler(String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException {
        String ItfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName)
                .getFcItfSignature(); // can't use lookupFC before binding :'(
        Method[] methodList = null;
        try {
            methodList = Class.forName(ItfSignature).getMethods();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new NoSuchInterfaceException(e.getMessage());
        }
        if (!methodExist(methodList, methodName)) {
            throw new NoSuchMethodException("method " + methodName + " doesn't exist!");
            //return;
        }
        List<IntentHandler> res = new ArrayList<IntentHandler>();
        List<IntentHandler> itfIntenthandler = listIntentHandler(itfName);
        for (Iterator iterator = listIntentHandler(itfName).iterator(); iterator.hasNext();) {
            IntentHandler key = (IntentHandler) iterator.next();
            List<String> tmp = informationPool.get(key).get(itfName);
            for (Iterator listIter = tmp.iterator(); listIter.hasNext();) { //possibility to have several same intent on one method
                String mName = (String) listIter.next();
                if (mName.equals(methodName)) {
                    res.add(key);
                }
            }
        }
        return res;
    }

    public void removeIntentHandler(IntentHandler intentHandler) throws IllegalLifeCycleException,
            IllegalBindingException, NoSuchIntentHandlerException {
        if (intentHandlerExist(intentHandler)) {
            informationPool.remove(intentHandler);
        } else
            throw new NoSuchIntentHandlerException("intent handler doesn't exist!");
    }

    public void removeIntentHandler(IntentHandler intentHandler, String itfName)
            throws NoSuchInterfaceException, IllegalLifeCycleException, IllegalBindingException,
            NoSuchIntentHandlerException {
        if (intentHandlerExist(intentHandler)) {
            if (hasIntentHandler(itfName)) {
                informationPool.get(intentHandler).remove(itfName);
            }
        } else
            throw new NoSuchIntentHandlerException("intent handler doesn't exist!");
    }

    public void removeIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException, IllegalLifeCycleException,
            IllegalBindingException, NoSuchIntentHandlerException {
        if (intentHandlerExist(intentHandler)) {
            if (hasIntentHandler(itfName, methodName)) {
                informationPool.get(intentHandler).get(itfName).remove(methodName);
            }
        } else
            throw new NoSuchIntentHandlerException("intent handler doesn't exist!");
    }
}
