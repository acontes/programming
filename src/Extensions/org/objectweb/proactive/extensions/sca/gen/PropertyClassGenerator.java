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

import java.util.ArrayList;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.AbstractInterfaceClassGenerator;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;
import org.oasisopen.sca.annotation.Property;
//import org.osoa.sca.annotations.Property;


/**
 * Defines {@link #generateClass(String)} method which generates a subclass based on original one. It takes care of
 * presence of org.osoa.sca.annotations.Property annotation, the generated subclass contains the getters and setters
 * corresponding to the properties.
 *
 * @author The ProActive Team
 */
public class PropertyClassGenerator extends AbstractInterfaceClassGenerator {
    private static PropertyClassGenerator instance;

    public static PropertyClassGenerator instance() {
        if (instance == null) {
            return new PropertyClassGenerator();
        } else {
            return instance;
        }
    }

    /*
     * Converts a string to another string which the first letter become capital.
     *
     * @param name Name to convert.
     * @return String which first letter is capitalized.
     */
    private String nameUp(String name) {
        char[] nameUpper = name.toCharArray();
        nameUpper[0] = Character.toUpperCase(nameUpper[0]);
        String nameUp = new String(nameUpper);
        return nameUp;
    }

    /**
     * Generates a subclass from root class, if it contains org.osoa.sca.annotations.Property annotation. It adds to
     * the subclass the getter/setter corresponding to the properties.
     *
     * @param classToExtend Name of the class to be set as super class.
	 * @param classToHerit Name of class contains the methods we want to inherit
     * @return The generated class name.
     * @throws ClassGenerationFailedException If the generation failed.
     */
    public String generateClass(String classToExtend,String classToHerit) throws ClassGenerationFailedException {
        String generatedClassName = Utils.getPropertyClassName(classToExtend);
        try {
            loadClass(generatedClassName);
        } catch (ClassNotFoundException cnfe) {
            try {
                CtClass generatedCtClass = pool.makeClass(generatedClassName);
                generatedCtClass.defrost();

                // Set super class
                CtClass superClass = pool.get(classToExtend);
                generatedCtClass.setSuperclass(superClass);
                
                CtClass superClassToHerit = pool.get(classToHerit);

                // Get property fields of superclass
                CtField[] fields = superClassToHerit.getDeclaredFields();
                ArrayList<CtField> propertyFields = new ArrayList<CtField>();
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].hasAnnotation(Property.class)) {
                        propertyFields.add(fields[i]);
                    }
                }

                // Add default constructor
                CtConstructor constructorNoParam = CtNewConstructor.defaultConstructor(generatedCtClass);
                generatedCtClass.addConstructor(constructorNoParam);

                // Add getter and setter for property fields
                for (int i = 0; i < propertyFields.size(); i++) {
                    CtField propertyField = new CtField(propertyFields.get(i), generatedCtClass);
                    CtMethod getter = CtNewMethod.getter("get" + nameUp(propertyField.getName()),
                            propertyField);
                    generatedCtClass.addMethod(getter);
                    CtMethod setter = CtNewMethod.setter("set" + nameUp(propertyField.getName()),
                            propertyField);
                    generatedCtClass.addMethod(setter);
                }

                                generatedCtClass.stopPruning(true);
                                generatedCtClass.writeFile("generated/");
                                System.out.println("[JAVASSIST] generated class: " + generatedClassName);

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
                logger.error("Cannot generate subClass of [" + classToExtend + "] with javassist: " +
                    e.getMessage());
                throw new ClassGenerationFailedException("Cannot generate subClass of [" + classToExtend +
                    "] with javassist", e);
            }
        }
        return generatedClassName;
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
