/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2004 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
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
package org.objectweb.proactive.core.group;

import org.apache.log4j.Logger;

import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.InterfaceType;

import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.representative.ProActiveComponentRepresentativeFactory;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.mop.ConstructionOfProxyObjectFailedException;
import org.objectweb.proactive.core.mop.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.mop.InvalidProxyClassException;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.StubObject;


/**
 *
 *  // TODO : change class name (interfaces only are grouped)
 *
 * A class for creating groups of interfaces
 * Indeed, the standard mechanism cannot be used here, as we are referencing components
 * through interfaces of component representatives.
 *
 *  It was moved to this package so it can see className attribute in ProxyForGroup
 *
 * @author Matthieu Morel
 */
public class ProActiveComponentGroup {
    protected static Logger logger = Logger.getLogger(ProActiveComponentGroup.class.getName());

    /** Create an object representing an empty group of components specifying the java class of the components. */
    public static Object newActiveComponentGroup(
        ComponentParameters componentParameters)
        throws ClassNotFoundException, ClassNotReifiableException {
        Object result = null;

        try {
            result = MOP.newInstance(ProActiveInterface.class.getName(), null,
                    ProActiveGroup.DEFAULT_PROXYFORGROUP_CLASS_NAME,
                    new Object[] { null, null, null });
        } catch (ClassNotReifiableException e) {
            System.err.println("**** ClassNotReifiableException ****");
        } catch (InvalidProxyClassException e) {
            System.err.println("**** InvalidProxyClassException ****");
        } catch (ConstructionOfProxyObjectFailedException e) {
            System.err.println(
                "**** ConstructionOfProxyObjectFailedException ****");
        } catch (ConstructionOfReifiedObjectFailedException e) {
            System.err.println(
                "**** ConstructionOfReifiedObjectFailedException ****");
        }

        ((org.objectweb.proactive.core.group.ProxyForGroup) (((StubObject) result).getProxy())).className = Interface.class.getName();

        return ProActiveComponentRepresentativeFactory.instance()
                                                      .createComponentRepresentative(componentParameters,
            ((StubObject) result).getProxy());
    }

    /**
     * creates a group proxy on a set of generated Interface objects.
     * These objects are generated according to the InterfaceType they get as a parameter
     * @param interfaceType the type of interface we need a group of Interface objects on
     * @return a group of ProActiveInterface elements
     * @throws ClassNotFoundException
     * @throws ClassNotReifiableException
     */
    public static ProActiveInterface newActiveComponentInterfaceGroup(
        InterfaceType interfaceType)
        throws ClassNotFoundException, ClassNotReifiableException {
        try {
            ComponentParameters component_parameters = new ComponentParameters(ProActiveTypeFactory.instance()
                                                                                                   .createFcType(new InterfaceType[] {
                            interfaceType
                        }), new ControllerDescription(null, null));

            Object result = null;

            result = MOP.newInstance(ProActiveInterface.class.getName(), null,
                    ProActiveGroup.DEFAULT_PROXYFORGROUP_CLASS_NAME, null);

            ProxyForGroup proxy = (org.objectweb.proactive.core.group.ProxyForGroup) ((StubObject) result).getProxy();
            proxy.className = ProActiveInterface.class.getName();

            //return a reference on the generated interface reference corresponding to the interface type 
            return (ProActiveInterface) (ProActiveComponentRepresentativeFactory.instance()
                                                                                .createComponentRepresentative(component_parameters,
                proxy)).getFcInterface(interfaceType.getFcItfName());
        } catch (InvalidProxyClassException e) {
            logger.error("**** InvalidProxyClassException ****");
        } catch (ConstructionOfProxyObjectFailedException e) {
            logger.error("**** ConstructionOfProxyObjectFailedException ****");
        } catch (ConstructionOfReifiedObjectFailedException e) {
            logger.error("**** ConstructionOfReifiedObjectFailedException ****");
        } catch (NoSuchInterfaceException e) {
            logger.error("**** Interface not found **** " + e.getMessage());
        } catch (InstantiationException e) {
            logger.error("**** Cannot create component type **** " +
                e.getMessage());
        }
        return null;
    }

    ///** Create an object representing a group and create members with params cycling on nodeList. */
    // ComponentBody Parameters is unique for all the group members (notably the name is the same)...
    //	/**
    // jem3D stuff - to be committed later.
    //	 * creates a group
    //	 * @param className
    //	 * @param constructorsParameters
    //	 * @param nodeList
    //	 * @param componentParameters
    //	 * @return
    //	 * @throws ClassNotFoundException
    //	 * @throws ClassNotReifiableException
    //	 * @throws ActiveObjectCreationException
    //	 * @throws NodeException
    //	 */
    //    public static Object newActiveComponentGroupBuildWithMultithreading(
    //        String className, Object[][] constructorsParameters, String[] nodeList,
    //        ComponentParameters componentParameters)
    //        throws ClassNotFoundException, ClassNotReifiableException, 
    //            ActiveObjectCreationException, NodeException {
    //        Object result = ProActiveGroup.newGroup(Component.class.getName());
    //        ProxyForGroup proxy = (org.objectweb.proactive.core.group.ProxyForGroup) ProActiveGroup.getGroup(result);
    //
    //        proxy.createComponentMemberWithMultithread(className,
    //            constructorsParameters, nodeList, componentParameters);
    //
    //        return result;
    //    }
}
