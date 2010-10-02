package org.objectweb.proactive.core.component.adl.luc;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import lucci.io.FileUtilities;
import lucci.io.Utilities;
import lucci.text.xml.XMLNode;

public class Arguments
{
	public static void apply(Map<String, String> argumentValues, XMLNode node) throws ADLException
	{
		for (String n : argumentValues.keySet())
		{
			String v = node.getAttributes().get(n);

			if (v == null)
			{
				throw new ADLException("no value was found for argument " + n);
			}
			else
			{
				replaceArgument(v, argumentValues);
			}
		}

		for (XMLNode c : node.getChildren())
		{
			apply(argumentValues, c);
		}
	}

	public static String replaceArgument(String v, Map<String, String> argumentValues)
	{
		for (String a : argumentValues.keySet())
		{
			v = v.replace("${" + a + "}", argumentValues.get(a));
		}

		return v;
	}
	
	public static Map<String, String> parseArguments(File argFile) throws IOException
	{
		return Utilities.loadPropertiesToMap(new String(FileUtilities.getFileContent(argFile)));
	}
}
