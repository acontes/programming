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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.objectweb.fractal.adl.xml.XMLLoader;
import org.objectweb.fractal.adl.ADLErrors;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Parser;
import org.objectweb.fractal.adl.ParserException;
import org.objectweb.fractal.adl.error.BasicErrorLocator;
import org.objectweb.fractal.adl.error.ErrorLocator;
import org.objectweb.fractal.adl.timestamp.Timestamp;
import org.objectweb.fractal.adl.util.ClassLoaderHelper;
import org.objectweb.fractal.adl.xml.XMLErrors;
import org.objectweb.fractal.adl.xml.XMLParser;
import org.xml.sax.SAXParseException;

/**
 * The custom XML loader , the class override load method of XMLLoader
 * @author mug
 */
public class SCAXMLLoader extends XMLLoader {

    private boolean validate = true;
    private Parser parser;
    
    public SCAXMLConverter scaXMLConverter;
  
    public SCAXMLLoader() {
        super();
    }

    public SCAXMLLoader(final boolean validate) {
        super(validate);
    }
    /**
     * Get the properties values in the the SCA composite file 
     */
    public List<String[]> getPropertiesValues() {
        if(scaXMLConverter == null ){ // if it's not a sca composite file
            return null;
        }
        return scaXMLConverter.getXmlComponent().getProperties();
    }
    /**
     * Get the intents values in the the SCA composite file 
     */
    public List<String[]> getIntents(){
         if(scaXMLConverter == null ){ // if it's not a sca composite file
            return null;
        }
        return scaXMLConverter.getXmlComponent().getIntents();
    }
    
    // --------------------------------------------------------------------------
    // RE - Implementation of the Loader interface
    // --------------------------------------------------------------------------
    public Definition load(final String name, final Map<Object, Object> context) throws ADLException {
        // this class to modify !!!
        synchronized (this) {
            if (parser == null) {
                parser = new XMLParser(validate, nodeFactoryItf);
            }
        }
        final String file = name.replace('.', '/') + ".fractal";

        final String fileSca = name.replace('.', '/') + ".composite";

        final ClassLoader cl = ClassLoaderHelper.getClassLoader(this, context);
        URL url = cl.getResource(file);

        InputStream inStream = null;
        try {
            if (url == null) {
                System.out.println(new ADLException(ADLErrors.ADL_NOT_FOUND, file) + "\ngoing to look for "+fileSca);
                url = cl.getResource(fileSca);
                if (url == null) {
                    throw new ADLException(ADLErrors.ADL_NOT_FOUND, file);
                } else {
                    inStream = url.openStream();
                    scaXMLConverter = new SCAXMLConverter(inStream);
                    String xml = scaXMLConverter.getXmlComponent().toXml();
                    inStream = new ByteArrayInputStream(xml.getBytes());
                }
            } else {
                inStream = url.openStream();
            }
        } catch (IOException ioe) {
            throw new ADLException(ADLErrors.IO_ERROR, ioe, file);
        }
        try {

            nodeFactoryItf.setClassLoader(cl);
            final Definition d;
            d = (Definition) parser.parse(inStream, file);
            nodeFactoryItf.setClassLoader(ClassLoaderHelper.getClassLoader(this));
            if (d.getName() == null) {
                throw new ADLException(XMLErrors.DEFINTION_NAME_MISSING, d);
            }
            if (!d.getName().equals(name)) {
                throw new ADLException(ADLErrors.WRONG_DEFINITION_NAME, d, name, d.getName());
            }

            // Set timestamp of the ADL file
            Timestamp.setTimestamp(d, url);

            return d;
        } catch (final ParserException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof SAXParseException) {
                final SAXParseException spe = (SAXParseException) cause;
                final ErrorLocator locator = new BasicErrorLocator(file, spe.getLineNumber(), spe
                        .getColumnNumber());
                throw new ADLException(ADLErrors.PARSE_ERROR, locator, spe.getMessage());
            }
            throw new ADLException(ADLErrors.PARSE_ERROR, cause, cause.getMessage());
        }
    }
}
