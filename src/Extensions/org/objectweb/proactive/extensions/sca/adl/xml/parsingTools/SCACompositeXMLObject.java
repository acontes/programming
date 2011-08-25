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
import java.lang.annotation.Annotation;
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
import org.objectweb.proactive.extensions.sca.exceptions.SCAXMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * A fractal Object which correspond to SCA xml compisite file
 */
public class SCACompositeXMLObject {
    // GCMHeader of proactive fractal XML

    public final String GCMHEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> \n"
        + "<!DOCTYPE definition PUBLIC \"-//objectweb.org//DTD Fractal ADL 2.0//EN\" \"classpath://org/objectweb/proactive/core/component/adl/xml/proactive.dtd\">\n";
    protected String compositeName;
    protected List<ExposedInterface> exposedInterfaces;
    protected List<ComponentTag> components;
    protected List<WireTag> wires;
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

    public SCACompositeXMLObject() {
        initDocument();
        exposedInterfaces = new ArrayList<ExposedInterface>();
        components = new ArrayList<ComponentTag>();
        wires = new ArrayList<WireTag>();
        wired = new ArrayList<String[]>();
        intents = new ArrayList<IntentComposite>();
    }

    // revisit service tag out of component tag
    protected void revistExposedInterfaces() throws SCAXMLException {
        for (ExposedInterface exposedInterface : exposedInterfaces) {
            String softLink = exposedInterface.getSoftLink(); // Parse promote;
            String[] tmp = softLink.split("/");
            ComponentTag linkedComponent = getComponentByName(tmp[0]);
            if (linkedComponent != null) { // found corresponded component
                ExposedInterface linkedService = linkedComponent.getExposedInterfaceByName(tmp[1]);
                if (linkedService != null) { // found corresponded Service
                    exposedInterface.setImplementation(linkedService.getImplementation()); // set them as the same interface implementation
                    exposedInterface.setReferenceComponentName(linkedService.getReferenceComponentName());
                } else {
                    findServiceTagImplementation(linkedComponent.ComponentName, tmp[1], exposedInterface);
                    linkedComponent.getExposedInterface().add(
                            new ExposedInterface(exposedInterface.getExposedInterfaceName(), exposedInterface
                                    .getRole(), "", exposedInterface.implementation));
                }
            } else {
                throw new SCAXMLException("The service : " + exposedInterface.getExposedInterfaceName() +
                    " can not find correspond subComponent" + ": " + tmp[0]);
            }
        }
    }

    // revisit service tag out of Wired tag
    protected void revisitWireTag() throws SCAXMLException {
        // parse service tags in composite, to add bindings
        for (ExposedInterface ExposedInterface : exposedInterfaces) {
            String[] tmp = new String[2];
            tmp[0] = "this." + ExposedInterface.getExposedInterfaceName();
            tmp[1] = ExposedInterface.getReferenceComponentName() + "." +
                ExposedInterface.getExposedInterfaceName();
            wired.add(tmp);
        }
        for (WireTag wireTag : wires) {
            String source = wireTag.source;
            String target = wireTag.target;
            boolean serviceTagCompleted = exposedItfCompleted(target, "server");
            boolean referenceTagCompleted = false;
            if (serviceTagCompleted) {
                referenceTagCompleted = exposedItfCompleted(source, "client");
                if (!referenceTagCompleted) {
                    String[] tmp1 = target.split("/");
                    String[] tmp2 = source.split("/");
                    ExposedInterface stag = getComponentByName(tmp1[0]).getExposedInterfaceByName(tmp1[1]);
                    ExposedInterface rtag = getComponentByName(tmp2[0]).getExposedInterfaceByName(tmp2[1]);
                    rtag.implementation = stag.implementation;
                    referenceTagCompleted = true;
                }
            }
            if (serviceTagCompleted && referenceTagCompleted) {
                String[] tmp = new String[2];
                tmp[0] = source.replace("/", ".");
                tmp[1] = target.replace("/", ".");
                wired.add(tmp);
            }
        }
    }

    private void findServiceTagImplementation(String componentName, String serviceName,
            ExposedInterface exposedInterface) throws SCAXMLException {
        ComponentTag comptag = getComponentByName(componentName);
        try {
            // need to look into content class of component if there's a interface which correspond
            // hmm can use @service to find out as well!
            Class contentClass = Class.forName(comptag.getContentClassName());
            //            org.osoa.sca.annotations.Service anno = (org.osoa.sca.annotations.Service) contentClass.getAnnotation(org.osoa.sca.annotations.Service.class);
            //            Class cls = anno.value();
            //            System.err.println("debugggg=============== "+cls.getName());
            ////            for (Class class1 : cls) {
            ////                System.err.println("debugggg=============== "+class1.getName());
            ////            }
            List<Class> superInterfaces = new ArrayList<Class>();
            Class tmpClass = contentClass;
            while (!tmpClass.getName().equals(Object.class.getName())) {
                superInterfaces.addAll(new ArrayList<Class>(Arrays.asList(tmpClass.getInterfaces())));
                tmpClass = tmpClass.getSuperclass();
            }
            Class linkedItf = null;
            for (Class itf : superInterfaces) {
                if (itf.getSimpleName().equals(serviceName)) { // correponded interface found!
                    linkedItf = itf;
                    break;
                }
            }
            if (linkedItf != null) {
                exposedInterface.setImplementation(linkedItf.getName());
                exposedInterface.setReferenceComponentName(componentName);
            } else { // nothing can be found , must be some error in XML
                throw new SCAXMLException("Nothing corresponding can't be found with service tag : " +
                    exposedInterface.getExposedInterfaceName() + "" +
                    "there must be some error with composite XML");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SCACompositeXMLObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get Fractal binding tag value from sca wire tag
     */
    private boolean exposedItfCompleted(String sourceOrTarget, String role) throws SCAXMLException {
        String[] itfSplit = sourceOrTarget.split("/");
        boolean exposedItfGenerated = false;
        ComponentTag linkedTargetComponent = getComponentByName(itfSplit[0]);
        if (linkedTargetComponent != null) {
            ExposedInterface linkedService = linkedTargetComponent.getExposedInterfaceByName(itfSplit[1]);
            if (linkedService == null) { // create Service or reference
                //System.err.println("DEBIGGG ======= "+sourceOrTarget);
                ExposedInterface exItf = new ExposedInterface(itfSplit[1], role, "", "");
                if (role.equals("server")) // look for service annotation in server implementation or implemented itf
                {
                    findServiceTagImplementation(itfSplit[0], itfSplit[1], exItf);
                    exposedItfGenerated = true;
                }
                linkedTargetComponent.exposedInterfaces.add(exItf);
            } else { // found corresponded Service
                exposedItfGenerated = true;
            }
        }
        return exposedItfGenerated;
    }

    // revisit XML object to finalize the object construction
    public void revisitConstructedObject() throws SCAXMLException {
        // revisit service tag out of component tag
        revistExposedInterfaces();
        revisitWireTag();
    }

    /**
     * Get Fractal XML output
     * @return Fractal XML output
     */
    public String toXml() {
        Element rootEle = outPutdocument.createElement(FRACTAL_DEFINITION_TAG);
        rootEle.setAttribute(FRACTAL_NAME_ATTRIBUTE_OF_DEFINITION_TAG, compositeName);
        outPutdocument.appendChild(rootEle);
        if (components.size() == 1) { // primitive
            ComponentTag comp = components.get(0);
            for (ExposedInterface service : exposedInterfaces) {
                Element tmp = service.toXmlElement(outPutdocument);
                if (tmp != null) {
                    rootEle.appendChild(tmp);
                }
            }
            Element content = outPutdocument.createElement(ComponentTag.FRACTAL_CONTENT_TAG);
            content.setAttribute(ComponentTag.CLASS_ATTRIBUTE_OF_CONTENT_TAG, comp.contentClassName);
            Element controller = outPutdocument.createElement(ComponentTag.FRACTAL_CONTROLLER_TAG);
            controller.setAttribute(ComponentTag.FRACTAL_DESC_ATTRIBUTE_OF_CONTROLLER_TAG, "primitive");
            rootEle.appendChild(content);
            rootEle.appendChild(controller);
        } else { // composite
            for (ExposedInterface service : exposedInterfaces) {
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
        }
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
     * Get the last ExposedInterface of the list of ExposedInterface
     * @return 
     */
    public ExposedInterface getLastExposedInterface() {
        return exposedInterfaces.get(exposedInterfaces.size() - 1);
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

    public List<ExposedInterface> getExposedInterface() {
        return exposedInterfaces;
    }

    public void setExposedInterface(List<ExposedInterface> services) {
        this.exposedInterfaces = services;
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
