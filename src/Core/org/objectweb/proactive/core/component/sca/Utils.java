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
package org.objectweb.proactive.core.component.sca;

import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.Factory;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.component.sca.control.SCAPropertyController;


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
}
