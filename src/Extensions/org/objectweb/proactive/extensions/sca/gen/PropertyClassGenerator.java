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


/**
 * Defines {@link #generateClass(String)} method which generates a subclass based on original one. It takes care of
 * presence of Property annotation, the generated subclass contains the getters and setters corresponding to the
 * properties.
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

    /**
     * Generates a subclass from root class, if it contains Property annotation. It adds to the subclass the
     * getter/setter corresponding to the properties.
     *
     * @param rootClass Name of the class to be set as super class.
     * @return The generated class name.
     * @throws ClassGenerationFailedException If the generation failed.
     */
    public String generateClass(String rootClass) throws ClassGenerationFailedException {
        String generatedClassName = Utils.getPropertyClassName(rootClass);
        try {
            loadClass(generatedClassName);
        } catch (ClassNotFoundException cnfe) {
            try {
                CtClass classToEdit = pool.get(rootClass);
                classToEdit.defrost();
                classToEdit.setName(generatedClassName);

                // Get property fields of superclass
                CtField[] fields = classToEdit.getDeclaredFields();
                ArrayList<CtField> propertyFields = new ArrayList<CtField>();
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].hasAnnotation(org.oasisopen.sca.annotation.Property.class) ||
                        fields[i].hasAnnotation(org.osoa.sca.annotations.Property.class)) {
                        propertyFields.add(fields[i]);
                    }
                }

                // Add getter and setter for property fields
                for (int i = 0; i < propertyFields.size(); i++) {
                    CtField propertyField = new CtField(propertyFields.get(i), classToEdit);
                    CtMethod getter = CtNewMethod.getter("get" + nameUp(propertyField.getName()),
                            propertyField);
                    classToEdit.addMethod(getter);
                    CtMethod setter = CtNewMethod.setter("set" + nameUp(propertyField.getName()),
                            propertyField);
                    classToEdit.addMethod(setter);
                }

                //				classToEdit.stopPruning(true);
                //				classToEdit.writeFile("generated/");
                //				System.out.println("[JAVASSIST] generated class: " + generatedClassName);

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
                throw new ClassGenerationFailedException("Cannot generate subClass of [" + rootClass +
                    "] with javassist", e);
            }
        }
        return generatedClassName;
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

    /*
     * Non used.
     */
    public PAInterface generateInterface(String interfaceName, Component owner,
            PAGCMInterfaceType interfaceType, boolean isInternal, boolean isFunctionalInterface)
            throws InterfaceGenerationFailedException {
        return null;
    }
}
