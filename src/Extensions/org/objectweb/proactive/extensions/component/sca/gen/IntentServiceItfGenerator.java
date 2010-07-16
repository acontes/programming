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
package org.objectweb.proactive.extensions.component.sca.gen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.PAInterfaceImpl;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.component.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.component.sca.exceptions.ClassGenerationFailedException;


/**
 * InterceptorGenerator class define generateClass method which generate a new server service interface based on original one.
 * it take conscience of presence of intentHandlers, the generated server service methods should have same name as original one. 
 * @author mug
 *
 */
public class IntentServiceItfGenerator extends AbstractClassGenerator {
    private static IntentServiceItfGenerator instance;

    public static IntentServiceItfGenerator instance() {
        if (instance == null) {
            return new IntentServiceItfGenerator();
        } else {
            return instance;
        }
    }

    /**
     * 
     * @param superClass the super class which generated class extended
     * @param serviceItf the service interface which contain all functional calls
     * @return an array of CtMethod objects of functional calls
     * @throws NotFoundException
     */
    private CtMethod[] filtreMethods(CtClass superClass, Class serviceItf) throws NotFoundException {
        Method[] meds = serviceItf.getMethods();
        CtMethod[] Ctmeds = new CtMethod[meds.length];
        for (int i = 0; i < meds.length; i++) {
            Class<?>[] paramType = meds[i].getParameterTypes();
            String[] paramClaNames = new String[paramType.length];
            for (int j = 0; j < paramClaNames.length; j++) {
                paramClaNames[j] = paramType[j].getName();
            }
            CtClass[] CtParamType = pool.get(paramClaNames);
            Ctmeds[i] = superClass.getDeclaredMethod(meds[i].getName(), CtParamType);
        }
        return Ctmeds;
    }

    /**
     * 
     * @param sItf server interface object
     * @param numberOfIntents number of intents contain in the component
     * @return return generated class name
     * @throws ClassGenerationFailedException
     */
    public Object generateClass(Object sItf, Component owner, int numberOfIntents)
            throws ClassGenerationFailedException {
        String className = sItf.getClass().getSimpleName(); //extended class name
        try {
            String CName = Utils.getInterceptorClassName(className); //generated class name
            String serviceItfName = ((InterfaceType) ((Interface) sItf).getFcItfType()).getFcItfSignature(); //service interface name
            IntentHandler[] listOfIH = org.objectweb.proactive.extensions.component.sca.Utils
                    .getSCAIntentController(owner).listFcIntentHandler(null).toArray(new IntentHandler[0]);

            //System.err.println("Debugg " + serviceItfName);
            Class<?> generatedClass = null;
            try {
                generatedClass = loadClass(CName);
            } catch (ClassNotFoundException cnfe) {
                CtClass generatedCtClass = pool.makeClass(CName);
                //get super class
                CtClass superClass = pool.get(className);
                generatedCtClass.setSuperclass(superClass);

                Class<?> serviceItf = Class.forName(serviceItfName);
                // create intentHandlers instance fields 
                CtField intentHanderlers = CtField
                        .make(
                                "private org.objectweb.proactive.extensions.component.sca.control.IntentHandler[] IntentHandlers;",
                                generatedCtClass);
                generatedCtClass.addField(intentHanderlers);
                //create constructors 
                CtConstructor constructorDefault = CtNewConstructor.defaultConstructor(generatedCtClass);
                generatedCtClass.addConstructor(constructorDefault);
                String SecondConstructorBody = "public " +
                    CName +
                    "(org.objectweb.proactive.extensions.component.sca.control.IntentHandler[] IntentHandlers)" +
                    "{" +
                    "if($1.length != " +
                    numberOfIntents +
                    "){throw new org.objectweb.proactive.core.ProActiveRuntimeException(\"number of intents is not correct\");}" +
                    "else{this.IntentHandlers = IntentHandlers;}" + "}";
                CtConstructor SecondConstructor = CtNewConstructor.make(SecondConstructorBody,
                        generatedCtClass);
                generatedCtClass.addConstructor(SecondConstructor);
                // create all method calls 
                CtMethod[] services = filtreMethods(superClass, serviceItf);
                for (int i = 0; i < services.length; i++) {
                    // create wrapper : inside contain super.method();
                    CtMethod wrapper = CtNewMethod.delegator(services[i], generatedCtClass);
                    wrapper.setName(wrapper.getName() + 0);
                    generatedCtClass.addMethod(wrapper);
                    for (int j = 0; j < numberOfIntents; j++) {
                        CtMethod tmp = CtNewMethod.delegator(services[i], generatedCtClass);
                        tmp.setBody("{return ($r)IntentHandlers[" + j +
                            "].invoke(new org.objectweb.proactive.extensions.component.sca.control." +
                            "IntentJoinPoint(\"" + tmp.getName() + j + "\",this,$sig,$args));}");
                        // if there are intents left, then create method wrapper, otherwise create really method
                        String MethodName = (j == numberOfIntents - 1) ? tmp.getName() : tmp.getName() +
                            (j + 1);
                        tmp.setName(MethodName);
                        generatedCtClass.addMethod(tmp);
                    }
                }

                //	generatedCtClass.stopPruning(true);
                //	generatedCtClass.writeFile("generated_intent/");
                //	superClass.writeFile("generated_intent/");
                //	System.out.println("[JAVASSIST] generated class: " + CName);
                // 	Generate and add to cache the generated class
                generatedCtClass.defrost(); // defrost the generated class    
                byte[] bytecode = generatedCtClass.toBytecode();
                ClassDataCache.instance().addClassData(CName, bytecode);
                if (logger.isDebugEnabled()) {
                    logger.debug("added " + CName + " to cache");
                    logger.debug("generated classes cache is: " + ClassDataCache.instance().toString());
                }

                generatedClass = Utils.defineClass(CName, bytecode);
            }

            Constructor<?> cons = generatedClass.getConstructor(listOfIH.getClass());
            PAInterfaceImpl reference = (PAInterfaceImpl) cons.newInstance((Object) listOfIH); // create new instance
            PAInterfaceImpl serviceItf = (PAInterfaceImpl) sItf;
            reference.setFcItfOwner(serviceItf.getFcItfOwner()); // set owner
            reference.setFcItfName(serviceItf.getFcItfName());
            reference.setFcType(serviceItf.getFcItfType());
            reference.setFcIsInternal(serviceItf.isFcInternalItf());
            reference.setProxy(serviceItf.getProxy());
            return reference;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Cannot generate subClass of [" + className + "] with javassist: " + e.getMessage());
            throw new ClassGenerationFailedException("Cannot generate subClass of [" + className +
                "] with javassist", e);
        }
    }

}
