/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 *              Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.examples.components.sca.currencysms;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.objectweb.proactive.core.component.webservices.PAWSCaller;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class RestOrangeServiceCaller implements PAWSCaller {
    private String rootUrl;

    private String constructUrl(String id, String from, String to, String content) {
        return rootUrl + "?id=" + id + "&from=" + from + "&to=" + to + "&content=" + content;
    }

    public Object callWS(String methodName, Object[] args, Class<?> returnType) {
        if (!methodName.equals("sendSMS")) {
            System.err.println("NoSuchMethode on this RESTful webservice: " + methodName);
        }
        if (args.length != 4) {
            System.err.println("Nb of arguments is not correct!");
        }
        String url = constructUrl((String) args[0], (String) args[1], (String) args[2], (String) args[3]);
        ResultatParseur resP = new ResultatParseur(url);
        resP.execute();
        int status_code = resP.getStatus_code();
        String msg = resP.getStatus_msg();
        System.out.println("return message : " + msg);
        if (status_code == 200) {
            return true;
        }
        return false;
    }

    public void setup(Class<?> serviceClass, String wsUrl) {
        rootUrl = wsUrl;
    }
}

class ResultatParseur extends DefaultHandler {
    private String url;
    private boolean disp_code = false;
    private boolean disp_msg = false;
    private int status_code;
    private String status_msg;

    public ResultatParseur(String urlToParse) {
        url = urlToParse;
    }

    public int getStatus_code() {
        return status_code;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void execute() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(null);
            xmlReader.parse(url);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void startDocument() throws SAXException {
        System.out.println("Start Document: " + url);
    }

    public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
            throws SAXException {
        String eltName = localName;
        if ("".equals(eltName))
            eltName = rawName;
        if (eltName.equals("status_code")) {
            disp_code = true;
        }
        if (eltName.equals("status_msg")) {
            disp_msg = true;
        }
    }

    public void characters(char[] ch, int start, int length) {
        if (disp_code == true) {
            String text = new String(ch, start, length);
            if (text.trim().length() > 1) {
                status_code = Integer.parseInt(text);
            }
        }
        if (disp_msg == true) {
            String text = new String(ch, start, length);
            if (text.trim().length() > 1) {
                status_msg = text;
            }
        }
    }

    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String rawName)
            throws SAXException {
        String eltName = localName;
        if ("".equals(eltName))
            eltName = rawName;
        if (eltName.equals("status_code")) {
            disp_code = false;
        }
        if (eltName.equals("status_msg")) {
            disp_msg = false;
        }
    }

    public void endDocument() throws SAXException {
    }
}