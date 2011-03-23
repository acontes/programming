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

                CtClass superClassToHerit = pool.get(classToHerit);

                // Add constructors
                CtConstructor defaultConstructor = CtNewConstructor.defaultConstructor(generatedCtClass);
                generatedCtClass.addConstructor(defaultConstructor);

                // Get property fields of superclass
                List<CtField> fields = new ArrayList<CtField>(Arrays.asList(superClassToHerit.getFields()));
                do {
                    superClassToHerit = superClassToHerit.getSuperclass();
                    List<CtField> asList = Arrays.asList(superClassToHerit.getDeclaredFields());
                    fields.addAll(asList);
                } while (!superClassToHerit.getName().equals(Object.class.getName()));

                ArrayList<CtField> requiresFields = new ArrayList<CtField>();
                for (int i = 0; i < fields.size(); i++) {
                    if (fields.get(i).hasAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class)) {
                        requiresFields.add(fields.get(i));
                    }
                }

                String listFCBody = "return new String[] {";
                int i = 0;
                for (i = 0; i < requiresFields.size() - 1; i++) {
                    org.objectweb.fractal.fraclet.annotations.Requires tmp = (Requires) requiresFields.get(i)
                            .getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
                    listFCBody += "\"" + tmp.name() + "\",";
                }
                org.objectweb.fractal.fraclet.annotations.Requires tmp = (Requires) requiresFields.get(i)
                        .getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
                listFCBody += "\"" + tmp.name() + "\"};";
                CtMethod listFC = CtNewMethod.make("public String[] listFc() {" + listFCBody + "}",
                        generatedCtClass);
                generatedCtClass.addMethod(listFC);

                String lookupFcBody = "";
                for (i = 0; i < requiresFields.size(); i++) {
                    org.objectweb.fractal.fraclet.annotations.Requires tmp2 = (Requires) requiresFields
                            .get(i).getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
                    lookupFcBody += "if (clientItfName.equals(\"" + tmp2.name() + "\")) {" + "return " +
                        requiresFields.get(i).getName() + "; }";
                }
                lookupFcBody += "else { return null; }";
                CtMethod lookupFc = CtNewMethod.make("public Object lookupFc(String clientItfName) {" +
                    lookupFcBody + "}", generatedCtClass);
                generatedCtClass.addMethod(lookupFc);

                String bindFcBody = "";
                for (i = 0; i < requiresFields.size(); i++) {
                    org.objectweb.fractal.fraclet.annotations.Requires tmp3 = (Requires) requiresFields
                            .get(i).getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
                    bindFcBody += "if (clientItfName.equals(\"" + tmp3.name() + "\")) {" +
                        requiresFields.get(i).getName() + " = (" + requiresFields.get(i).getType().getName() +
                        ")serverItf; }";
                }
                CtMethod bindFc = CtNewMethod.make(
                        "public void bindFc(String clientItfName, Object serverItf) {" + bindFcBody + "}",
                        generatedCtClass);
                generatedCtClass.addMethod(bindFc);

                String unbindFcBody = "";
                for (i = 0; i < requiresFields.size(); i++) {
                    org.objectweb.fractal.fraclet.annotations.Requires tmp4 = (Requires) requiresFields
                            .get(i).getAnnotation(org.objectweb.fractal.fraclet.annotations.Requires.class);
                    unbindFcBody += "if (clientItfName.equals(\"" + tmp4.name() + "\")) {" +
                        requiresFields.get(i).getName() + " = null ; }";
                }
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
