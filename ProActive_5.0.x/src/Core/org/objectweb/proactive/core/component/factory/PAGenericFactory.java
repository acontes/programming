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
package org.objectweb.proactive.core.component.factory;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.node.Node;


/**
 * A factory for instantiating components on remote nodes.
 *
 * @author The ProActive Team
 */
@PublicAPI
public interface PAGenericFactory extends GenericFactory {

    /**
     * Creates a component.
     *
     * @param type
     *            an arbitrary component type.
     * @param controllerDesc
     *            a description of the controller part of the component to be
     *            created. This description is implementation specific. If it is
     *            <tt>null</tt> then a "default" controller part will be used.
     * @param contentDesc
     *            a description of the content part of the component to be
     *            created. This description is implementation specific.
     * @return the {@link Component} interface of the created component.
     * @throws InstantiationException
     *             if the component cannot be created.
     */
    Component newFcInstance(Type type, ControllerDescription controllerDesc, ContentDescription contentDesc)
            throws InstantiationException;

    /**
     * Creates a functional component
     * @param type the functional type of the component
     * @param nfType the non-functional type of the component
     * @param contentDesc
     *            a description of the controller part of the component to be
     *            created. This description is implementation specific. If it is
     *            <tt>null</tt> then a "default" controller part will be used.
     * @param controllerDesc
     *            description of the content part of the component to be
     *            created. This description is implementation specific.
     * @param node the node where to create the component
     * @return the {@link Component} interface of the created component.
     * @throws InstantiationException if the component cannot be created.
     */
    public Component newFcInstance(Type type, Type nfType, ContentDescription contentDesc,
            ControllerDescription controllerDesc, Node node) throws InstantiationException;

    /**
     *  Creates a non-functional component
     * @param type  an arbitrary component type.
     * @param controllerDesc
     *                           a description of the controller part of the component to be
     *            created. This description is implementation specific. If it is
     *            <tt>null</tt> then a "default" controller part will be used.
     * @param contentDesc
     *           a description of the content part of the component to be
     *            created. This description is implementation specific.
     * @return the {@link Component} interface of the created component.
     * @throws InstantiationException
     *                                                 if the component cannot be created.
     */
    Component newNFcInstance(Type type, ControllerDescription controllerDesc, ContentDescription contentDesc)
            throws InstantiationException;

    /**
     * Creates a component on a given node.
     *
     * @param type
     *            an arbitrary component type.
     * @param controllerDesc
     *            a description of the controller part of the component to be
     *            created. This description is implementation specific. If it is
     *            <tt>null</tt> then a "default" controller part will be used.
     * @param contentDesc
     *            a description of the content part of the component to be
     *            created. This description is implementation specific.
     * @param node
     *            the node where to create the component
     * @return the {@link Component} interface of the created component.
     * @throws InstantiationException
     *             if the component cannot be created.
     */
    Component newFcInstance(Type type, ControllerDescription controllerDesc, ContentDescription contentDesc,
            Node node) throws InstantiationException;

    /**
     * Creates a non-functional component on a given node.
     * @param type
     *         an arbitrary component type.
     * @param controllerDesc
     *                 a description of the controller part of the component to be
     *            created. This description is implementation specific. If it is
     *            <tt>null</tt> then a "default" controller part will be used.
     * @param contentDesc
     *                         a description of the content part of the component to be
     *            created. This description is implementation specific.
     * @param node
     *                 the node where to create the component
     * @return the {@link Component} interface of the created component.
     * @throws InstantiationException
     *         if the component cannot be created.
     */
    Component newNFcInstance(Type type, ControllerDescription controllerDesc, ContentDescription contentDesc,
            Node node) throws InstantiationException;
}
