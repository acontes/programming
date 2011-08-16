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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.objectweb.fractal.adl.util.ClassLoaderHelper;
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
public class SCAXMLConverterHandler extends DefaultHandler {

    private Locator locator;
    private SCACompositeXMLObject xmlComponent;
    private String currentPropertyName;
    private String currentPCDATA;
    private boolean constructingService = false;
    private boolean constructingComponent = false;
    private boolean constructingProperty = false;

    public SCAXMLConverterHandler() {
        super();
        locator = new LocatorImpl();
        xmlComponent = new SCACompositeXMLObject();
    }

    public List<String[]> getPropertiesValues() {
        return xmlComponent.getProperties();
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
        analyseTagsAndAtrributes(localName, attributs);
    }

    /**
     * Evenement recu a chaque fermeture de balise.
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
        if (localName.equalsIgnoreCase("component")) {
            constructingComponent = false;
            return;
        }
        if (localName.equalsIgnoreCase("service")) {
            constructingService = false;
        }
        if (localName.equalsIgnoreCase("property")) {
            String[] tmp = new String[3];
            tmp[0] = "";
            tmp[1] = currentPropertyName;
            tmp[2] = currentPCDATA;
            xmlComponent.getLastComponent().getProperties().add(tmp);
            constructingProperty = false;
        }
    }

    /**
     * End of parsing procedure, revisit everything
     */
    public void endDocument() {
        xmlComponent.revisitConstructedObject();
    }

    /**
     * Treatment of data between element tag
     */
    public void characters(char[] ch, int start, int end) throws SAXException {
        //System.out.println("#PCDATA : " + new String(ch, start, end));
        currentPCDATA = new String(ch, start, end);
    }

    protected void parseCompositeTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("composite")) {
            //Composite tage at the begining of file
            xmlComponent.setCompositeName(atts.getValue("name"));
            return;
        }
    }

    protected void parseComponentTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("component")) {
            constructingComponent = true;
            ComponentTag tmp = new ComponentTag();
            tmp.setComponentName(atts.getValue("name"));
            xmlComponent.getComponents().add(tmp);
            return;
        }
    }

    protected void parseImplementation_javaTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("implementation.java")) {
            if (constructingComponent) {
                xmlComponent.getLastComponent().setContentClassName(atts.getValue("class"));
            } else {
                System.err.println("tag implementation.java can't be parsed outside component tag context");
            }
            return;
        }
    }

    protected void parseServiceTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("service")) {
            constructingService = true;
            ServiceTag tmpService = new ServiceTag(atts.getValue("name"), "server", "", "");
            String promote = atts.getValue("promote");
            String requires = atts.getValue("requires");
            if (promote != null) { // must be in composite service construction
                if(!constructingComponent)
                    tmpService.setSoftLink(promote);
                else
                    System.err.println("attribute promote can't be parsed inside component's service tag context");
            }
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
                    IntentFileHandler tmp = new IntentFileHandler();
                    intentReader.setContentHandler(tmp);
                    intentReader.parse(new InputSource(url.openStream()));
                    System.err.println(tmp.getImplementedClass().toUpperCase());

                } catch (Exception ex) {
                    Logger.getLogger(SCAXMLConverterHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.err.println(new String(bytes));
            }

            if (constructingComponent) {
                tmpService.setReferenceComponentName(xmlComponent.getLastComponent().getComponentName());
                xmlComponent.getLastComponent().getServices().add(tmpService);
            } else {
                xmlComponent.getServices().add(tmpService);
            }
            return;
        }
    }

    protected void parseInterface_javaTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("interface.java")) {
            ServiceTag tmpService = null;
            if (constructingComponent) {
                tmpService = xmlComponent.getLastComponent().getLastService();
            } else {
                System.err.println("tag interface.java shouldn't be used in composite context");
                tmpService = xmlComponent.getLastService();
            }
            if (constructingService) {
                tmpService.setImplementation(atts.getValue("interface"));
            } else {
                System.err.println("tag interface.java can't be parsed outside service tag context");
            }
            return;
        }
    }

    protected void parseReferenceTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("reference")) {
            System.err.println("tag reference's parsing procedure is not implemented yet!");
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

    public SCACompositeXMLObject getXmlComponent() {
        return xmlComponent;
    }

    public void setXmlComponent(SCACompositeXMLObject xmlComponent) {
        this.xmlComponent = xmlComponent;
    }

    private class Intent {

        String name;
        String implementation;
        String applicatedInterface;
    }
}
