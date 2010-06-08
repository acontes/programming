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
package org.objectweb.proactive.extensions.component.sca;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.component.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.extensions.component.sca.gen.PropertyClassGenerator;


/**
 * This class is used for creating SCA components. It acts as :
 * <ol>
 * <li> a bootstrap component</li>
 * <li> a specialized GenericFactory for instantiating new SCA components on remote nodes ({@link PAGenericFactory})</li>
 * <li> a utility class providing static methods to create collective interfaces</li>
 * </ol>
 *
 * @author The ProActive Team
 */
@PublicAPI
public class SCAFractive extends Fractive {
    private static SCAFractive instance = null;
    public static final String DEFAULT_SCACOMPONENT_CONFIG_FILE_LOCATION = "/org/objectweb/proactive/extensions/component/sca/config/default-component-config.xml";

    /**
     * no-arg constructor (used by Fractal to get a bootstrap component)
     */
    public SCAFractive() {
    }

    /**
     * Returns singleton
     *
     * @return SCAFractive a singleton
     */
    private static SCAFractive instance() {
        if (instance == null) {
            instance = new SCAFractive();
        }
        return instance;
    }

    public Component newFcInstance(Type type, ControllerDescription controllerDesc,
            ContentDescription contentDesc, Node node) throws InstantiationException {
        ControllerDescription newControllerDesc = new ControllerDescription(controllerDesc.getName(),
            controllerDesc.getHierarchicalType(), DEFAULT_SCACOMPONENT_CONFIG_FILE_LOCATION);
        if (newControllerDesc.getHierarchicalType().equals(Constants.PRIMITIVE)) {
            String className = contentDesc.getClassName();
            System.err.println("DEBUGG "+ className);
            // Test whether the component class has a property annotation
            boolean hasSCAProperty = true;
            if (hasSCAProperty) {
                try {
                    contentDesc.setClassName(PropertyClassGenerator.instance().generateClass(className));
                } catch (ClassGenerationFailedException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.newFcInstance(type, newControllerDesc, contentDesc, node);
    }
}
