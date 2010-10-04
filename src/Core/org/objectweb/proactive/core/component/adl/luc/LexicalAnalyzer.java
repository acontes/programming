package org.objectweb.proactive.core.component.adl.luc;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import lucci.io.JavaResource;
import lucci.text.xml.XMLNode;
import lucci.text.xml.XMLUtilities;

public class LexicalAnalyzer
{

	public XMLNode parse(String adlDescription, boolean validate) throws IOException, ParserConfigurationException, SAXException
	{
		// inserts the DTD declaration at the top of the XML text
		adlDescription = new String(new JavaResource(NewFactory.class, "xmlheader.txt").getByteArray()) + "\n\n" + adlDescription;

		// parse XML
		return XMLUtilities.xml2node(adlDescription, validate);
	}

}
