/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.component.gen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

/**
 * This class is the parent of classes for generating component interfaces. It provides utility methods that are used in subclasses.
 * 
 * @author Matthieu Morel
 *
 */
public abstract class AbstractInterfaceClassGenerator {
    protected static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_BYTECODE_GENERATION);
    protected static ClassPool pool = ClassPool.getDefault();
    protected static Hashtable generatedClassesCache = new Hashtable();

    /**
     * Returns the generatedClassesCache.
     * @return a Map acting as a cache for generated classes
     */
    static Map getGeneratedClassesCache() {
        return generatedClassesCache;
    }

    protected Class loadClass(final String className)
        throws ClassNotFoundException {
        // try to fetch the class from the default class loader
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    public ProActiveInterface generateControllerInterface(
        final String controllerInterfaceName, Component owner,
        InterfaceType interfaceType) throws InterfaceGenerationFailedException {
        return generateInterface(controllerInterfaceName, owner, interfaceType,
            false, false);
    }

    public ProActiveInterface generateFunctionalInterface(
        final String functionalInterfaceName, Component owner,
        InterfaceType interfaceType) throws InterfaceGenerationFailedException {
        return generateInterface(functionalInterfaceName, owner, interfaceType,
            false, true);
    }

    public abstract ProActiveInterface generateInterface(
        final String interfaceName, Component owner,
        InterfaceType interfaceType, boolean isInternal,
        boolean isFunctionalInterface)
        throws InterfaceGenerationFailedException;

    /**
     * Gets all super-interfaces from the interfaces of this list, and
     * adds them to this list.
     * @param interfaces a list of interfaces
     */
    public static void addSuperInterfaces(List interfaces)
        throws NotFoundException {
        for (int i = 0; i < interfaces.size(); i++) {
            CtClass[] super_itfs_table = ((CtClass) interfaces.get(i)).getInterfaces();
            List super_itfs = new ArrayList(super_itfs_table.length); // resizable list
            for (int j = 0; j < super_itfs_table.length; j++) {
                super_itfs.add(super_itfs_table[j]);
            }
            addSuperInterfaces(super_itfs);
            CtClass super_itf;
            for (int j = 0; j < super_itfs.size(); j++) {
                if (!interfaces.contains(super_itfs.get(j))) {
                    super_itf = (CtClass) super_itfs.get(j);
                    if (!(super_itf.equals(pool.get(
                                    ProActiveInterface.class.getName())) ||
                            super_itf.equals(pool.get(Interface.class.getName())))) {
                        interfaces.add(super_itfs.get(j));
                    }
                }
            }
        }
    }

    public static Class defineClass(final String className,
        final byte[] bytes) {
        // The following code invokes defineClass on the current thread classloader by reflection
        try {
            Class clc = Class.forName("java.lang.ClassLoader");
            Class[] argumentTypes = new Class[4];
            argumentTypes[0] = className.getClass();
            argumentTypes[1] = bytes.getClass();
            argumentTypes[2] = Integer.TYPE;
            argumentTypes[3] = Integer.TYPE;

            Method method = clc.getDeclaredMethod("defineClass", argumentTypes);
            method.setAccessible(true);

            Object[] effectiveArguments = new Object[4];
            effectiveArguments[0] = className;
            effectiveArguments[1] = bytes;
            effectiveArguments[2] = new Integer(0);
            effectiveArguments[3] = new Integer(bytes.length);

            return (Class) method.invoke(Thread.currentThread()
                                               .getContextClassLoader(),
                effectiveArguments);
        } catch (ClassNotFoundException cnfe) {
            //cat.error(cnfe.toString());
            throw new ProActiveRuntimeException(cnfe.toString());
        } catch (NoSuchMethodException nsme) {
            nsme.printStackTrace();

            //cat.error(nsme.toString());
            throw new ProActiveRuntimeException(nsme.toString());
        } catch (IllegalAccessException iae) {
            throw new ProActiveRuntimeException(iae.toString());
        } catch (InvocationTargetException ite) {
            throw new ProActiveRuntimeException(ite.toString());
        }
    }

    /**
     * retreives the bytecode associated to the generated class of the given name
     */
    public static byte[] getClassData(String classname) {
        return (byte[]) getGeneratedClassesCache().get(classname);
    }
}
