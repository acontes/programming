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
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.type.ComponentType;
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
		PAInterface sItf = serverItf;
		List listOfIntents = getIntentsOfEveryMethod(clientItfName);
		Component ownerLocal = this.getFcItfOwner();
		try {
			if (Utils.getSCAIntentController(ownerLocal).hasAtleastOneIntentHandler(clientItfName)) {
				try {
					String sItfName = IntentClassGenerator.instance().generateClass(sItf.getClass().getName(), sItf.getClass().getName());
					PAInterfaceImpl reference=null;
					try {
						reference = (PAInterfaceImpl)Class.forName(sItfName).getConstructor().newInstance();
					} catch (Exception e) {

						e.printStackTrace();
					} 		
					reference.setFcItfOwner(serverItf.getFcItfOwner());
					reference.setFcItfName(serverItf.getFcItfName());
					reference.setFcType(serverItf.getFcItfType());
					reference.setFcIsInternal(serverItf.isFcInternalItf());
					reference.setProxy(serverItf.getProxy());
					sItf = reference;
					Utils.getSCAIntentController(ownerLocal).addServerReference(clientItfName,reference);
					System.err.println(reference.getClass().getName());
				} catch (ClassGenerationFailedException cgfe) {
					controllerLogger
					.error("could not generate intent interceptor for reference (client interface) " +
							clientItfName + ": " + cgfe.getMessage());
					IllegalBindingException ibe = new IllegalBindingException(
							"could not generate intent controller for reference (client interface) " +
							clientItfName + ": " + cgfe.getMessage());
					ibe.initCause(cgfe);
					throw ibe;
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		super.primitiveBindFc(clientItfName, sItf);
		addIntentsToEveryMethod(clientItfName, listOfIntents);
	}
	/**
	 * This method get all intents of each methods before binding procedure, thus before the generation of the proxy object
	 * @param clientItfName
	 * @return 
	 * @throws NoSuchInterfaceException
	 */
	private List getIntentsOfEveryMethod(String clientItfName) throws NoSuchInterfaceException
	{
		List res = new ArrayList();
		 String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(clientItfName)
         .getFcItfSignature();
		 SCAIntentController scaic = Utils.getSCAIntentController(owner);
		 try {
			Method[] methodList = Class.forName(itfSignature).getDeclaredMethods();
			if (methodList.length > 0) {
                try {
                    for (int i = 0; i < methodList.length; i++) {
                        List<IntentHandler> tmp = scaic.listIntentHandler(clientItfName, methodList[i].getName());
                        res.add(tmp);
                    }
                } catch (NoSuchMethodException nsme) {
                    // Should never happen
                }
            }
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// Should never happen
		}
		return res;
	}
	/**
	 * This method add intents that have been added before binding procedure into proxy object
	 * @param clientItfName
	 * @param listOfIntents
	 * @return
	 * @throws NoSuchInterfaceException
	 */
	private List addIntentsToEveryMethod(String clientItfName, List listOfIntents) throws NoSuchInterfaceException
	{
		List res = new ArrayList();
		 String itfSignature = ((ComponentType) owner.getFcType()).getFcInterfaceType(clientItfName)
         .getFcItfSignature();
		 SCAIntentController scaic = Utils.getSCAIntentController(owner);
		 try {
			Method[] methodList = Class.forName(itfSignature).getDeclaredMethods();
			if (methodList.length > 0) {
                try {
                    for (int i = 0; i < methodList.length; i++) {
                    	
                        List<IntentHandler> tmp = (List<IntentHandler>) listOfIntents.get(i);
                        for (IntentHandler intentHandler : tmp) {
                        	scaic.addIntentHandler(intentHandler,clientItfName, methodList[i].getName());
						}
                    }
                } catch (NoSuchMethodException nsme) {
                    // Should never happen
                }
            }
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// Should never happen
		}
		return res;
	}
	
	//add unbind procedure
	
}
