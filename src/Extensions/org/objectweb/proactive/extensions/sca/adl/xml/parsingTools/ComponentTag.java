/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.objectweb.proactive.extensions.sca.adl.xml.parsingTools;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ComponentTag {

    protected String ComponentName;
    protected String contentClassName;
    protected List<ServiceTag> services;
    protected List<String[]> properties;

    public ComponentTag() {
        super();
        services = new ArrayList<ServiceTag>();
        properties = new ArrayList<String[]>();
    }

    public ServiceTag getLastService() {
        return services.get(services.size() - 1);
    }

    /**
     * Get Fractal XML output
     * @return Fractal XML output
     */
    public Element toXmlElement(Document doc) {

        Element rootEle = doc.createElement("component");
        rootEle.setAttribute("name", ComponentName);
        for (ServiceTag service : services) {
            Element tmp = service.toXmlElement(doc);
            if (tmp != null) {
                rootEle.appendChild(tmp);
            }
        }
        Element content = doc.createElement("content");
        content.setAttribute("class", contentClassName);
        Element controller = doc.createElement("controller");
        controller.setAttribute("desc", "primitive");
        rootEle.appendChild(content);
        rootEle.appendChild(controller);
        return rootEle;
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

    public List<ServiceTag> getServices() {
        return services;
    }

    public void setServices(List<ServiceTag> services) {
        this.services = services;
    }
}