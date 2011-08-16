/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.objectweb.proactive.extensions.sca.adl.xml.parsingTools;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class IntentFileHandler extends DefaultHandler {

    private String implementedClass;

    public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs)
            throws SAXException {
        if (localName.equalsIgnoreCase("implementation.java")) {
            implementedClass = attributs.getValue("class");
        }
    }

    public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
    }

    /*******************************************************************************
     *          Generated getters and setters
     *******************************************************************************/
    public String getImplementedClass() {
        return implementedClass;
    }

    public void setImplementedClass(String implementedClass) {
        this.implementedClass = implementedClass;
    }
}