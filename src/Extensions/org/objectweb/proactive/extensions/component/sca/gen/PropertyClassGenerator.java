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

import java.io.IOException;
import java.util.ArrayList;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.util.ClassDataCache;
import org.objectweb.proactive.extensions.component.sca.exceptions.ClassGenerationFailedException;


/**
 *
 * @author The ProActive Team
 */
public class PropertyClassGenerator extends AbstractClassGenerator {
    private static PropertyClassGenerator instance;

    public static PropertyClassGenerator instance() {
        if (instance == null) {
            return new PropertyClassGenerator();
        } else {
            return instance;
        }
    }

    /**
     * convert a String to another string which the first case become capital
     * "cool" --> "Cool"
     * @param name
     * @return String which first case is capital letter
     */
    private String NameUp(String name) {
        char[] nameUpper = name.toCharArray();
        nameUpper[0] = Character.toUpperCase(nameUpper[0]);
        String nameUp = new String(nameUpper);
        return nameUp;
    }

    /**
     * generate a subclass from root class, if it contains org.osoa.sca.annotations.Property annotation
     * @param className Name of the component class.
     * @return The generated class name.
     */
    public String generateClass(String className) throws ClassGenerationFailedException {
        String CName = Utils.getPropertyClassName(className);
        Class<?> generatedClass;
        try {
            generatedClass = loadClass(CName);
        } catch (ClassNotFoundException cnfe) {
            try {
                CtClass generatedCtClass = pool.makeClass(CName);
                generatedCtClass.defrost();
                CtClass sup = pool.get(className); // get super class
                CtField[] decFields = sup.getDeclaredFields(); // get declared fleids of superclass
                ArrayList<CtField> proptyFields = new ArrayList<CtField>();
                // check annotated feilds
                for (int i = 0; i < decFields.length; i++) {
                    Object anno = decFields[i].getAnnotation(org.osoa.sca.annotations.Property.class);
                    if (anno != null) {
                        proptyFields.add(decFields[i]);
                    }

                }
                generatedCtClass.setSuperclass(sup); // set super class to generated class
                CtConstructor constructorNoParam = CtNewConstructor.defaultConstructor(generatedCtClass);
                generatedCtClass.addConstructor(constructorNoParam);
                // create and  add getter , setter
                for (int i = 0; i < proptyFields.size(); i++) {
                    CtField tmp = new CtField(proptyFields.get(i), generatedCtClass);
                    CtMethod getter = CtNewMethod.getter("get" + NameUp(tmp.getName()), tmp);
                    CtMethod setter = CtNewMethod.setter("set" + NameUp(tmp.getName()), tmp);
                    generatedCtClass.addMethod(getter);
                    generatedCtClass.addMethod(setter);
                }
                //          generatedCtClass.stopPruning(true);
                //        	generatedCtClass.writeFile("generated/");
                //			System.out.println("[JAVASSIST] generated class: " + CName); 	
                // Generate and add to cache the generated class
                byte[] bytecode = generatedCtClass.toBytecode();
                ClassDataCache.instance().addClassData(CName, bytecode);
                if (logger.isDebugEnabled()) {
                    logger.debug("added " + CName + " to cache");
                    logger.debug("generated classes cache is: " + ClassDataCache.instance().toString());
                }
                generatedClass = Utils.defineClass(CName, bytecode);
                generatedCtClass.defrost(); // defrost the generated class
            } catch (Exception e) {
                logger.error("Cannot generate subClass of [" + className + "] with javassist: " +
                    e.getMessage());
                throw new ClassGenerationFailedException("Cannot generate subClass of [" + className +
                    "] with javassist", e);
            }
        }
        return CName;
    }
}
