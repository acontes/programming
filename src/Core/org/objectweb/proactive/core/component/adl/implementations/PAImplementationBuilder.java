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
package org.objectweb.proactive.core.component.adl.implementations;

import java.util.Map;

import org.objectweb.fractal.adl.implementations.ImplementationBuilder;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.adl.nodes.VirtualNode;


/**
 * @author The ProActive Team
 */
public interface PAImplementationBuilder extends ImplementationBuilder {

    /**
     * Allows the creation of a ProActive component on a given virtual node
     * @param type the type of the component
     * @param name the name of the component
     * @param definition the definition of the component
     * @param controllerDesc the description of the controller
     * @param contentDesc the description of the content
     * @param adlVN the virtual node where the component should be deployed
     * @param context context
     * @return an instance of the specified component (or a group of instances if the virtual node is
     * a multiple one)
     * @throws Exception if the creation of the component failed
     */
    public Object createComponent(Object type, String name, String definition,
            ControllerDescription controllerDesc, ContentDescription contentDesc, VirtualNode adlVN,
            Map<Object, Object> context) throws Exception;
}