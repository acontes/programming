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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.proactive.extensions.sca.adl.xml.parsingTools.SCACompositeXMLObject;
import org.objectweb.proactive.extensions.sca.adl.xml.parsingTools.SCAXMLConverterHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * This class define how to convert a SCA composite file into Fractal file
 */
public class SCAXMLConverter {

    private InputStream in;
    //embeded xmlComponent object
    private SCACompositeXMLObject xmlComponent;

    public SCAXMLConverter(InputStream in) {
        this.in = in;
        parseSCAXML();
    }

    /**
     * Convert SCA composite file to a Fractal string unit
     * @return 
     */
    private void parseSCAXML() {
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            SCAXMLConverterHandler SCAXMLConverterHandler = new SCAXMLConverterHandler();
            saxReader.setContentHandler(SCAXMLConverterHandler);
            saxReader.parse(new InputSource(in));
            xmlComponent = SCAXMLConverterHandler.getXmlComponent();
        } catch (IOException ex) {
            Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);

        } catch (SAXException ex) {
            Logger.getLogger(SCAXMLConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SCACompositeXMLObject getXmlComponent() {
        return xmlComponent;
    }

    public void setXmlComponent(SCACompositeXMLObject xmlComponent) {
        this.xmlComponent = xmlComponent;
    }   
}
