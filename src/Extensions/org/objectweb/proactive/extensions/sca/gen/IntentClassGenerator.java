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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.AbstractInterfaceClassGenerator;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;


/**
 * Defines {@link #generateClass(String)} method which generates a subclass based on original one. It takes care of
 * presence of intent positioned on service interface of a component.
 *
 * @author The ProActive Team
 */
public class IntentClassGenerator extends AbstractInterfaceClassGenerator {

    private static IntentClassGenerator instance;

    public static IntentClassGenerator instance() {
        if (instance == null) {
            return new IntentClassGenerator();
        } else {
            return instance;
        }
    }

    /**
     * Generates a subclass from root class to manage intents positioned on service interface of a component.
     *
     * @param rootClass Name of the class to be set as super class.
     * @return The generated class name.
     * @throws ClassGenerationFailedException If the generation failed.
     */
    public String generateClass(String rootClass) throws ClassGenerationFailedException {
        String generatedClassName = Utils.getIntentClassName(rootClass);
        try {
            loadClass(generatedClassName);
        } catch (ClassNotFoundException cnfe) {
            try {
                CtClass classToEdit = pool.get(rootClass);
                classToEdit.defrost();
                classToEdit.setName(generatedClassName);

                CtMethod[] methods = classToEdit.getDeclaredMethods();
                List<CtMethod> extentedMethodes = new ArrayList<CtMethod>();
                for (CtMethod ctMethod : methods) {
                    //System.err.println("DEBUG =============== "+ ctMethod.getLongName());
                    if (ctMethod.getModifiers() != Modifier.PRIVATE)
                        extentedMethodes.add(ctMethod);
                }
                // Add intent managements
                for (CtMethod ctExtentedMethod : extentedMethodes) {
                    CtField intentHandlerArray = CtField.make("private java.util.List intentArray" +
                        ctExtentedMethod.getName() + "= new java.util.ArrayList();", classToEdit);
                    classToEdit.addField(intentHandlerArray);
                    String intentArrayName = intentHandlerArray.getName();

                    CtField intentHandlerCounter = CtField.make("private int counter" +
                        ctExtentedMethod.getName() + "= -1;", classToEdit);
                    classToEdit.addField(intentHandlerCounter);
                    String intentCounterName = intentHandlerCounter.getName();

                    CtMethod newMethod = CtNewMethod.copy(ctExtentedMethod, classToEdit, null);
                    String WorkerMethodName = ctExtentedMethod.getName() +
                        new BigInteger(130, new SecureRandom()).toString();
                    newMethod.setName(WorkerMethodName);
                    classToEdit.addMethod(newMethod);

                    ctExtentedMethod.setBody("{\n" + intentCounterName + "++;\n" + "if(" + intentCounterName +
                        " < " + intentArrayName + ".size()){" +
                        "return ($r)((org.objectweb.proactive.extensions.sca.control.IntentHandler)" +
                        intentHandlerArray.getName() + ".get(" + intentHandlerCounter.getName() +
                        ")).invoke(new org.objectweb.proactive.extensions.sca.control." +
                        "IntentJoinPoint(this, \"" + ctExtentedMethod.getName() + "\", $sig, $args));\n" +
                        "}\n" + "else{" + intentCounterName + "= -1;\n" + "return ($r)this." +
                        WorkerMethodName + "($$);\n" + "}\n" + "}\n");

                    generateHelperMethods(intentHandlerArray, classToEdit);

                }

                //                				classToEdit.stopPruning(true);
                //                				classToEdit.writeFile("generated/");
                //                				System.out.println("[JAVASSIST] generated class: " + generatedClassName);

                // Generate and add to cache the generated class
                byte[] bytecode = classToEdit.toBytecode();
                ClassDataCache.instance().addClassData(generatedClassName, bytecode);
                if (logger.isDebugEnabled()) {
                    logger.debug("added " + generatedClassName + " to cache");
                    logger.debug("generated classes cache is: " + ClassDataCache.instance().toString());
                }
                Utils.defineClass(generatedClassName, bytecode);

                // Defrost the generated class
                classToEdit.defrost();
            } catch (Exception e) {
                e.printStackTrace();
                throw new ClassGenerationFailedException("Cannot generate subClass of [" + rootClass +
                    "] with javassist", e);
            }

        }
        return generatedClassName;
    }

    /*
     * Generates some methods useful to manage intents on a method.
     *
     * @param intentArray Array of intents positioned on a method.
     * @param generatedCtClass Generated class.
     * @throws CannotCompileException If the generation failed.
     */
    private void generateHelperMethods(CtField intentArray, CtClass generatedCtClass)
            throws CannotCompileException {
        String intentArrayName = intentArray.getName();
        CtMethod add = CtNewMethod.make("public void addInto" + intentArrayName +
            "(org.objectweb.proactive.extensions.sca.control.IntentHandler ih){" + intentArrayName +
            ".add(ih);\n" + "}", generatedCtClass);
        generatedCtClass.addMethod(add);
        CtMethod remove = CtNewMethod.make("public void removeFrom" + intentArrayName +
            "(org.objectweb.proactive.extensions.sca.control.IntentHandler ih){" + intentArrayName +
            ".remove(ih);\n" + "}", generatedCtClass);
        generatedCtClass.addMethod(remove);
        CtMethod list = CtNewMethod.make("public java.util.List list" + intentArrayName + "(){" + "return " +
            intentArrayName + ";" + "}", generatedCtClass);
        generatedCtClass.addMethod(list);

    }

    /*
     * Non used.
     */
    public PAInterface generateInterface(String interfaceName, Component owner,
            PAGCMInterfaceType interfaceType, boolean isInternal, boolean isFunctionalInterface)
            throws InterfaceGenerationFailedException {
        return null;
    }

}
