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
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.sca.control.SCAPropertyController;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.extensions.sca.gen.BusinessClassWithAuthentificationGenerator;
import org.objectweb.proactive.extensions.sca.gen.IntentClassGenerator;
import org.objectweb.proactive.extensions.sca.gen.PropertyClassGenerator;
import org.objectweb.proactive.extensions.sca.gen.SCAReferenceGenerator;
import org.objectweb.proactive.extensions.sca.intentpolicies.authentification.AuthentificationItf;


/**
 * This class is used for creating SCA/GCM components. It acts as :
 * <ol>
 * <li> a bootstrap component</li>
 * <li> a specialized GenericFactory for instantiating new SCA/GCM components on remote nodes ({@link PAGenericFactory})</li>
 * </ol>
 *
 * @author The ProActive Team
 */
@PublicAPI
public class SCAFractive extends Fractive {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS);
    public static final String DEFAULT_SCACOMPONENT_CONFIG_FILE_LOCATION = "/org/objectweb/proactive/extensions/sca/config/default-component-config.xml";

    /**
     * no-arg constructor (used by ProActive to get a bootstrap component)
     */
    public SCAFractive() {
    }

    /*
     * @see
     * org.objectweb.fractal.api.factory.GenericFactory#newFcInstance(org.objectweb.fractal.api.
     * Type, java.lang.Object, java.lang.Object)
     */
    public Component newFcInstance(Type type, Object controllerDesc, Object contentDesc)
            throws InstantiationException {
        if (controllerDesc.equals(Constants.COMPOSITE) || controllerDesc.equals(Constants.PRIMITIVE)) {
            return newFcInstance(type, new ControllerDescription(null, (String) controllerDesc,
                DEFAULT_SCACOMPONENT_CONFIG_FILE_LOCATION), contentDesc);
        } else {
            return super.newFcInstance(type, controllerDesc, contentDesc);
        }
    }

    /*
     * @see
     * org.objectweb.proactive.core.component.factory.PAGenericFactory#newNFcInstance(org.objectweb
     * .fractal.api.Type, org.objectweb.proactive.core.component.ControllerDescription,
     * org.objectweb.proactive.core.component.ContentDescription,
     * org.objectweb.proactive.core.node.Node)
     */
    public Component newFcInstance(Type type, ControllerDescription controllerDesc,
            ContentDescription contentDesc, Node node) throws InstantiationException {
        controllerDesc = new ControllerDescription(controllerDesc.getName(), controllerDesc
                .getHierarchicalType(), DEFAULT_SCACOMPONENT_CONFIG_FILE_LOCATION);
        String className = contentDesc.getClassName();
        if (controllerDesc.getHierarchicalType().equals(Constants.PRIMITIVE) &&
            controllerDesc.getControllersSignatures().containsKey(SCAPropertyController.class.getName())) {
            //String className = contentDesc.getClassName();
            String generatedClassName = className;
            try {
                generatedClassName = IntentClassGenerator.instance().generateClass(className);
                contentDesc.setClassName(generatedClassName);
            } catch (ClassGenerationFailedException cgfe) {
                InstantiationException ie = new InstantiationException(
                    "Cannot generate SCA Intent class for " + className + " : " + cgfe.getMessage());
                ie.initCause(cgfe);
                throw ie;
            }
            // Test whether the component class has a property annotation
            if (hasPropertyAnnotation(generatedClassName)) {
                try {
                    generatedClassName = PropertyClassGenerator.instance().generateClass(generatedClassName);
                    contentDesc.setClassName(generatedClassName);
                } catch (ClassGenerationFailedException cgfe) {
                    InstantiationException ie = new InstantiationException(
                        "Cannot generate SCA Property class for " + className + " : " + cgfe.getMessage());
                    ie.initCause(cgfe);
                    throw ie;
                }
            }
            if (Utils.hasReferencenAnnotation(className)) {
                try {
                    generatedClassName = SCAReferenceGenerator.instance().generateClass(generatedClassName);
                    contentDesc.setClassName(generatedClassName);
                } catch (ClassGenerationFailedException cgfe) {
                    InstantiationException ie = new InstantiationException(
                        "Cannot generate SCA Reference class for " + className + " : " + cgfe.getMessage());
                    ie.initCause(cgfe);
                    throw ie;
                }
            }
        }
        Component res = super.newFcInstance(type, controllerDesc, contentDesc, node);
        //        if (controllerDesc.getHierarchicalType().equals(Constants.PRIMITIVE)) {
        //            if (Utils.hasAuthentificationAnnotation(className)) {
        //                System.err.println("HAS AUTH:!");
        //            }
        //        }
        return res;
    }

    /*
     * Determines if a class Property annotation.
     *
     * @param className Class to introspect.
     * @return True if the given class contains Property annotation.
     * @throws InstantiationException If the class cannot be found.
     */
    private boolean hasPropertyAnnotation(String className) throws InstantiationException {
        try {
            Class<?> clazz = Class.forName(className);
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].isAnnotationPresent(org.oasisopen.sca.annotation.Property.class) ||
                    fields[i].isAnnotationPresent(org.osoa.sca.annotations.Property.class)) {
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
