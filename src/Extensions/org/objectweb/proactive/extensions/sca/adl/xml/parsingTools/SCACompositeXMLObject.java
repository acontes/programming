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

    public final String GCMHeader = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> \n"
            + "<!DOCTYPE definition PUBLIC \"-//objectweb.org//DTD Fractal ADL 2.0//EN\" \"classpath://org/objectweb/proactive/core/component/adl/xml/proactive.dtd\">\n";
    protected String compositeName;
    protected List<ServiceTag> services;
    protected List<ComponentTag> components;
    protected List<String[]> properties;
    protected List<String[]> wired;
    protected Document outPutdocument;

    public ComponentTag getLastComponent() {
        return components.get(components.size() - 1);
    }

    public ServiceTag getLastService() {
        return services.get(services.size() - 1);
    }

    public SCACompositeXMLObject() {
        initDocument();
        services = new ArrayList<ServiceTag>();
        components = new ArrayList<ComponentTag>();
        properties = new ArrayList<String[]>();
        wired = new ArrayList<String[]>();
    }
    // revisit service tag out of component tag

    protected void revistServicesTags() {
        for (ServiceTag serviceTag : services) {
            String softLink = serviceTag.getSoftLink(); // Parse promote;
            String[] tmp = softLink.split("/");
            ComponentTag linkedComponent = null;
            for (ComponentTag componentTag : components) {
                if (componentTag.getComponentName().equals(tmp[0])) {
                    linkedComponent = componentTag;
                    break;
                }
            }
            if (linkedComponent != null) { // found corresponded component
                ServiceTag linkedService = null;
                for (ServiceTag serviceTag1 : linkedComponent.getServices()) {
                    if (serviceTag1.getServiceName().equalsIgnoreCase(tmp[1])) {
                        linkedService = serviceTag1;
                        break;
                    }
                }
                if (linkedService != null) { // found corresponded Service
                    serviceTag.setImplementation(linkedService.getImplementation()); // set them as the same interface implementation
                    serviceTag.setReferenceComponentName(linkedService.getReferenceComponentName());
                } else {
                    try {
                        // need to look into content class of component if there's a interface which correspond
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

    protected void revisitWiredTag() {
        // parse service tags in composite, to add bindings
        for (ServiceTag serviceTag : services) {
            String[] tmp = new String[2];
            tmp[0] = "this." + serviceTag.getServiceName();
            tmp[1] = serviceTag.getReferenceComponentName() + "." + serviceTag.getServiceName();
            wired.add(tmp);
        }
    }

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
        String res = "";
        Element rootEle = outPutdocument.createElement("definition");
        rootEle.setAttribute("name", compositeName);
        outPutdocument.appendChild(rootEle);
        for (ServiceTag service : services) {
            rootEle.appendChild(service.toXmlElement(outPutdocument));
        }
        for (ComponentTag component : components) {
            properties.addAll(component.getProperties());
            rootEle.appendChild(component.toXmlElement(outPutdocument));
        }
        for (String[] binding : wired) {
            Element tmp = outPutdocument.createElement("binding");
            tmp.setAttribute("client", binding[0]);
            tmp.setAttribute("server", binding[1]);
            rootEle.appendChild(tmp);
        }
        Element prim = outPutdocument.createElement("controller");
        prim.setAttribute("desc", "composite");
        rootEle.appendChild(prim);
        return GCMHeader + getOutPutDoc();
    }

    /**
     * init output document
     */
    protected void initDocument() {
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
            //
            Logger.getLogger(SCACompositeXMLObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlString;
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

    public List<String[]> getWired() {
        return wired;
    }

    public void setWired(List<String[]> wired) {
        this.wired = wired;
    }
}