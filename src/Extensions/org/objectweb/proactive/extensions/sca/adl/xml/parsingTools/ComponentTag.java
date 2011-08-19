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

package org.objectweb.proactive.extensions.sca.adl.xml.parsingTools;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * The Object which correspond to the content of component tag 
 * @author mug
 */
public class ComponentTag {

    protected String ComponentName;
    protected String contentClassName;
    protected List<ExposedInterface> exposedInterfaces;
    protected List<String[]> properties;

    public ComponentTag() {
        exposedInterfaces = new ArrayList<ExposedInterface>();
        properties = new ArrayList<String[]>();
    }

    /**
     * Get the last ExposedInterface of the list of ExposedInterface
     * @return 
     */
    public ExposedInterface getLastExposedInterface() {
        return exposedInterfaces.get(exposedInterfaces.size() - 1);
    }

    /**
     * Get Fractal XML Element output of the component
     */
    public Element toXmlElement(Document doc) {

        Element rootEle = doc.createElement(FRACTAL_COMPONENT_TAG);
        rootEle.setAttribute(FRACTAL_NAME_ATTRIBUTE_OF_COMPONENT_TAG, ComponentName);
        for (ExposedInterface service : exposedInterfaces) {
            Element tmp = service.toXmlElement(doc);
            if (tmp != null) {
                rootEle.appendChild(tmp);
            }
        }
        Element content = doc.createElement(FRACTAL_CONTENT_TAG);
        content.setAttribute(CLASS_ATTRIBUTE_OF_CONTENT_TAG, contentClassName);
        Element controller = doc.createElement(FRACTAL_CONTROLLER_TAG);
        controller.setAttribute(FRACTAL_DESC_ATTRIBUTE_OF_CONTROLLER_TAG, "primitive");
        rootEle.appendChild(content);
        rootEle.appendChild(controller);
        return rootEle;
    }

    /**
     * Get the ExposedInterface in the list of ExposedInterfaces with the name 
      */
    public ExposedInterface getExposedInterfaceByName(String name) {
        for (ExposedInterface ExposedInterface : exposedInterfaces) {
            if (ExposedInterface.exposedInterfaceName.equals(name)) {
                return ExposedInterface;
            }
        }
        return null;
    }

    /*******************************************************************************
     *          Generated getters and setters
     *******************************************************************************/
    public String getComponentName() {
        return ComponentName;
    }

    public void setComponentName(String ComponentName) {
        this.ComponentName = ComponentName;
    }

    public String getContentClassName() {
        return contentClassName;
    }

    public void setContentClassName(String contentClassName) {
        this.contentClassName = contentClassName;
    }

    public List<String[]> getProperties() {
        return properties;
    }

    public void setProperties(List<String[]> properties) {
        this.properties = properties;
    }

    public List<ExposedInterface> getExposedInterface() {
        return exposedInterfaces;
    }

    public void setExposedInterface(List<ExposedInterface> services) {
        this.exposedInterfaces = services;
    }

    //Fractal tag and attribute name Constants
    public static final String FRACTAL_COMPONENT_TAG = "component";
    public static final String FRACTAL_NAME_ATTRIBUTE_OF_COMPONENT_TAG = "name";

    public static final String FRACTAL_CONTENT_TAG = "content";
    public static final String CLASS_ATTRIBUTE_OF_CONTENT_TAG = "class";

    public static final String FRACTAL_CONTROLLER_TAG = "controller";
    public static final String FRACTAL_DESC_ATTRIBUTE_OF_CONTROLLER_TAG = "desc";

}