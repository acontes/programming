package org.objectweb.proactive.core.component.adl.luc;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import lucci.io.JavaResource;
import lucci.text.xml.XMLNode;
import lucci.text.xml.XMLUtilities;

public class LexicalAnalyzer
{

	public XMLNode parse(String adlDescription, boolean validate) throws IOException, ParserConfigurationException, SAXException
	{
		// gets the header text, which include de DTD declaration
		String header = new String(new JavaResource(getClass(), "xmlheader.txt").getByteArray());
		
		// gets the URL to the DTD
		URL dtdURL = new JavaResource(getClass(), "adl.dtd").getURL();
		
		// and put it into the header
		header = header.replace("${url}", dtdURL.toExternalForm());
		
		// inserts the header at the top of the XML text
		adlDescription = header + "\n\n" + adlDescription;

		// and parse the whose XML
		return XMLUtilities.xml2node(adlDescription, validate);
	}

}
