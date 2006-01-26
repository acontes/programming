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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.proactive.core.ProActiveRuntimeException;


/**
 * Utility class for bytecode generation operations.
 *
 * @author Matthieu Morel
 *
 */
public class Utils {
    public static final String GENERATED_DEFAULT_PREFIX = "Generated_";
    public static final String REPRESENTATIVE_DEFAULT_SUFFIX = "_representative";
    public static final String COMPOSITE_REPRESENTATIVE_SUFFIX = "_composite";
    public static final String OUTPUT_INTERCEPTOR_SUFFIX = "_outputInterceptor";
    public static final String STUB_DEFAULT_PACKAGE = null;
    public static final char GEN_PACKAGE_SEPARATOR = '_';
    public static final String GEN_ITF_NAME_SEPARATOR = "_I_";
    public static final String GEN_MIDDLE_SEPARATOR = "_O_";

    public static boolean isRepresentativeClassName(String classname) {
        return (classname.startsWith(GENERATED_DEFAULT_PREFIX) &&
        classname.endsWith(REPRESENTATIVE_DEFAULT_SUFFIX));
    }

    public static String getInterfaceSignatureFromRepresentativeClassName(
        String className) {
        if (!isRepresentativeClassName(className)) {
            return null;
        }
        String tmp = className.replaceAll(GENERATED_DEFAULT_PREFIX, "");
        tmp = tmp.replaceAll(REPRESENTATIVE_DEFAULT_SUFFIX, "");
        tmp = tmp.substring(0, tmp.indexOf(GEN_MIDDLE_SEPARATOR));
        tmp = tmp.replaceAll(GEN_ITF_NAME_SEPARATOR, "-").replace(GEN_PACKAGE_SEPARATOR,
                '.');

        return tmp;
    }

    public static String getInterfaceNameFromRepresentativeClassName(
        String className) {
        if (!isRepresentativeClassName(className)) {
            return null;
        }
        String tmp = className.replaceAll(GENERATED_DEFAULT_PREFIX, "");
        tmp = tmp.replaceAll(REPRESENTATIVE_DEFAULT_SUFFIX, "");
        tmp = tmp.substring(tmp.indexOf(GEN_MIDDLE_SEPARATOR) +
                GEN_MIDDLE_SEPARATOR.length(), tmp.length());
        tmp = tmp.replaceAll(GEN_ITF_NAME_SEPARATOR, "-").replace(GEN_PACKAGE_SEPARATOR,
                '.');
        return tmp;
    }

    public static String getMetaObjectClassName(
        String functionalInterfaceName, String javaInterfaceName) {
        // just a way to have an identifier (possibly not unique ? ... but readable)
        return (GENERATED_DEFAULT_PREFIX +
        javaInterfaceName.replace('.', GEN_PACKAGE_SEPARATOR) +
        GEN_MIDDLE_SEPARATOR +
        functionalInterfaceName.replaceAll("-", GEN_ITF_NAME_SEPARATOR));
    }

    public static String getMetaObjectComponentRepresentativeClassName(
        String functionalInterfaceName, String javaInterfaceName) {
        // just a way to have an identifier (possibly not unique ... but readable)
        return (getMetaObjectClassName(functionalInterfaceName,
            javaInterfaceName) + REPRESENTATIVE_DEFAULT_SUFFIX);
    }

    public static String getOutputInterceptorClassName(
        String functionalInterfaceName, String javaInterfaceName) {
        // just a way to have an identifier (possibly not unique ... but readable)
        return (getMetaObjectClassName(functionalInterfaceName,
            javaInterfaceName) + OUTPUT_INTERCEPTOR_SUFFIX);
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
}
