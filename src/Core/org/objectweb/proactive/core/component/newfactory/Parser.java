package org.objectweb.proactive.core.component.newfactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import lucci.io.FileUtilities;
import lucci.io.JavaResource;
import lucci.io.Utilities;
import lucci.text.xml.XMLNode;
import lucci.text.xml.XMLUtilities;

public class Parser
{

	public XMLNode parseADL(String adlDescription, boolean validate) throws IOException, ParserConfigurationException, SAXException
	{
		// gets the header text, which include de DTD declaration
		String header = new String(new JavaResource(getClass(), "xmlheader.txt").getByteArray());

		// gets the URL to the DTD
		URL dtdURL = new JavaResource(getClass(), "adl.dtd").getURL();

		// and put it into the header
		header = header.replace("${url}", dtdURL.toExternalForm());
		System.out.println("XML header: " + header);
		// inserts the header at the top of the XML text
		// adlDescription = header + "\n\n" + adlDescription;

		// and parse the whose XML
		return XMLUtilities.xml2node(adlDescription, validate);
	}

	public Map<String, String> parseArgumentsValueFile(File argFile) throws IOException
	{
		return Utilities.loadPropertiesToMap(new String(FileUtilities.getFileContent(argFile)));
	}

	public Map<String, String> parseArgumentsXMLAttribute(String s)
	{
		Map<String, String> m = new HashMap<String, String>();

		if (s != null)
		{
			for (String arg : s.split(" *, *"))
			{
				int p = arg.indexOf('=');

				// if there is no default value
				if (p < 0)
				{
					m.put(arg, null);
				}
				else
				{
					String name = arg.substring(0, p);
					String defaultValue = arg.substring(p + 1);
					m.put(name, defaultValue);
				}
			}
		}

		return m;
	}
}
