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

    public SCAXMLConverter(InputStream in) {
        super();
        this.in = in;
        locator = new LocatorImpl();
        xmlComponent = new FractalXMLObject();
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
            System.out.println(xmlComponent);
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
            System.out.println(xmlComponent);
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
     * Definition du locator qui permet a tout moment pendant l'analyse, de localiser
     * le traitement dans le flux. Le locator par defaut indique, par exemple, le numero
     * de ligne et le numero de caractere sur la ligne.
     * @author smeric
     * @param value le locator a utiliser.
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator value) {
        locator = value;
    }

    /**
     * Evenement envoye au demarrage du parse du flux xml.
     * @throws SAXException en cas de probleme quelquonque ne permettant pas de
     * se lancer dans l'analyse du document.
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        System.out.println("Debut de l'analyse du document");
    }

    /**
     * Evenement envoye a la fin de l'analyse du flux xml.
     * @throws SAXException en cas de probleme quelquonque ne permettant pas de
     * considerer l'analyse du document comme etant complete.
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {
        System.out.println("Fin de l'analyse du document");
    }

    /**
     * Debut de traitement dans un espace de nommage.
     * @param prefixe utilise pour cet espace de nommage dans cette partie de l'arborescence.
     * @param URI de l'espace de nommage.
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(String prefix, String URI) throws SAXException {
        System.out.println("Traitement de l'espace de nommage : " + URI + ", prefixe choisi : " + prefix);
    }

    /**
     * Fin de traitement de l'espace de nommage.
     * @param prefixe le prefixe choisi a l'ouverture du traitement de l'espace nommage.
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        System.out.println("Fin de traitement de l'espace de nommage : " + prefix);
    }

    /**
     * Evenement recu a chaque fois que l'analyseur rencontre une balise xml ouvrante.
     * @param nameSpaceURI l'url de l'espace de nommage.
     * @param localName le nom local de la balise.
     * @param rawName nom de la balise en version 1.0 <code>nameSpaceURI + ":" + localName</code>
     * @throws SAXException si la balise ne correspond pas a ce qui est attendu,
     * comme par exemple non respect d'une dtd.
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs)
            throws SAXException {
        System.out.println("Ouverture de la balise : " + localName);

        if (!"".equals(nameSpaceURI)) { // espace de nommage particulier
            System.out.println("  appartenant a l'espace de nom : " + nameSpaceURI);
        }

        System.out.println("  Attributs de la balise : ");

        for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
            System.out.println("     - " + attributs.getLocalName(index) + " = " + attributs.getValue(index));
        }
        analyseTagsAndAtrributes(localName, attributs);
    }

    /**
     * Evenement recu a chaque fermeture de balise.
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
        System.out.print("Fermeture de la balise : " + localName);

        if (!"".equals(nameSpaceURI)) { // name space non null
            System.out.print("appartenant a l'espace de nommage : " + localName);
        }

        System.out.println();
    }

    /**
     * Evenement recu a chaque fois que l'analyseur rencontre des caracteres (entre
     * deux balises).
     * @param ch les caracteres proprement dits.
     * @param start le rang du premier caractere a traiter effectivement.
     * @param end le rang du dernier caractere a traiter effectivement
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int end) throws SAXException {
        System.out.println("#PCDATA : " + new String(ch, start, end));
    }

    /**
     * Recu chaque fois que des caracteres d'espacement peuvent etre ignores au sens de
     * XML. C'est a dire que cet evenement est envoye pour plusieurs espaces se succedant,
     * les tabulations, et les retours chariot se succedants ainsi que toute combinaison de ces
     * trois types d'occurrence.
     * @param ch les caracteres proprement dits.
     * @param start le rang du premier caractere a traiter effectivement.
     * @param end le rang du dernier caractere a traiter effectivement
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] ch, int start, int end) throws SAXException {
        System.out.println("espaces inutiles rencontres : ..." + new String(ch, start, end) + "...");
    }

    /**
     * Rencontre une instruction de fonctionnement.
     * @param target la cible de l'instruction de fonctionnement.
     * @param data les valeurs associees a cette cible. En general, elle se presente sous la forme 
     * d'une serie de paires nom/valeur.
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(String target, String data) throws SAXException {
        System.out.println("Instruction de fonctionnement : " + target);
        System.out.println("  dont les arguments sont : " + data);
    }

    /**
     * Recu a chaque fois qu'une balise est evitee dans le traitement a cause d'un
     * probleme non bloque par le parser. Pour ma part je ne pense pas que vous
     * en ayez besoin dans vos traitements.
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String arg0) throws SAXException {
        // Je ne fais rien, ce qui se passe n'est pas franchement normal.
        // Pour eviter cet evenement, le mieux est quand meme de specifier une dtd pour vos
        // documents xml et de les faire valider par votre parser.              
    }

    private void analyseTagsAndAtrributes(String tag, Attributes atts) {
        if (tag.equalsIgnoreCase("composite")) {
            //Composite tage at the begining of file
            xmlComponent.name = atts.getValue("name");
        } else {
            if (tag.equalsIgnoreCase("implementation.java")) {
                xmlComponent.contentClassName = atts.getValue("class");
            } else {
                if (tag.equalsIgnoreCase("service")) {
                    String[] tmp = new String[3];
                    tmp[0] = atts.getValue("promote");
                    tmp[1] = "server";
                    tmp[2] = atts.getValue("name");
                    xmlComponent.interfaceNamesAndRoles.add(tmp);
                } else {
                    if (tag.equalsIgnoreCase("reference")) {
                        String[] tmp = new String[3];
                        tmp[0] = atts.getValue("promote");
                        tmp[1] = "client";
                        tmp[2] = atts.getValue("name");
                        xmlComponent.interfaceNamesAndRoles.add(tmp);
                    } else {
                        if (tag.equalsIgnoreCase("property")) {
                            System.out.println("no treatment yet");
                        }
                    }
                }
            }
        }

    }

    private class FractalXMLObject {

        protected String name;
        public final String GCMHeader = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> \n"
            + "<!DOCTYPE definition PUBLIC \"-//objectweb.org//DTD Fractal ADL 2.0//EN\" \"classpath://org/objectweb/proactive/core/component/adl/xml/proactive.dtd\">\n";
        protected List<String[]> interfaceNamesAndRoles;
        protected String contentClassName;
        protected HashMap<String, String> attributes;
        private Document outPutdocument;

        public FractalXMLObject() {
            interfaceNamesAndRoles = new ArrayList<String[]>();
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
                tmp += "\ninterface signature: " + strings[0] + "\ninterface role: " + strings[1] +
                    "\ninterface name: " + strings[2];
            }
            return tmp;
        }

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
}
