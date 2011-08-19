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

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.fractal.adl.util.ClassLoaderHelper;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 *
 * This class define how to convert a SCA composite file into Fractal file
 */
public class SCAXMLConverterHandler extends DefaultHandler {

    private SCACompositeXMLObject xmlComponent;
    private String currentPropertyName;
    private String currentPCDATA;
    private boolean constructingService = false;
    private boolean constructingComponent = false;

    public SCAXMLConverterHandler() {
        super();
        xmlComponent = new SCACompositeXMLObject();
    }

    /**
     * Treatment of XML elements, construct FractalXMLObject from this
     * @throws SAXException 
     */
    @Override
    public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs)
            throws SAXException {
        analyseTagsAndAtrributes(localName, attributs);
    }

    /**
     * Operation while closing a tag
     */
    @Override
    public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
        if (localName.equalsIgnoreCase("component")) {
            constructingComponent = false;
            return;
        }
        if (localName.equalsIgnoreCase("service")) {
            constructingService = false;
        }
        if (localName.equalsIgnoreCase("property")) {
            if (constructingComponent) {
                String[] tmp = new String[3];
                tmp[0] = xmlComponent.getLastComponent().getComponentName();
                tmp[1] = currentPropertyName;
                tmp[2] = currentPCDATA;
                xmlComponent.getLastComponent().getProperties().add(tmp);
            }
        }
    }

    /**
     * End of parsing procedure, revisit everything
     */
    @Override
    public void endDocument() {
        xmlComponent.revisitConstructedObject();
    }

    /**
     * Treatment of data between element tag
     */
    @Override
    public void characters(char[] ch, int start, int end) throws SAXException {
        currentPCDATA = new String(ch, start, end);
    }

    /**
     * Rules to parse a sca composite tag
     */
    protected void parseCompositeTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase(SCA_COMPOSITE_TAG)) {
            //Composite tage at the begining of file
            xmlComponent.setCompositeName(atts.getValue(SCA_NAME_ATTRIBUTE_OF_COMPOSITE_TAG));
        }
    }

    /**
     * Rules to parse a sca component tag
     */
    protected void parseComponentTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase(SCA_COMPONENT_TAG)) {
            constructingComponent = true;
            ComponentTag tmp = new ComponentTag();
            tmp.setComponentName(atts.getValue(SCA_NAME_ATTRIBUTE_OF_COMPONENT_TAG));
            xmlComponent.getComponents().add(tmp);
        }
    }

    /**
     * Rules to parse a sca Implementation.java tag
     */
    protected void parseImplementation_javaTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase(SCA_IMPLEMENTATION_JAVA_TAG)) {
            if (constructingComponent) {
                xmlComponent.getLastComponent().setContentClassName(
                        atts.getValue(SCA_CLASS_ATTRIBUTE_OF_IMPLEMENTATION_JAVA_TAG));
            } else {
                System.err.println("tag implementation.java can't be parsed outside component tag context");
            }
        }
    }

    /**
     * Rules to parse a sca Service tag
     */
    private void parseExposedInterface(String tag, Attributes atts, String role) {

        constructingService = true;
        String serviceName = atts.getValue(SCA_NAME_ATTRIBUTE_OF_EXPOSED_INTERFACE);
        String promote = atts.getValue(SCA_PROMOTE_ATTRIBUTE_OF_EXPOSED_INTERFACE);
        String requires = atts.getValue(SCA_REQUIRES_ATTRIBUTE_OF_EXPOSED_INTERFACE);
        ExposedInterface tmpService = new ExposedInterface(serviceName, role, "", "");
        if (promote != null) { // must be in composite service construction
            if (!constructingComponent) {
                tmpService.setSoftLink(promote);
            } else {
                System.err
                        .println("attribute promote can't be parsed inside component's service tag context");
            }
        }
        if (requires != null) {
            String requiresName = requires.replace('.', '/') + ".composite";
            ClassLoader cl = ClassLoaderHelper.getClassLoader(this);
            final URL url = cl.getResource(requiresName);
            try {
                XMLReader intentReader = XMLReaderFactory
                        .createXMLReader("org.apache.xerces.parsers.SAXParser");
                IntentFileHandler tmp = new IntentFileHandler();
                intentReader.setContentHandler(tmp);
                intentReader.parse(new InputSource(url.openStream()));
                String implementedClass = tmp.implementedClass;
                IntentComposite intComp = new IntentComposite(tmp.name, xmlComponent.getLastComponent()
                        .getComponentName(), serviceName, implementedClass);
                xmlComponent.intents.add(intComp);
            } catch (Exception ex) {
                Logger.getLogger(SCAXMLConverterHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (constructingComponent) {
            tmpService.setReferenceComponentName(xmlComponent.getLastComponent().getComponentName());
            xmlComponent.getLastComponent().getExposedInterface().add(tmpService);
        } else {
            xmlComponent.getExposedInterface().add(tmpService);
        }

    }

    /**
     * Rules to parse a sca interface.java tag
     */
    protected void parseInterface_javaTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase(SCA_INTERFACE_JAVA_TAG)) {
            ExposedInterface tmpService = null;
            if (constructingComponent) {
                tmpService = xmlComponent.getLastComponent().getLastExposedInterface();
            } else {
                System.err.println("tag interface.java shouldn't be used in composite context");
                tmpService = xmlComponent.getLastExposedInterface();
            }
            if (constructingService) {
                tmpService.setImplementation(atts.getValue(SCA_INTERFACE_ATTRIBUTE_OF_INTERFACE_JAVA_TAG));
            } else {
                System.err.println("tag interface.java can't be parsed outside service tag context");
            }
        }
    }

    /**
     * Rules to parse a sca Reference tag
     */
    protected void parseReferenceTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase(SCA_REFERENCE_TAG)) {
            parseExposedInterface(tag, atts, "client");
        }
    }

    /**
     * Rules to parse a sca Service tag
     */
    protected void parseServiceTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase(SCA_SERVICE_TAG)) {
            parseExposedInterface(tag, atts, "server");
        }
    }

    /**
     * Rules to parse a sca Wired tag
     */
    protected void parseWiredTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase(SCA_WIRE_TAG)) {
            WireTag wireTag = new WireTag(atts.getValue(SCA_SOURCE_ATTRIBUTE_OF_WIRE_TAG), atts
                    .getValue(SCA_TARGET_ATTRIBUTE_OF_WIRE_TAG));
            xmlComponent.wires.add(wireTag);
        }
    }

    /**
     * Rules to parse a sca Property tag
     */
    protected void parsePropertyTag(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase(SCA_PROPERTY_TAG)) {
            if (constructingComponent) {
                currentPropertyName = atts.getValue(SCA_NAME_ATTRIBUTE_OF_PROPERTY_TAG);
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
        parseReferenceTag(tag, atts);
        parseInterface_javaTag(tag, atts);
        parseReferenceTag(tag, atts);
        parseWiredTag(tag, atts);
        parsePropertyTag(tag, atts);
    }

    public SCACompositeXMLObject getXmlComponent() {
        return xmlComponent;
    }

    public void setXmlComponent(SCACompositeXMLObject xmlComponent) {
        this.xmlComponent = xmlComponent;
    }

    //SCA tags name and their attributes name constants
    public static final String SCA_COMPOSITE_TAG = "composite";
    public static final String SCA_NAME_ATTRIBUTE_OF_COMPOSITE_TAG = "name";
    /**************************************************************************/
    public static final String SCA_COMPONENT_TAG = "component";
    public static final String SCA_NAME_ATTRIBUTE_OF_COMPONENT_TAG = "name";
    /**************************************************************************/
    public static final String SCA_IMPLEMENTATION_JAVA_TAG = "implementation.java";
    public static final String SCA_CLASS_ATTRIBUTE_OF_IMPLEMENTATION_JAVA_TAG = "class";
    /**************************************************************************/
    public static final String SCA_SERVICE_TAG = "service";
    public static final String SCA_REFERENCE_TAG = "reference";
    public static final String SCA_NAME_ATTRIBUTE_OF_EXPOSED_INTERFACE = "name";
    public static final String SCA_PROMOTE_ATTRIBUTE_OF_EXPOSED_INTERFACE = "promote";
    public static final String SCA_REQUIRES_ATTRIBUTE_OF_EXPOSED_INTERFACE = "requires";
    /**************************************************************************/
    public static final String SCA_INTERFACE_JAVA_TAG = "interface.java";
    public static final String SCA_INTERFACE_ATTRIBUTE_OF_INTERFACE_JAVA_TAG = "interface";
    /**************************************************************************/
    public static final String SCA_PROPERTY_TAG = "property";
    public static final String SCA_NAME_ATTRIBUTE_OF_PROPERTY_TAG = "name";
    /**************************************************************************/
    public static final String SCA_WIRE_TAG = "wire";
    public static final String SCA_SOURCE_ATTRIBUTE_OF_WIRE_TAG = "source";
    public static final String SCA_TARGET_ATTRIBUTE_OF_WIRE_TAG = "target";
}
