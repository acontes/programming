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
package org.objectweb.proactive.extensions.sca.adl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.objectweb.fractal.adl.util.ClassLoaderHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * This class define how to convert a SCA composite file into Fractal file
 */
public class SCAXMLConverter extends DefaultHandler {

    private InputStream in;
    private Locator locator;
    private FractalXMLObject xmlComponent;
    private String currentPropertyName;
    private String currentPCDATA;
    private boolean constructingService = false;
    private boolean constructingComponent = false;
    private boolean constructingProperty = false;

    public SCAXMLConverter(InputStream in) {
        super();
        this.in = in;
        locator = new LocatorImpl();
        xmlComponent = new FractalXMLObject();
    }

    public Map getPropertiesValues() {
        return xmlComponent.attributes;
    }

    /**
     * Convert SCA composite file to a Fractal string unit
     * @return 
     */
    public String ConvertSCAXMLToFractal() {
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            saxReader.setContentHandler(this);
            saxReader.parse(new InputSource(in));
            //System.out.println(xmlComponent);
        } catch (IOException ex) {
            Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);

        } catch (SAXException ex) {
            Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlComponent.toXml();
    }

    public String ConvertSCAXMLToFractal(String file) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(file, this);
            //System.out.println(xmlComponent);
        } catch (IOException ex) {
            Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";//getOutPutDoc();
    }

    /**
     * set Document locator
     * @param value 
     */
    public void setDocumentLocator(Locator value) {
        locator = value;
    }

    /**
     * Treatment of XML elements, construct FractalXMLObject from this
     * @throws SAXException 
     */
    public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs)
            throws SAXException {
        if (localName.equalsIgnoreCase("service")) {
            constructingService = true;
        }
//        System.out.println("Open element : " + localName);
//
//        if (!"".equals(nameSpaceURI)) { // espace de nommage particulier
//            System.out.println(" element is from namespace : " + nameSpaceURI);
//        }
//
//        System.out.println("  Attributs of the element : ");
//
//        for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
//            System.out.println("     - " + attributs.getLocalName(index) + " = " + attributs.getValue(index));
//        }
        analyseTagsAndAtrributes(localName, attributs);

    }

    /**
     * Evenement recu a chaque fermeture de balise.
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
//        System.out.print("Fermeture de la balise : " + localName);
//
//        if (!"".equals(nameSpaceURI)) { // name space non null
//            System.out.print("appartenant a l'espace de nommage : " + localName);
//        }
        //analyseTagsAndAtrributes(currentLocalName, currentAttributs);
        //System.err.println("DEBUGGGGGGG ================= " + currentPCDATA +  currentAttributs.getValue("name"));
        if (localName.equalsIgnoreCase("property")) {
            xmlComponent.attributes.put(currentPropertyName, currentPCDATA);
        }
    }

    /**
     * Treatment of data between element tag
     */
    public void characters(char[] ch, int start, int end) throws SAXException {
        //System.out.println("#PCDATA : " + new String(ch, start, end));
        currentPCDATA = new String(ch, start, end);
    }

    /**
     * Rencontre une instruction de fonctionnement.
     * @param target la cible de l'instruction de fonctionnement.
     * @param data les valeurs associees a cette cible. En general, elle se presente sous la forme 
     * d'une serie de paires nom/valeur.
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(String target, String data) throws SAXException {
//        System.out.println("Instruction de fonctionnement : " + target);
//        System.out.println("  dont les arguments sont : " + data);
    }

    protected void parseCompositeTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("composite")) {
            //Composite tage at the begining of file
            xmlComponent.name = atts.getValue("name");
            return;
        }
    }

    protected void parseComponentTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("component")) {
            constructingComponent = true;
            Component tmp = new Component();
            xmlComponent.components.add(tmp);
            xmlComponent.getLastComponent().name = atts.getValue("name");
            return;
        }
    }

    protected void parseImplementation_javaTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("implementation.java")) {
            xmlComponent.contentClassName = atts.getValue("class");
            if (constructingComponent) {
                xmlComponent.getLastComponent().contentClassName = atts.getValue("class");
            } else {
                System.err.println("tag implementation.java can't be parsed outside component tag context");
            }
            return;
        }
    }

    protected void parseServiceTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("service")) {
            constructingService = true;
            Service tmpService = null;
            tmpService = new Service(atts.getValue("name"), "server", "", "");
            String promote = atts.getValue("promote");
            String requires = atts.getValue("requires");
            if (promote != null) {
                if (promote.contains("/")) {
                    tmpService.softLink = promote;
                } else {
                    tmpService.softLink = "";
                    tmpService.implementation = promote;
                }
            } else {
                if (requires != null) {
                    System.err.println("construct intent here : " + requires + System.getenv(tag));
                    String requiresName = requires.replace('.', '/') + ".composite";
                    System.err.println("DEBUGGG============" + requiresName);
                    ClassLoader cl = ClassLoaderHelper.getClassLoader(this);
                    final URL url = cl.getResource(requiresName);
                    //String requiresName = requires+".composite";

                    byte[] bytes = new byte[4000];
                    try {
                        url.openStream().read(bytes);
                        XMLReader intentReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                        intentFileHandler tmp = new intentFileHandler();
                        intentReader.setContentHandler(tmp);
                        intentReader.parse(new InputSource(url.openStream()));
                        System.err.println(tmp.implementedClass.toUpperCase());

                    } catch (Exception ex) {
                        Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.err.println(new String(bytes));
                }
            }
            if (constructingComponent) {
                xmlComponent.getLastComponent().services.add(tmpService);
            } else {
                xmlComponent.services.add(tmpService);
            }

            String[] tmp = new String[3];
            tmp[0] = atts.getValue("promote");
            tmp[1] = "server";
            tmp[2] = atts.getValue("name");
            xmlComponent.interfaceNamesAndRoles.add(tmp);
            return;
        }

    }

    protected void parseInterface_javaTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("interface.java")) {
            Service tmpService = null;
            if (constructingComponent) {
                tmpService = xmlComponent.getLastComponent().getLastService();
            } else {
                tmpService = xmlComponent.getLastService();
            }
            if (constructingService) {
                tmpService.implementation = atts.getValue("interface");
            } else {
                System.err.println("tag interface.java can't be parsed outside service tag context");
            }
            return;
        }
    }

    protected void parseReferenceTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("reference")) {
            String[] tmp = new String[3];
            tmp[0] = atts.getValue("promote");
            tmp[1] = "client";
            tmp[2] = atts.getValue("name");
            xmlComponent.interfaceNamesAndRoles.add(tmp);
            return;
        }
    }

    protected void parsePropertyTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("property")) {
            if (constructingComponent) {
                constructingProperty = true;
//                            System.err.println("not implemented yet!");
//                            //xmlComponent.attributes.put(atts.getValue("name"), currentPCDATA);
//                            System.err.println(atts.getValue("name") +  currentPCDATA);
                currentPropertyName = atts.getValue("name");
                return;
            } else {
                System.err.println("tag property can't be parsed outside component tag context");
            }
        }

    }

    protected void analyseTagsAndAtrributes(String tag, Attributes atts) {
        parseCompositeTag(tag, atts);
        parseComponentTag(tag, atts);
        parseImplementation_javaTag(tag, atts);
        parseServiceTag(tag, atts);
        parseInterface_javaTag(tag, atts);
        parseReferenceTag(tag, atts);
        parsePropertyTag(tag, atts);
    }

    /**
     * A fractal Object which correspond to SCA xml compisite file
     */
    private class FractalXMLObject {

        protected String name;
        public final String GCMHeader = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> \n"
                + "<!DOCTYPE definition PUBLIC \"-//objectweb.org//DTD Fractal ADL 2.0//EN\" \"classpath://org/objectweb/proactive/core/component/adl/xml/proactive.dtd\">\n";
        private Document outPutdocument;
        protected List<Service> services;
        protected List<Component> components;
        protected List<String[]> interfaceNamesAndRoles;
        protected String contentClassName;
        protected HashMap<String, String> attributes;

        public Component getLastComponent() {
            return components.get(components.size() - 1);
        }

        public Service getLastService() {
            return services.get(services.size() - 1);
        }

        public FractalXMLObject() {
            services = new ArrayList<Service>();
            components = new ArrayList<Component>();
            interfaceNamesAndRoles = new ArrayList<String[]>();
            attributes = new HashMap<String, String>();
            initDocument();
        }

        /**
         * init output document
         */
        private void initDocument() {
            try {
                DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
                outPutdocument = docBuilder.newDocument();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        /**
         * Get the out put document in string form
         */
        private String getOutPutDoc() {
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
                Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return xmlString;
        }

        public String toString() {
            String tmp = "name of component: " + name;
            tmp += "\ncontentClassName: " + contentClassName;
            for (String[] strings : interfaceNamesAndRoles) {
                tmp += "\ninterface signature: " + strings[0] + "\ninterface role: " + strings[1]
                        + "\ninterface name: " + strings[2];
            }
            return tmp;
        }

        /**
         * Get Fractal XML output
         * @return Fractal XML output
         */
        public String toXml() {
            Element rootEle = outPutdocument.createElement("definition");
            rootEle.setAttribute("name", name);
            outPutdocument.appendChild(rootEle);
            for (String[] strings : interfaceNamesAndRoles) {
                Element itf = outPutdocument.createElement("interface");
                itf.setAttribute("signature", strings[0]);
                itf.setAttribute("role", strings[1]);
                itf.setAttribute("name", strings[2]);
                rootEle.appendChild(itf);
            }
            Element contentClass = outPutdocument.createElement("content");
            contentClass.setAttribute("class", contentClassName);
            rootEle.appendChild(contentClass);
            Element prim = outPutdocument.createElement("controller");
            prim.setAttribute("desc", "primitive");
            rootEle.appendChild(prim);
            return GCMHeader + getOutPutDoc();
        }
    }

    private class Component {

        String name;
        String contentClassName;
        List<Service> services;
        HashMap<String, String> attributes;

        public Component() {
            services = new ArrayList<Service>();
            attributes = new HashMap<String, String>();
        }

        public Service getLastService() {
            return services.get(services.size() - 1);
        }
    }

    private class Service {

        String name;
        String role;
        String softLink;
        String implementation;

        public Service(String name, String role, String softLink, String implementation) {
            this.name = name;
            this.role = role;
            this.softLink = softLink;
            this.implementation = implementation;
        }
    }

    private class Intent {

        String name;
        String implementation;
        String applicatedInterface;
    }

    private class intentFileHandler extends DefaultHandler {

        private String implementedClass;

        public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs)
                throws SAXException {
            if (localName.equalsIgnoreCase("implementation.java")) {
                implementedClass = attributs.getValue("class");
            }
        }

        public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
        }
    }
}
