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
package org.objectweb.proactive.core.component.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.fraclet.annotations.Requires;
import org.objectweb.fractal.fraclet.types.Cardinality;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;


/**
 * Defines {@link #generateClass(String)} method which generates a subclass based on original one. It implements the
 * methods of the {@link BindingController} for the fields where the annotation {@link Requires} is present.
 *
 * @author The ProActive Team
 */
public class RequiresClassGenerator extends AbstractInterfaceClassGenerator {

    private static RequiresClassGenerator instance;

    public static RequiresClassGenerator instance() {
        if (instance == null) {
            return new RequiresClassGenerator();
        } else {
            return instance;
        }
    }

    /**
     * Generates a subclass from root class. It implements the methods of the {@link BindingController} for the
     * fields where the annotation {@link Requires} is present.
     *
     * @param classToExtend Name of the class to be set as super class.
     * @param classToHerit Name of class contains the methods we want to inherit
     * @return The generated class name.
     * @throws ClassGenerationFailedException If the generation failed.
     */
    public String generateClass(String classToExtend, String classToHerit)
            throws InterfaceGenerationFailedException {
        String generatedClassName = Utils.getRequiresClassName(classToExtend);
        try {
            loadClass(generatedClassName);
        } catch (ClassNotFoundException cnfe) {
            try {
                CtClass generatedCtClass = pool.makeClass(generatedClassName);
                generatedCtClass.defrost();
                CtClass superClass = pool.get(classToExtend);
                generatedCtClass.setSuperclass(superClass);

                CtClass interfaceToImplement = pool
                        .get("org.objectweb.fractal.api.control.BindingController");
                generatedCtClass.addInterface(interfaceToImplement);

                CtClass superClassToHerit = pool.get(classToExtend);

                // Add constructors
                CtConstructor defaultConstructor = CtNewConstructor.defaultConstructor(generatedCtClass);
                generatedCtClass.addConstructor(defaultConstructor);

                // Get property fields of superclass
                List<CtField> fields = new ArrayList<CtField>(Arrays.asList(superClassToHerit.getDeclaredFields()));
                do {
                    superClassToHerit = superClassToHerit.getSuperclass();
                    List<CtField> asList = Arrays.asList(superClassToHerit.getDeclaredFields());
                    fields.addAll(asList);
                } while (!superClassToHerit.getName().equals(Object.class.getName()));

                ArrayList<CtField> requiresFields = new ArrayList<CtField>();
                ArrayList<CtField> collectionFields = new ArrayList<CtField>();
                for (int i = 0; i < fields.size(); i++) {
                	Requires tmp = (Requires)fields.get(i).getAnnotation(Requires.class);
                    if (tmp != null) {
                        if (tmp.cardinality().equals(Cardinality.COLLECTION)) {
                            collectionFields.add(fields.get(i));
                        } else {
                            requiresFields.add(fields.get(i));
                        }
                    }
                }
                // Begin of the listFCBody construction
                int requiresSize = requiresFields.size();
                String collectionFieldSize = "";
                String keySetTmp = "java.util.Set tmp = new java.util.HashSet();\n";
                String requiresTmp = "";
                for (Iterator<CtField> iterator = collectionFields.iterator(); iterator.hasNext();) {
                    CtField field = iterator.next();
                    collectionFieldSize += field.getName() + ".size() + ";
                    keySetTmp += "tmp.addAll(" + field.getName() + ".keySet());\n";
                }
                keySetTmp += "tmp.toArray(result);\n";
                int i = 0;
                for (i = 0; i < requiresFields.size(); i++) {
                    Requires tmp = (Requires)requiresFields.get(i).getAnnotation(Requires.class);
                    requiresTmp += "result[" + collectionFieldSize + "+" + i + "]= \"" + tmp.name() + "\";\n";
                }
                String listFCBody = "String[] result = new String[" + collectionFieldSize + requiresSize +
                    "];\n" + keySetTmp + requiresTmp + "return result;\n";
                // End of the listFCBody construction
                CtMethod listFC = CtNewMethod.make("public String[] listFc() {" + listFCBody + "}",
                        generatedCtClass);
                generatedCtClass.addMethod(listFC);

                // Begin of the lookupFCBody construction
                String lookupFcBody = "";
                for (i = 0; i < requiresFields.size(); i++) {
                    Requires tmp2 = (Requires)requiresFields.get(i).getAnnotation(Requires.class);
                    lookupFcBody += "if (clientItfName.equals(\"" + tmp2.name() + "\")) {\n" + "return " +
                        requiresFields.get(i).getName() + ";\n }\n";
                }

                for (Iterator<CtField> iterator = collectionFields.iterator(); iterator.hasNext();) {
                    CtField field = iterator.next();
                    lookupFcBody += "if(" + field.getName() + ".containsKey(clientItfName)){\n" + "return " +
                        field.getName() + ".get(clientItfName);\n" + "}\n";
                }
                lookupFcBody += "else { return null; }\n";
                // End of the lookupFCBody construction
                CtMethod lookupFc = CtNewMethod.make("public Object lookupFc(String clientItfName) {" +
                    lookupFcBody + "}", generatedCtClass);
                generatedCtClass.addMethod(lookupFc);

                // Begin of the bindFCBody construction
                String bindFcBody = "";
                for (i = 0; i < requiresFields.size(); i++) {
                    Requires tmp3 = (Requires)requiresFields.get(i).getAnnotation(Requires.class);
                    bindFcBody += "if (clientItfName.equals(\"" + tmp3.name() + "\")) \n{" +
                        requiresFields.get(i).getName() + " = (" + requiresFields.get(i).getType().getName() +
                        ")serverItf;\n return; \n }\n";
                }

                for (Iterator<CtField> iterator = collectionFields.iterator(); iterator.hasNext();) {
                    CtField field = iterator.next();
                    bindFcBody += "else{\n" + field.getName() + ".put(clientItfName,serverItf);\n" + "}\n";
                }
                // End of the bindFCBody construction
                CtMethod bindFc = CtNewMethod.make(
                        "public void bindFc(String clientItfName, Object serverItf) {" + bindFcBody + "}",
                        generatedCtClass);
                generatedCtClass.addMethod(bindFc);

                // Begin of the unbindFCBody construction
                String unbindFcBody = "";
                for (i = 0; i < requiresFields.size(); i++) {
                    Requires tmp4 = (Requires)requiresFields.get(i).getAnnotation(Requires.class);
                    unbindFcBody += "if (clientItfName.equals(\"" + tmp4.name() + "\"))\n {" +
                        requiresFields.get(i).getName() + " = null ;\n return;\n }\n";
                }

                for (Iterator<CtField> iterator = collectionFields.iterator(); iterator.hasNext();) {
                    CtField field = iterator.next();
                    unbindFcBody += "if(" + field.getName() + ".containsKey(clientItfName)){\n" +
                        field.getName() + ".remove(clientItfName);\n" + "}\n";
                }
                // End of the unbindFCBody construction
                CtMethod unbindFc = CtNewMethod.make("public void unbindFc(String clientItfName) {" +
                    unbindFcBody + "}", generatedCtClass);
                generatedCtClass.addMethod(unbindFc);

                //				generatedCtClass.stopPruning(true);
                //				generatedCtClass.writeFile("generated/");
                //				System.out.println("[JAVASSIST] generated class: " + generatedClassName);

                // Generate and add to cache the generated class
                byte[] bytecode = generatedCtClass.toBytecode();
                ClassDataCache.instance().addClassData(generatedClassName, bytecode);
                if (logger.isDebugEnabled()) {
                    logger.debug("added " + generatedClassName + " to cache");
                    logger.debug("generated classes cache is: " + ClassDataCache.instance().toString());
                }
                Utils.defineClass(generatedClassName, bytecode);

                // Defrost the generated class
                generatedCtClass.defrost();
            } catch (Exception e) {
                throw new InterfaceGenerationFailedException("Cannot generate subClass of [" + classToExtend +
                    "] with javassist", e);
            }

        }
        return generatedClassName;
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
