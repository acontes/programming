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
import java.util.List;
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
public class SCAXMLConverterMod {

    private InputStream in;
    private SCACompositeXMLObject xmlComponent;
  
    public SCAXMLConverterMod(InputStream in) {
        super();
        this.in = in;
       
    }

    public List<String[]> getPropertiesValues() {
        return xmlComponent.getProperties();
    }

    /**
     * Convert SCA composite file to a Fractal string unit
     * @return 
     */
    public String ConvertSCAXMLToFractal() {
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            SCAXMLConverterHandler tmp = new SCAXMLConverterHandler();
            saxReader.setContentHandler(tmp);
            saxReader.parse(new InputSource(in));
            xmlComponent = tmp.getXmlComponent();
            //xmlComponent.revisitConstructedObject();
            //System.out.println(xmlComponent);
        } catch (IOException ex) {
            Logger.getLogger(SCAXMLConverterMod.class.getName()).log(Level.SEVERE, null, ex);

        } catch (SAXException ex) {
            Logger.getLogger(SCAXMLConverterMod.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlComponent.toXml();
    }
  

}
