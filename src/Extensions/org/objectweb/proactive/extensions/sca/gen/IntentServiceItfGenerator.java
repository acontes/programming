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
package org.objectweb.proactive.extensions.sca.gen;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.PAInterfaceImpl;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.AbstractInterfaceClassGenerator;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.sca.Constants;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;


/**
 * Defines {@link #generateInterface(Object, Component)} method which generates a new service interface based on
 * original one. It takes care of presence of intentHandlers, the generated service interface methods should have
 * same name as original one.
 *
 * @author The ProActive Team
 */
public class IntentServiceItfGenerator extends AbstractInterfaceClassGenerator {
    private static IntentServiceItfGenerator instance;

    public static IntentServiceItfGenerator instance() {
        if (instance == null) {
            return new IntentServiceItfGenerator();
        } else {
            return instance;
        }
    }

    /*
     * Returns the CtMethods from the given CtClass which implement the methods of the given service interface.
     *
     * @param superClass The class which generated class extends.
     * @param serviceItf The service interface class which contains all functional methods.
     * @return CtMethods from the given CtClass which implement the methods of the given service interface.
     * @throws NotFoundException If a CtMethod cannot be found.
     */
    private CtMethod[] getMethodsToExtend(CtClass superClass, Class<?> serviceItf) throws NotFoundException {
        Method[] serviceItfMethods = serviceItf.getMethods();
        CtMethod[] methodsToExtend = new CtMethod[serviceItfMethods.length];
        for (int i = 0; i < serviceItfMethods.length; i++) {
            Class<?>[] paramTypes = serviceItfMethods[i].getParameterTypes();
            String[] paramClassNames = new String[paramTypes.length];
            for (int j = 0; j < paramClassNames.length; j++) {
                paramClassNames[j] = paramTypes[j].getName();
            }
            CtClass[] ctParamTypes = pool.get(paramClassNames);
            methodsToExtend[i] = superClass.getDeclaredMethod(serviceItfMethods[i].getName(), ctParamTypes);
        }
        return methodsToExtend;
    }

    /**
     * Extends the given service interface instance to take care of intent handlers.
     *
     * @param serviceItf The service interface instance to extend.
     * @param owner The component on which the service interface instance will be bound and which has intent handlers.
     * @return The generated class name.
     * @throws ClassGenerationFailedException If the generation of the class failed.
     */
    public Object generateInterface(Object serviceItf, String ItfName, Component owner)
            throws ClassGenerationFailedException {
        String serviceItfClassName = serviceItf.getClass().getSimpleName();
        try {
            String componentName = null;
            try {
                GCM.getNameController(owner).getFcName();
            } catch (NoSuchInterfaceException nsie) {
                componentName = "NoNameComponent";
            }
            String generatedClassName = Utils.getIntentInterceptorClassName(componentName,
                    serviceItfClassName);
            List<IntentHandler> intentHandlersInList = ((SCAIntentController) owner
                    .getFcInterface(Constants.SCA_INTENT_CONTROLLER)).listExistingIntentHandler();
            Class<?> generatedClass = null;
            try {
                generatedClass = loadClass(generatedClassName);
            } catch (ClassNotFoundException cnfe) {
                CtClass generatedCtClass = pool.makeClass(generatedClassName);

                // Set super class
                CtClass superClass = pool.get(serviceItfClassName);
                generatedCtClass.setSuperclass(pool.get(serviceItfClassName));
             
                CtField intentHandlerArray = CtField
                		.make("private java.util.List intentArray;"
                				, generatedCtClass);
                generatedCtClass.addField(intentHandlerArray);
                // Add constructors
                CtConstructor defaultConstructor = CtNewConstructor.defaultConstructor(generatedCtClass);
                generatedCtClass.addConstructor(defaultConstructor);
                
                String thirdConstructorBody = "public " +
                generatedClassName +
                "(java.util.List intentArray)" +
                "{\n" +
                 "this.intentArray = intentArray;\n" + "}";
                CtConstructor ThirdConstructor = CtNewConstructor.make(thirdConstructorBody,
                        generatedCtClass);
                generatedCtClass.addConstructor(ThirdConstructor);
 
                // Add extended methods 
                String serviceItfSignature = ((InterfaceType) ((Interface) serviceItf).getFcItfType())
                        .getFcItfSignature();
                Class<?> serviceItfClass = Class.forName(serviceItfSignature);
                CtMethod[] methodsToExtend = getMethodsToExtend(superClass, serviceItfClass);
                for (int i = 0; i < methodsToExtend.length; i++) {
                    // Create wrapper : inside contains super.method();
                    IntentHandler[] intentHandlersForGivingMethod = ((SCAIntentController) owner
                            .getFcInterface(Constants.SCA_INTENT_CONTROLLER)).listIntentHandler(ItfName,
                            methodsToExtend[i].getName()).toArray(new IntentHandler[0]);

                    int[] indexes = ((SCAIntentController) owner
                            .getFcInterface(Constants.SCA_INTENT_CONTROLLER)).indexesOfIntentsOfMethod(
                            ItfName, methodsToExtend[i].getName());

                    CtMethod wrapper = CtNewMethod.delegator(methodsToExtend[i], generatedCtClass);
                    String wrapperNameBase = wrapper.getName()+UUID.randomUUID().toString(); // unique method name
                    wrapper.setName(wrapperNameBase+0);
                    generatedCtClass.addMethod(wrapper);
                    for (int j = 0; j < indexes.length; j++) {
                        int indexOfIntent = indexes[j];
                        CtMethod newMethod = CtNewMethod.delegator(methodsToExtend[i], generatedCtClass);
                        newMethod.setBody("{\n" +
                        		"return ($r)((org.objectweb.proactive.extensions.sca.control.IntentHandler)intentArray.get(" + indexOfIntent +
                              ")).invoke(new org.objectweb.proactive.extensions.sca.control." +
                              "IntentJoinPoint(this, \"" + wrapperNameBase + j + "\", $sig, $args));\n}");
                        // If there are intents left, then create method wrapper, otherwise create really method
                        String methodName = (j == intentHandlersForGivingMethod.length - 1) ? newMethod
                                .getName() : wrapperNameBase + (j + 1);
                        newMethod.setName(methodName);
                        generatedCtClass.addMethod(newMethod);
                    }
                }

//                generatedCtClass.stopPruning(true);
//                generatedCtClass.writeFile("generated/");
//                System.out.println("[JAVASSIST] generated class: " + generatedClassName);

                // 	Generate and add to cache the generated class
                generatedCtClass.defrost();
                byte[] bytecode = generatedCtClass.toBytecode();
                ClassDataCache.instance().addClassData(generatedClassName, bytecode);
                if (logger.isDebugEnabled()) {
                    logger.debug("added " + generatedClassName + " to cache");
                    logger.debug("generated classes cache is: " + ClassDataCache.instance().toString());
                }
                generatedClass = Utils.defineClass(generatedClassName, bytecode);
            }
            // Instantiate class
            PAInterfaceImpl reference = (PAInterfaceImpl) generatedClass.getConstructor(
            		java.util.List.class).newInstance(intentHandlersInList);
            PAInterfaceImpl sItf = (PAInterfaceImpl) serviceItf;
            reference.setFcItfOwner(sItf.getFcItfOwner());
            reference.setFcItfName(sItf.getFcItfName());
            reference.setFcType(sItf.getFcItfType());
            reference.setFcIsInternal(sItf.isFcInternalItf());
            reference.setProxy(sItf.getProxy());
            return reference;
        } catch (Exception e) {
            logger.error("Cannot generate subclass of [" + serviceItfClassName + "] with javassist: " +
                e.getMessage());
            throw new ClassGenerationFailedException("Cannot generate subClass of [" + serviceItfClassName +
                "] with javassist", e);
        }
    }

    /*
     * Non used.
     * (non-Javadoc)
     * @see org.objectweb.proactive.core.component.gen.AbstractInterfaceClassGenerator#generateInterface(java.lang.String, org.objectweb.fractal.api.Component, org.objectweb.proactive.core.component.type.PAGCMInterfaceType, boolean, boolean)
     */
    public PAInterface generateInterface(String interfaceName, Component owner,
            PAGCMInterfaceType interfaceType, boolean isInternal, boolean isFunctionalInterface)
            throws InterfaceGenerationFailedException {
        return null;
    }
}
