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
package org.objectweb.proactive.extensions.sca.adl.implementations;

import org.objectweb.proactive.core.component.adl.implementations.*;
import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription; //import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.extensions.sca.Utils;
import org.objectweb.proactive.core.component.adl.nodes.ADLNodeProvider;
import org.objectweb.proactive.core.component.adl.nodes.VirtualNode;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;


/**
 * @author The ProActive Team
 */
public class SCAPAImplementationBuilderImpl extends PAImplementationBuilderImpl {

    protected ObjectsContainer commonCreation(Object type, String name, String definition,
            ContentDescription contentDesc, VirtualNode adlVN, Map<Object, Object> context) throws Exception {
        ObjectsContainer res = super.commonCreation(type, name, definition, contentDesc, adlVN, context);
        SCAObjectsContainer newRes = new SCAObjectsContainer(res.getNodesContainer(), res
                .getBootstrapComponent());
        Component bootstrap = null;
        if (context != null) {
            newRes.bootstrap = (Component) context.get("bootstrap");
        }
        if (bootstrap == null) {
            newRes.bootstrap = Utils.getBootstrapComponent();
        }
        return newRes;

    }

    protected class SCAObjectsContainer extends PAImplementationBuilderImpl.ObjectsContainer {
        private Object nodesContainer;
        protected Component bootstrap;

        public SCAObjectsContainer(Object nodesProvider, Component bootstrap) {
            super(nodesProvider, bootstrap);
        }

        public Component createFComponent(ComponentType type, ControllerDescription controllerDesc,
                ContentDescription contentDesc, VirtualNode adlVN) throws Exception {
            PAGenericFactory gf = Utils.getPAGenericFactory(bootstrap);
            Component result = gf.newFcInstance(type, controllerDesc, contentDesc, ADLNodeProvider
                    .getNode(nodesContainer));
            return result;
        }
    }
    // --------------------------------------------------------------------------

}
