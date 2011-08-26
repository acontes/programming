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
package org.objectweb.proactive.extensions.sca;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.Factory;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.sca.control.SCAPropertyController;


/**
 * Utility methods for SCA components.
 *
 * @author The ProActive Team
 */
@PublicAPI
public class Utils extends org.objectweb.proactive.core.component.Utils {
    /**
     * Returns a bootstrap component to create other SCA components.
     *
     * @return Bootstrap component to create other SCA components.
     * @throws InstantiationException If the bootstrap component cannot be created.
     */
    public static Component getBootstrapComponent() throws InstantiationException {
        String bootTmplClassName = System.getProperty("sca.provider");
        if (bootTmplClassName == null) {
            throw new InstantiationException("The sca.provider system property is not defined");
        }
        Factory bootTmpl;
        try {
            Class<?> bootTmplClass = Class.forName(bootTmplClassName);
            bootTmpl = (Factory) bootTmplClass.newInstance();
        } catch (Exception e) {
            throw new InstantiationException("Cannot find or instantiate the '" + bootTmplClassName +
                "' class specified in the sca.provider system property");
        }
        return bootTmpl.newFcInstance();
    }

    /**
     * Returns a bootstrap component to create other SCA components. This method creates an instance of the class
     * whose name is associated to the "sca.provider" key, which must implement the {@link Factory} or
     * {@link GenericFactory} interface, and returns the component instantiated by this factory.
     *
     * @param hints {@link Map} which must associate a value to the "sca.provider" key, and which may associate a
     * {@link ClassLoader} to the "classloader" key. This class loader will be used to load the bootstrap
     * component.
     * @return Bootstrap component to create other SCA components.
     * @throws InstantiationException If the bootstrap component cannot be created.
     */
    public static Component getBootstrapComponent(final Map<?, ?> hints) throws InstantiationException {
        String bootTmplClassName = (String) hints.get("sca.provider");
        if (bootTmplClassName == null) {
            bootTmplClassName = System.getProperty("sca.provider");
        }
        if (bootTmplClassName == null) {
            throw new InstantiationException("The sca.provider value is not defined");
        }
        Object bootTmpl;
        try {
            ClassLoader cl = (ClassLoader) hints.get("classloader");
            if (cl == null) {
                cl = new Utils().getClass().getClassLoader();
            }
            Class<?> bootTmplClass = cl.loadClass(bootTmplClassName);
            bootTmpl = bootTmplClass.newInstance();
        } catch (Exception e) {
            throw new InstantiationException("Cannot find or instantiate the '" + bootTmplClassName +
                "' class associated to the sca.provider key");
        }
        if (bootTmpl instanceof GenericFactory) {
            return ((GenericFactory) bootTmpl).newFcInstance(null, null, hints);
        } else {
            return ((Factory) bootTmpl).newFcInstance();
        }
    }

    /**
     * Returns the {@link SCAIntentController} interface of the given SCA component.
     *
     * @param component Reference on a SCA component.
     * @return {@link SCAIntentController} interface of the given SCA component.
     * @throws NoSuchInterfaceException If there is no such interface.
     */
    public static SCAIntentController getSCAIntentController(final Component component)
            throws NoSuchInterfaceException {
        return (SCAIntentController) component.getFcInterface(Constants.SCA_INTENT_CONTROLLER);
    }

    /**
     * Returns the {@link SCAPropertyController} interface of the given SCA component.
     *
     * @param component Reference on a SCA component.
     * @return {@link SCAPropertyController} interface of the given SCA component.
     * @throws NoSuchInterfaceException If there is no such interface.
     */
    public static SCAPropertyController getSCAPropertyController(final Component component)
            throws NoSuchInterfaceException {
        return (SCAPropertyController) component.getFcInterface(Constants.SCA_PROPERTY_CONTROLLER);
    }

    /*
     * Determines if a class Property annotation.
     *
     * @param className Class to introspect.
     * @return True if the given class contains Authentication annotation.
     * @throws InstantiationException If the class cannot be found.
     */
    public static boolean hasAuthentificationAnnotation(String className) throws InstantiationException {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(org.oasisopen.sca.annotation.Authentication.class) ||
                clazz.isAnnotationPresent(org.osoa.sca.annotations.Authentication.class)) {// Authentication annotation is presented in class level
                return true;
            }
            Method[] methodes = clazz.getDeclaredMethods();
            for (int i = 0; i < methodes.length; i++) {
                // Authentication annotation is presented in method level
                if (methodes[i].isAnnotationPresent(org.oasisopen.sca.annotation.Authentication.class) ||
                    methodes[i].isAnnotationPresent(org.osoa.sca.annotations.Authentication.class)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException cnfe) {
            InstantiationException ie = new InstantiationException("Cannot find classe " + className + " : " +
                cnfe.getMessage());
            ie.initCause(cnfe);
            throw ie;
        }

        return false;
    }

    /*
     * Determines if a class Property annotation.
     *
     * @param className Class to introspect.
     * @return True if the given class contains Authentication annotation.
     * @throws InstantiationException If the class cannot be found.
     */
    public static boolean hasConfidentialityAnnotation(String className) throws InstantiationException {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(org.oasisopen.sca.annotation.Confidentiality.class) ||
                clazz.isAnnotationPresent(org.osoa.sca.annotations.Confidentiality.class)) {// Authentication annotation is presented in class level
                return true;
            }
            Method[] methodes = clazz.getDeclaredMethods();
            for (int i = 0; i < methodes.length; i++) {
                // Authentication annotation is presented in method level
                if (methodes[i].isAnnotationPresent(org.oasisopen.sca.annotation.Confidentiality.class) ||
                    methodes[i].isAnnotationPresent(org.osoa.sca.annotations.Confidentiality.class)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException cnfe) {
            InstantiationException ie = new InstantiationException("Cannot find classe " + className + " : " +
                cnfe.getMessage());
            ie.initCause(cnfe);
            throw ie;
        }

        return false;
    }

    /*
     * Determines if a class Property annotation.
     *
     * @param className Class to introspect.
     * @return True if the given class contains Authentication annotation.
     * @throws InstantiationException If the class cannot be found.
     */
    public static boolean hasReferencenAnnotation(String className) throws InstantiationException {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(org.oasisopen.sca.annotation.Reference.class) ||
                clazz.isAnnotationPresent(org.osoa.sca.annotations.Reference.class)) {// Authentication annotation is presented in class level
                return true;
            }
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                // Authentication annotation is presented in method level
                if (fields[i].isAnnotationPresent(org.oasisopen.sca.annotation.Reference.class) ||
                    fields[i].isAnnotationPresent(org.osoa.sca.annotations.Reference.class)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException cnfe) {
            InstantiationException ie = new InstantiationException("Cannot find classe " + className + " : " +
                cnfe.getMessage());
            ie.initCause(cnfe);
            throw ie;
        }

        return false;
    }

}
