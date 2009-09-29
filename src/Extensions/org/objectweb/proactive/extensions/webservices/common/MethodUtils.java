/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.webservices.common;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.util.SerializableMethod;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;


public class MethodUtils {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    private Method[] objectMethods;
    private ArrayList<Method> disallowedMethods;
    private Class<?> objectClass;

    public MethodUtils(Class<?> objectClass) {
        this.objectClass = objectClass;
        this.objectMethods = objectClass.getMethods();
        this.disallowedMethods = getCorrespondingMethods(WSConstants.disallowedMethods
                .toArray(new String[WSConstants.disallowedMethods.size()]));
    }

    private boolean contains(Object[] objects, Object o) {
        Class<?> oClass = o.getClass();
        for (Object object : objects) {
            if ((oClass.cast(object)).equals(oClass.cast(o))) {
                return true;
            }
        }
        return false;
    }

    public static void checkMethodClass(Method method) throws ProActiveException {
        if (method.getDeclaringClass().getName().startsWith(
                org.objectweb.proactive.core.mop.Utils.STUB_DEFAULT_PACKAGE))
            throw new ProActiveException("Method " + method.getName() +
                " is a method form the stub not from the class. \n" + "This method will not be exposed. \n" +
                "Use <class name>.class.getMethod(String methodName, Class<?>... parameters) to solve this issue");
    }

    public static void checkMethodsClass(Method[] methods) throws ProActiveException {
        if (methods != null) {
            for (Method method : methods) {
                checkMethodClass(method);
            }
        }
    }

    public static ArrayList<SerializableMethod> getSerializableMethods(Method[] methods) {
        if (methods == null || methods.length == 0)
            return null;
        ArrayList<SerializableMethod> serializableMethods = new ArrayList<SerializableMethod>();
        for (Method method : methods) {
            serializableMethods.add(new SerializableMethod(method));
        }
        return serializableMethods;
    }

    public static Method[] getMethodsFromSerializableMethods(ArrayList<SerializableMethod> serializableMethods) {
        if (serializableMethods == null) {
            return null;
        }

        Method[] methods = new Method[serializableMethods.size()];
        for (int i = 0; i < serializableMethods.size(); i++) {
            methods[i] = serializableMethods.get(i).getMethod();
        }
        return methods;
    }

    public static ArrayList<String> getCorrespondingMethodsName(Method[] methods) {
        ArrayList<String> methodsName = new ArrayList<String>();
        for (Method method : methods) {
            if (!methodsName.contains(method.getName()))
                methodsName.add(method.getName());
        }
        return methodsName;
    }

    public ArrayList<Method> getCorrespondingMethods(String[] methodsName) {
        ArrayList<Method> methods = new ArrayList<Method>();
        ArrayList<Method> correspondingMethods;
        for (String name : methodsName) {
            correspondingMethods = getMethodsFromName(name);
            if (correspondingMethods != null)
                methods.addAll(correspondingMethods);
        }
        return methods;
    }

    /**
     * From a class and a method name, returns all the methods of this class whose
     * name is the same as the name given in parameter.
     * If several methods match (so with different signature), all these methods are
     * returned.
     */
    private ArrayList<Method> getMethodsFromName(String methodName) {
        ArrayList<Method> correspondingMethods = new ArrayList<Method>();

        for (Method method : this.objectMethods) {
            if (methodName.equals(method.getName()))
                correspondingMethods.add(method);
        }

        if (correspondingMethods.size() == 0)
            return null;

        return correspondingMethods;
    }

    /**
     * Returns the methods to be excluded in the shape of a String array. These methods are methods defined in the
     * WSConstants.disallowedMethods vector and methods which are not in deployedMethods.
     * In case of a null methodsName, only methods in dissallowdMethods vector are
     * returned.
     *
     * @param methodsName
     * @return
     */
    public ArrayList<String> getExcludedMethodsName(String[] deployedMethods) {
        ArrayList<String> excludedMethodsName = new ArrayList<String>();

        excludedMethodsName.addAll(WSConstants.disallowedMethods);

        if ((deployedMethods == null) || (deployedMethods.length == 0))
            return excludedMethodsName;

        for (Method m : this.objectMethods) {
            if (!contains(deployedMethods, m.getName())) {
                excludedMethodsName.add(m.getName());
            }
        }
        logger.info("Excluded method names are the following ones:");
        for (String name : excludedMethodsName) {
            logger.info(name);
        }
        return excludedMethodsName;
    }

    /**
     * Returns the methods to be excluded in the shape of a Method array. These methods are methods defined in the
     * WSConstants.disallowedMethods vector and methods which are not in deployedMethods.
     * In case of a null methodsName, only methods in dissallowdMethods vector are
     * returned.
     *
     * @param methodsName
     * @return
     */
    public ArrayList<Method> getExcludedMethods(Method[] deployedMethods) {
        ArrayList<Method> excludedMethods = new ArrayList<Method>();

        excludedMethods.addAll(this.disallowedMethods);

        if ((deployedMethods == null) || (deployedMethods.length == 0))
            return excludedMethods;

        for (Method m : this.objectMethods) {
            if (!contains(deployedMethods, m)) {
                excludedMethods.add(m);
            }
        }
        return excludedMethods;
    }

}
