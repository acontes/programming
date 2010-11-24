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
package org.objectweb.proactive.extensions.sca.control;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.CollectionUtils;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveRuntimeException;
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
    //*private HashMap<IntentHandler, HashMap<String, List<String>>> informationPool;
    private HashMap<Integer, HashMap<String, List<String>>> informationPool;
    private List<IntentHandler> listOfIntent;

    public SCAIntentControllerImpl(Component owner) {
        super(owner);
        //informationPool = new HashMap<IntentHandler, HashMap<String, List<String>>>();
        informationPool = new HashMap<Integer, HashMap<String, List<String>>>();
        listOfIntent = new ArrayList<IntentHandler>();
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
    
    /**
     * get associated index of a given IntentHandler in listOfIntent. -1 if not exist
     * @param i
     * @return
     */
    private int indexAssociatedWithIntent(IntentHandler i)
    {
    	return ListContainsIntent(i, listOfIntent);
    }
    
    /**
     * get the intentHandler associated with index
     * @param index
     * @return
     */
    private IntentHandler intentAssociatedWithIndex(int index)
    {
    	return listOfIntent.get(index);
    }
    
    /**
     * get associated index of a given IntentHandler in a given list. -1 if not exist
     * @param i
     * @param list
     * @return
     */
    private int ListContainsIntent(IntentHandler i, List<IntentHandler> list)
    {
    	int index = 0;
    	for (IntentHandler it : list) {
			if(i.ID == it.ID)
			{
				return index;
			}
			index++;
		}
    	return -1;
    }
    
    private boolean methodExist(Method[] methodList, String methodName) {
        for (int i = 0; i < methodList.length; i++) {
            if (methodName.equals(methodList[i].getName())) {
                return true;
            }
        }
        return false;
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


    public void addIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException, IllegalLifeCycleException, IllegalBindingException
    {
    	LifeCycleController lcCtr = GCM.getGCMLifeCycleController(owner);
    	if(lcCtr.getFcState().equals(lcCtr.STARTED))
    	{
    		throw new IllegalLifeCycleException("component already started, impossible to add an Intent Handler.");
    	}
    	Object Itf = GCM.getBindingController(owner).lookupFc(itfName);
    	if(Itf != null)
    	{
    		throw new IllegalBindingException("binding already done for the Interface!");
    	}	
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
        }
        HashMap<String, List<String>> itfPool;
        List<String> methods;
        int intentIndex = indexAssociatedWithIntent(intentHandler);
        if (!informationPool.containsKey(intentIndex)) // intentHandler not exist
        //*if (!informationPool.containsKey(intentHandler)) // intentHandler not exist
        {
            itfPool = new HashMap<String, List<String>>();
            methods = new ArrayList<String>();
            methods.add(methodName);
            itfPool.put(itfName, methods); // all methods in "itfName" interfaces
            
            informationPool.put(listOfIntent.size(), itfPool);
            listOfIntent.add(intentHandler);
            //*informationPool.put(intentHandler, itfPool);
        } else {
        	itfPool = informationPool.get(intentIndex);
            //*itfPool = informationPool.get(intentHandler);
            if ((!itfPool.containsKey(itfName))||itfPool==null) {
                methods = new ArrayList<String>();
                methods.add(methodName);
                itfPool.put(itfName, methods); // all methods in "itfName" interfaces
            } else //possibility to have several same intent on one method
            {
                methods = itfPool.get(itfName);
                methods.add(methodName);
            }
        }
    }
    

    public boolean hasIntentHandler() throws NoSuchInterfaceException, NoSuchMethodException {
        return !listIntentHandler().isEmpty();
    }

    public boolean hasIntentHandler(String ItfName) throws NoSuchInterfaceException, NoSuchMethodException {
        return !listIntentHandler(ItfName).isEmpty();
    }

    public boolean hasIntentHandler(String ItfName, String methodName) throws NoSuchInterfaceException,
            NoSuchMethodException {
        return !listIntentHandler(ItfName, methodName).isEmpty();
    }

    

    /**
     * Returns a new list containing all elements that are contained in
     * both given lists. code copied from org.apache.commons.collections.ListUtils.
     *
     * @param list1  the first list
     * @param list2  the second list
     * @return  the intersection of those two lists
     * @throws NullPointerException if either list is null
     */
    public static <E> List<E> intersection(final List<? extends E> list1, final List<? extends E> list2) {
        final List<E> result = new ArrayList<E>();

        List<? extends E> smaller = list1;
        List<? extends E> larger = list2;
        if (list1.size() > list2.size()) {
            smaller = list2;
            larger = list1;
        }
        
        //HashSet<E> hashSet = new HashSet<E>(smaller);
        List<? extends E> tmp = new ArrayList<E>(smaller);
        for (E e : larger) {
        	if(tmp.contains(e))
        	{
        		result.add(e);
        		tmp.remove(e);
        	}
        }
        return result;
    }
    
    public List<IntentHandler> listIntentHandler() throws NoSuchInterfaceException, NoSuchMethodException
    {
    	List<IntentHandler> res = new ArrayList<IntentHandler>();
    	String[] listFc = GCM.getBindingController(owner).listFc();
    	if(listFc.length > 0) // at least on interface
    	{
    		res = listIntentHandler(listFc[0]);
	        for (int i = 1; i < listFc.length; i++) {
	        	List<IntentHandler> tmp = listIntentHandler(listFc[i]);
	        	res = intersection(tmp, res);
	        }
    	}
        return res;
    }
    
    public List<IntentHandler> listIntentHandler(String itfName) throws NoSuchInterfaceException, NoSuchMethodException {
    	List<IntentHandler> res = new ArrayList<IntentHandler>();
    	String ItfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).getFcItfSignature(); // can't use lookupFC before binding :'(
    	Method[] methodList=null;
		try {
			methodList = Class.forName(ItfSignature).getMethods();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			throw new NoSuchInterfaceException(e.getMessage());
		}
		if(methodList.length > 0) // at least one element
		{
			res = listIntentHandler(itfName,methodList[0].getName());
			for (int i = 1; i < methodList.length; i++) {
				List<IntentHandler> tmp = listIntentHandler(itfName,methodList[i].getName());
				res = intersection(tmp, res);
			}
		}
		return res;	
    }

    
    private List<IntentHandler> listExistingIntentHandler(String ItfName) {
        List<IntentHandler> res = new ArrayList<IntentHandler>();
        for (Iterator iterator = informationPool.keySet().iterator(); iterator.hasNext();) {
        	int key = (Integer) iterator.next();
            //*IntentHandler key = (IntentHandler) iterator.next();
            if (informationPool.get(key).containsKey(ItfName)) {
            	IntentHandler tmp = intentAssociatedWithIndex(key);
            	res.add(tmp);
                //*res.add(key);
            }
        }
        return res;
    }
    
    public List<IntentHandler> listExistingIntentHandler() {
    	//*return new ArrayList<IntentHandler>(informationPool.keySet());
    	return listOfIntent;
    }
    
    public boolean intentHandlerExists(String ItfName)
    {
    	return !listExistingIntentHandler(ItfName).isEmpty();
    }
    
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
        }
        List<IntentHandler> res = new ArrayList<IntentHandler>();
        for (Iterator iterator = listExistingIntentHandler(itfName).iterator(); iterator.hasNext();) {
            IntentHandler key = (IntentHandler) iterator.next();
            int indexKey = indexAssociatedWithIntent(key);
            List<String> tmp = informationPool.get(indexKey).get(itfName);
            //*List<String> tmp = informationPool.get(key).get(itfName);
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
            IllegalBindingException, NoSuchIntentHandlerException, NoSuchInterfaceException, NoSuchMethodException {
    	LifeCycleController lcCtr = GCM.getGCMLifeCycleController(owner);
    	if(lcCtr.getFcState().equals(lcCtr.STARTED))
    	{
    		throw new IllegalLifeCycleException("component already started, impossible to add an Intent Handler.");
    	}
    	List<IntentHandler> tmp = listIntentHandler();
    	int intentExist = ListContainsIntent(intentHandler, tmp);
    	if(intentExist != -1)
    	//*if(tmp.contains(intentHandler))
    	{
    		String[] listFc = GCM.getBindingController(owner).listFc();
        	if(listFc.length > 0) // at least on interface
        	{
        		for (int i = 0; i < listFc.length; i++) {
        			removeIntentHandler(intentHandler, listFc[i]);
        		}
        	}
    	}
    	else
            throw new NoSuchIntentHandlerException("intent handler doesn't exist!");
    }

    public void removeIntentHandler(IntentHandler intentHandler, String itfName)
            throws NoSuchInterfaceException, IllegalLifeCycleException, IllegalBindingException,
            NoSuchIntentHandlerException, NoSuchMethodException {
    	LifeCycleController lcCtr = GCM.getGCMLifeCycleController(owner);
    	if(lcCtr.getFcState().equals(lcCtr.STARTED))
    	{
    		throw new IllegalLifeCycleException("component already started, impossible to add an Intent Handler.");
    	}
    	List<IntentHandler> tmp = listIntentHandler(itfName);
    	System.err.println(tmp.size());
    	String ItfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).getFcItfSignature(); // can't use lookupFC before binding :'(
    	Method[] methodList=null;
    	int intentExist = ListContainsIntent(intentHandler, tmp);
    	if(intentExist != -1)
    	//*if(tmp.contains(intentHandler))
    	{
			try {
				methodList = Class.forName(ItfSignature).getMethods();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				throw new NoSuchInterfaceException(e.getMessage());
			}
			if(methodList.length > 0) // at least one element
			{
				for (int i = 0; i < methodList.length; i++) {
					removeIntentHandler(intentHandler, itfName, methodList[i].getName());
				}
			}
    	}
    	else
            throw new NoSuchIntentHandlerException("intent handler doesn't exist!");
    }

    public void removeIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException, IllegalLifeCycleException,
            IllegalBindingException, NoSuchIntentHandlerException {
    	LifeCycleController lcCtr = GCM.getGCMLifeCycleController(owner);
    	if(lcCtr.getFcState().equals(lcCtr.STARTED))
    	{
    		throw new IllegalLifeCycleException("component already started, impossible to add an Intent Handler.");
    	}
    	String ItfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(itfName).getFcItfSignature(); // can't use lookupFC before binding :'(
    	List<IntentHandler> tmp = listIntentHandler(itfName,methodName);
    	int intentExist = ListContainsIntent(intentHandler, tmp);
    	if(intentExist != -1)
    	//*if(tmp.contains(intentHandler))
    	{
    		Method[] methodList=null;
    		try {
				methodList = Class.forName(ItfSignature).getMethods();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				throw new NoSuchInterfaceException(e.getMessage());
			}
			if (!methodExist(methodList, methodName)) {
	            throw new NoSuchMethodException("method " + methodName + " doesn't exist!");
	        }
			else{
				int intentIndex = indexAssociatedWithIntent(intentHandler);
				informationPool.get(intentIndex).get(itfName).remove(methodName);
				//informationPool.get(intentHandler).get(itfName).remove(methodName);
				if(informationPool.get(intentIndex).get(itfName).isEmpty())
				//if(informationPool.get(intentHandler).get(itfName).isEmpty())
				{
					informationPool.get(intentIndex).remove(itfName);
					//*informationPool.get(intentHandler).remove(itfName);
					if(informationPool.get(intentIndex).isEmpty())
			        //*if(informationPool.get(intentHandler).isEmpty())
			        {
			        	informationPool.remove(intentIndex);
			        	listOfIntent.remove(intentIndex);
			        	//*informationPool.remove(intentHandler);
			        }
				}
			}
    	}
        else
            throw new NoSuchIntentHandlerException("intent handler doesn't exist!");
    }
}
