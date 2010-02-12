/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
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
package org.objectweb.proactive.core.component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.etsi.uri.gcm.api.type.GCMInterfaceType;
import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;


/**
 * Utility methods
 *
 * @author The ProActive Team
 */
public class Utils {
    public static Component getBootstrapComponent() throws InstantiationException {
        Component bootstrapComponent;
        try {
            bootstrapComponent = GCM.getBootstrapComponent();
        } catch (InstantiationException ie) {
            if (System.getProperty("gcm.provider") == null) {
                try {
                    bootstrapComponent = Fractal.getBootstrapComponent();
                } catch (InstantiationException ie2) {
                    if (System.getProperty("fractal.provider") == null) {
                        throw new InstantiationException(
                            "Neither the gcm.provider or the fractal.provider system properties are defined");
                    } else {
                        throw ie2;
                    }
                }
            } else {
                throw ie;
            }
        }
        return bootstrapComponent;
    }

    public static Component getBootstrapComponent(final Map<?, ?> hints) throws InstantiationException {
        Component bootstrapComponent;
        try {
            bootstrapComponent = GCM.getBootstrapComponent(hints);
        } catch (InstantiationException ie) {
            if (System.getProperty("gcm.provider") == null) {
                try {
                    bootstrapComponent = Fractal.getBootstrapComponent(hints);
                } catch (InstantiationException ie2) {
                    if (System.getProperty("fractal.provider") == null) {
                        throw new InstantiationException(
                            "Neither the gcm.provider or the fractal.provider system properties are defined");
                    } else {
                        throw ie2;
                    }
                }
            } else {
                throw ie;
            }
        }
        return bootstrapComponent;
    }

    /**
     * @return null if clientItfName does not begin with the name of a collection interface,
     *         the name of the collection interface otherwise
     */
    public static String pertainsToACollectionInterface(String clientItfName, Component owner) {
        InterfaceType[] itfTypes = (((ComponentType) owner.getFcType()).getFcInterfaceTypes());
        for (int i = 0; i < itfTypes.length; i++) {
            if (itfTypes[i].isFcCollectionItf()) {
                if (clientItfName.startsWith(itfTypes[i].getFcItfName())) {
                    return itfTypes[i].getFcItfName();
                }
            }
        }
        return null;
    }

    public static boolean hasSingleCardinality(String itfName, Component owner) {
        Iterator<Interface> it = Arrays.<Interface> asList((Interface[]) owner.getFcInterfaces()).iterator();
        while (it.hasNext()) {
            PAGCMInterfaceType itfType = (PAGCMInterfaceType) it.next().getFcItfType();
            if (itfType.getFcItfName().equals(itfName) && itfType.isGCMSingletonItf()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMulticastItf(String itfName, Component owner) {
        try {
            return GCMTypeFactory.MULTICAST_CARDINALITY.equals(getCardinality(itfName, owner));
        } catch (NoSuchInterfaceException e) {
            return false;
        }
    }

    public static boolean isGathercastItf(Interface itf) {
        if (!(itf instanceof PAInterface)) {
            return false;
        }
        return ((GCMInterfaceType) itf.getFcItfType()).isGCMGathercastItf();
    }

    public static boolean isSingletonItf(String itfName, Component owner) {
        try {
            return GCMTypeFactory.SINGLETON_CARDINALITY.equals(getCardinality(itfName, owner));
        } catch (NoSuchInterfaceException e) {
            return false;
        }
    }

    public static String getCardinality(String itfName, Component owner) throws NoSuchInterfaceException {
        InterfaceType[] itfTypes = ((ComponentType) owner.getFcType()).getFcInterfaceTypes();

        for (InterfaceType type : itfTypes) {
            if (type.getFcItfName().equals(itfName)) {
                return ((GCMInterfaceType) type).getGCMCardinality();
            }
        }
        throw new NoSuchInterfaceException(itfName);
    }

    public static String getMethodSignatureWithoutReturnTypeAndModifiers(Method m) {
        String result = m.toString();
        result = result.substring(result.indexOf(m.getName()));
        return result;
    }

    /**
     * Check whether a component interface name match a controller interface. According to the 
     * Fractal specification a controller interface name is either "component" or ends with
     * "-component". 
     * 
     * @param itfName an interface name
     * @return true if it's a controller interface name
     */
    public static boolean isControllerInterfaceName(String itfName) {
        // according to Fractal spec v2.0 , section 4.1
        return ((itfName != null) && (itfName.endsWith("-controller") || itfName.equals(Constants.COMPONENT)));
    }
}
