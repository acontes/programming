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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.objectweb.proactive.extensions.sca.adl.xml.parsingTools;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A fractal Object which correspond to SCA xml compisite file
 */
public class SCACompositeXMLObject {
    // GCMHeader of proactive fractal XML

    public final String GCMHEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> \n"
            + "<!DOCTYPE definition PUBLIC \"-//objectweb.org//DTD Fractal ADL 2.0//EN\" \"classpath://org/objectweb/proactive/core/component/adl/xml/proactive.dtd\">\n";
    protected String compositeName;
    protected List<ServiceTag> services;
    protected List<ComponentTag> components;
    protected List<String[]> wired;
    protected List<IntentComposite> intents;
    protected Document outPutdocument;

    /**
     * Get the componentTag object out of the list of component with its name
     */
    private ComponentTag getComponentByName(String name) {
        for (ComponentTag componentTag : components) {
            if (componentTag.ComponentName.equals(name)) {
                return componentTag;
            }
        }
        return null;
    }

    /**
     * Get the serviceTag object out of the list of component with its name
     */
    private ServiceTag getServiceByName(String name) {
        for (ServiceTag serviceTag : services) {
            if (serviceTag.serviceName.equals(name)) {
                return serviceTag;
            }
        }
        return null;
    }

    public SCACompositeXMLObject() {
        initDocument();
        services = new ArrayList<ServiceTag>();
        components = new ArrayList<ComponentTag>();
        wired = new ArrayList<String[]>();
        intents = new ArrayList<IntentComposite>();
    }

    // revisit service tag out of component tag
    protected void revistServicesTags() {
        for (ServiceTag serviceTag : services) {
            String softLink = serviceTag.getSoftLink(); // Parse promote;
            String[] tmp = softLink.split("/");
            ComponentTag linkedComponent = getComponentByName(tmp[0]);
            if (linkedComponent != null) { // found corresponded component
                ServiceTag linkedService = linkedComponent.getServiceByName(tmp[1]);
                if (linkedService != null) { // found corresponded Service
                    serviceTag.setImplementation(linkedService.getImplementation()); // set them as the same interface implementation
                    serviceTag.setReferenceComponentName(linkedService.getReferenceComponentName());
                } else {
                    try {
                        // need to look into content class of component if there's a interface which correspond
                        // hmm can use @service to find out as well!
                        Class contentClass = Class.forName(linkedComponent.getContentClassName());
                        List<Class> superInterfaces = new ArrayList<Class>();
                        Class tmpClass = contentClass;
                        while (!tmpClass.getName().equals(Object.class.getName())) {
                            superInterfaces.addAll(new ArrayList<Class>(Arrays.asList(tmpClass.getInterfaces())));
                            tmpClass = tmpClass.getSuperclass();
                        }
                        Class linkedItf = null;
                        for (Class itf : superInterfaces) {
                            if (itf.getSimpleName().equals(tmp[1])) { // correponded interface found!
                                linkedItf = itf;
                                break;
                            }
                        }
                        if (linkedItf != null) {
                            serviceTag.setImplementation(linkedItf.getName());
                            serviceTag.setReferenceComponentName(linkedComponent.getComponentName());
                            // add new service on the component
                            linkedComponent.getServices().add(new ServiceTag(serviceTag.getServiceName(),
                                    serviceTag.getRole(), "", linkedItf.getName()));
                        } else { // nothing can be found , must be some error in XML
                            System.err.println("Nothing corresponding can't be found with service tag : " + serviceTag.getServiceName() + ""
                                    + "there must be some error with composite XML");
                        }
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(SCACompositeXMLObject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                System.err.println("The service : " + serviceTag.getServiceName() + " can not find correspond subComponent"
                        + ": " + tmp[0]);
            }
        }
    }
    // revisit service tag out of Wired tag

    protected void revisitWiredTag() {
        // parse service tags in composite, to add bindings
        for (ServiceTag serviceTag : services) {
            String[] tmp = new String[2];
            tmp[0] = "this." + serviceTag.getServiceName();
            tmp[1] = serviceTag.getReferenceComponentName() + "." + serviceTag.getServiceName();
            wired.add(tmp);
        }
    }
    // revisit XML object to finalize the object construction

    public void revisitConstructedObject() {
        // revisit service tag out of component tag
        revistServicesTags();
        revisitWiredTag();
    }

    /**
     * Get Fractal XML output
     * @return Fractal XML output
     */
    public String toXml() {
        Element rootEle = outPutdocument.createElement(FRACTAL_DEFINITION_TAG);
        rootEle.setAttribute(FRACTAL_NAME_ATTRIBUTE_OF_DEFINITION_TAG, compositeName);
        outPutdocument.appendChild(rootEle);
        for (ServiceTag service : services) {
            rootEle.appendChild(service.toXmlElement(outPutdocument));
        }
        for (ComponentTag component : components) {
            rootEle.appendChild(component.toXmlElement(outPutdocument));
        }
        for (String[] binding : wired) {
            Element tmp = outPutdocument.createElement(FRACTAL_BINDING_TAG);
            tmp.setAttribute(FRACTAL_CLIENT_ATTRIBUTE_OF_BINDING_TAG, binding[0]);
            tmp.setAttribute(FRACTAL_SERVER_ATTRIBUTE_OF_BINDING_TAG, binding[1]);
            rootEle.appendChild(tmp);
        }
        Element prim = outPutdocument.createElement(ComponentTag.FRACTAL_CONTROLLER_TAG);
        prim.setAttribute(ComponentTag.FRACTAL_DESC_ATTRIBUTE_OF_CONTROLLER_TAG, "composite");
        rootEle.appendChild(prim);
        return GCMHEADER + getOutPutDoc();
    }

    /**
     * initialize output document
     */
    private void initDocument() {
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            outPutdocument = docBuilder.newDocument();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SCACompositeXMLObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get the out put document in string form
     */
    protected String getOutPutDoc() {
        String xmlString = null;
        try {
            //Output the XML
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(outPutdocument);
            trans.transform(source, result);
            xmlString = sw.toString();
        } catch (Exception ex) {
            Logger.getLogger(SCACompositeXMLObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlString;
    }

    /**
     * Get the intents values in the the SCA composite file 
     */
    public List<String[]> getIntents() {
        List<String[]> res = new ArrayList<String[]>();
        for (IntentComposite intentComposite : intents) {
            String[] tmp = new String[3];
            tmp[0] = intentComposite.applicatedComponentName;
            tmp[1] = intentComposite.applicatedServiceName;
            tmp[2] = intentComposite.implementation;
            res.add(tmp);
        }
        return res;
    }

    /**
     * Get the properties values in the the SCA composite file 
     */
    public List<String[]> getProperties() {
        List<String[]> properties = new ArrayList<String[]>();
        for (ComponentTag component : components) {
            properties.addAll(component.getProperties());
        }
        return properties;
    }
    /**
     * Get the last Component of the list of component
     * @return 
     */
    public ComponentTag getLastComponent() {
        return components.get(components.size() - 1);
    }
    /**
     * Get the last serviceTag of the list of ServiceTag
     * @return 
     */
    public ServiceTag getLastService() {
        return services.get(services.size() - 1);
    }

    /*******************************************************************************
     *          Generated getters and setters
     *******************************************************************************/
    public List<ComponentTag> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentTag> components) {
        this.components = components;
    }

    public String getCompositeName() {
        return compositeName;
    }

    public void setCompositeName(String compositeName) {
        this.compositeName = compositeName;
    }

    public Document getOutPutdocument() {
        return outPutdocument;
    }

    public void setOutPutdocument(Document outPutdocument) {
        this.outPutdocument = outPutdocument;
    }

    public List<ServiceTag> getServices() {
        return services;
    }

    public void setServices(List<ServiceTag> services) {
        this.services = services;
    }

    public List<String[]> getWired() {
        return wired;
    }

    public void setWired(List<String[]> wired) {
        this.wired = wired;
    }
    //Fractal tag and attribute name Constants
    public static final String FRACTAL_DEFINITION_TAG = "definition";
    public static final String FRACTAL_NAME_ATTRIBUTE_OF_DEFINITION_TAG = "name";
    public static final String FRACTAL_BINDING_TAG = "binding";
    public static final String FRACTAL_CLIENT_ATTRIBUTE_OF_BINDING_TAG = "client";
    public static final String FRACTAL_SERVER_ATTRIBUTE_OF_BINDING_TAG = "server";
}
