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
package org.objectweb.proactive.core.component.adl.bindings;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.bindings.Binding;
import org.objectweb.fractal.adl.bindings.BindingContainer;
import org.objectweb.fractal.adl.bindings.BindingErrors;
import org.objectweb.fractal.adl.bindings.TypeBindingLoader;
import org.objectweb.fractal.adl.components.Component;
import org.objectweb.fractal.adl.components.ComponentContainer;
import org.objectweb.fractal.adl.interfaces.IDLException;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.interfaces.InterfaceContainer;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;


/**
 * @author The ProActive Team
 */
public class ProActiveTypeBindingLoader extends TypeBindingLoader {
    private boolean isMulticastBinding(final Binding binding, final Map<String, Map<String, Interface>> itfMap)
            throws ADLException {
        final String from = binding.getFrom();
        if (from == null) {
            throw new ADLException(BindingErrors.MISSING_FROM, binding);
        }
        int i = from.indexOf('.');
        if (i < 1 || i == from.length() - 1) {
            throw new ADLException(BindingErrors.INVALID_FROM_SYNTAX, binding, from);
        }
        final String fromCompName = from.substring(0, i);
        final String fromItfName = from.substring(i + 1);
        final Interface fromItf = getInterface(fromCompName, fromItfName, binding, itfMap);
        return ProActiveTypeFactory.MULTICAST_CARDINALITY.equals(((TypeInterface) fromItf).getCardinality());
    }

    @Override
    protected void checkNode(final Object node, final Map<Object, Object> context) throws ADLException {
        if (node instanceof BindingContainer) {
            final Map<String, Map<String, Interface>> itfMap = new HashMap<String, Map<String, Interface>>();
            if (node instanceof InterfaceContainer) {
                final Map<String, Interface> containerItfs = new HashMap<String, Interface>();
                for (final Interface itf : ((InterfaceContainer) node).getInterfaces()) {
                    containerItfs.put(itf.getName(), itf);
                }
                itfMap.put("this", containerItfs);
            }
            if (node instanceof ComponentContainer) {
                for (final Component comp : ((ComponentContainer) node).getComponents()) {
                    if (comp instanceof InterfaceContainer) {
                        final Map<String, Interface> compItfs = new HashMap<String, Interface>();
                        for (final Interface itf : ((InterfaceContainer) comp).getInterfaces()) {
                            compItfs.put(itf.getName(), itf);
                        }
                        itfMap.put(comp.getName(), compItfs);
                    }
                }
            }
            for (final Binding binding : ((BindingContainer) node).getBindings()) {
                checkBinding(binding, itfMap, context);
            }
            final Map<String, Binding> fromItfs = new HashMap<String, Binding>();
            for (final Binding binding : ((BindingContainer) node).getBindings()) {
                final Binding previousDefinition = fromItfs.put(binding.getFrom(), binding);
                if ((previousDefinition != null) && !isMulticastBinding(binding, itfMap)) {
                    throw new ADLException(BindingErrors.DUPLICATED_BINDING, binding, binding.getFrom(),
                        previousDefinition);
                }
            }
        }
        if (node instanceof ComponentContainer) {
            for (final Component comp : ((ComponentContainer) node).getComponents()) {
                checkNode(comp, context);
            }
        }
    }

    @Override
    protected void checkBinding(final Binding binding, final Interface fromItf, final String fromCompName,
            final String fromItfName, final Interface toItf, final String toCompName, final String toItfName,
            final Map<Object, Object> context) throws ADLException {
        try {
            super.checkBinding(binding, fromItf, fromCompName, fromItfName, toItf, toCompName, toItfName,
                    context);
        } catch (ADLException e) {
            // check if signatures are incompatible
            TypeInterface cItf = (TypeInterface) fromItf;
            TypeInterface sItf = (TypeInterface) toItf;

            try {
                //ClassLoader cl = getClassLoader(context);
                Class<?> clientSideItfClass = (Class<?>) interfaceCodeLoaderItf.loadInterface(cItf
                        .getSignature(), context);
                Class<?> serverSideItfClass = (Class<?>) interfaceCodeLoaderItf.loadInterface(sItf
                        .getSignature(), context);

                //            	Class<?> clientSideItfClass = Class<?>.forName(cItf.getSignature());
                //            	Class<?> serverSideItfClass = Class<?>.forName(sItf.getSignature());
                if (!clientSideItfClass.isAssignableFrom(serverSideItfClass)) {
                    // check if multicast interface
                    if (ProActiveTypeFactory.MULTICAST_CARDINALITY.equals(cItf.getCardinality())) {
                        Method[] clientSideItfMethods = clientSideItfClass.getMethods();
                        Method[] serverSideItfMethods = serverSideItfClass.getMethods();

                        if (clientSideItfMethods.length != serverSideItfMethods.length) {
                            throw new ADLException(
                                "incompatible binding between multicast client interface " +
                                    cItf.getName() +
                                    " (" +
                                    cItf.getSignature() +
                                    ")  and server interface " +
                                    sItf.getName() +
                                    " (" +
                                    sItf.getSignature() +
                                    ") : there is not the same number of methods (including those inherited) " +
                                    "in both interfaces !", binding);
                        }

                        //                        Map<Method, Method> matchingMethodsForThisItf = new HashMap<Method, Method>(clientSideItfMethods.length);
                        //
                        //                        for (Method method : clientSideItfMethods) {
                        //                            try {
                        ////                                matchingMethodsForThisItf
                        ////                                        .put(method, MulticastBindingChecker.searchMatchingMethod(method, serverSideItfMethods, ProActiveTypeInterface.GATHER_CARDINALITY.equals(sItf.getCardinality()), (ProActiveInterface)toItf));
                        //                                matchingMethodsForThisItf
                        //                                .put(method, MulticastBindingChecker.searchMatchingMethod(method, serverSideItfMethods, ProActiveTypeInterface.GATHER_CARDINALITY.equals(sItf.getCardinality())));
                        //                            } catch (ParameterDispatchException e1) {
                        //                                throw new ADLException("incompatible binding between multicast client interface " + cItf
                        //                                        .getName() + " (" + cItf.getSignature()
                        //                                        + ")  and server interface " + sItf.getName() + " (" + sItf
                        //                                        .getSignature() + ") : incompatible dispatch " +
                        //                                        e1.getMessage(), (Node) binding);
                        //                            } catch (NoSuchMethodException e1) {
                        //                                throw new ADLException("incompatible binding between multicast client interface " + cItf
                        //                                        .getName() + " (" + cItf.getSignature()
                        //                                        + ")  and server interface " + sItf.getName() + " (" + sItf
                        //                                        .getSignature() + ") : cannot find corresponding method " +
                        //                                        e1.getMessage(), (Node) binding);
                        //                            }
                        //                        }
                    }
                }
            } catch (IDLException e1) {
                throw new ADLException("incompatible binding between multicast client interface " +
                    cItf.getName() + " (" + cItf.getSignature() + ")  and server interface " +
                    sItf.getName() + " (" + sItf.getSignature() + ") : cannot find interface " +
                    e1.getMessage(), binding);
            }
        }
    }
}
