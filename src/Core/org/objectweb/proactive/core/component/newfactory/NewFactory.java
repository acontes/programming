package org.objectweb.proactive.core.component.newfactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import lucci.io.FileUtilities;
import lucci.text.xml.XMLNode;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.newfactory.description.ComponentDescription;
import org.xml.sax.SAXException;

public class NewFactory
{
	private Parser lexicalAnalyzer = new Parser();
	private SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
	private Backend backend = new Backend();

	public NewFactory()
	{
		if (System.getProperty("gcm.provider") == null)
		{
			System.setProperty("gcm.provider", Fractive.class.getName());
		}
	}

	public Component createComponent(File adlFile, File argumentFile, File deploymentFile) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException, ADLException
	{
		// read the arguments file, if given
		Map<String, String> args = argumentFile == null ? new HashMap<String, String>() : getLexicalAnalyzer().parseArgumentsValueFile(argumentFile);

		return createComponent(adlFile, args, deploymentFile);
	}

	public Component createComponent(File adlFile, Map<String, String> argValues, Object deploymentDescriptor) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException, ADLException
	{
		// parses XML
		XMLNode node = getLexicalAnalyzer().parseADL(new String(FileUtilities.getFileContent(adlFile)), false);

		// parses the "arguments" attribute to get the declared arguments and their potential default values
		Map<String, String> argSpecs = getLexicalAnalyzer().parseArgumentsXMLAttribute(node.getAttributes().get("arguments"));
		Map<String, String> args = new HashMap<String, String>(argSpecs);

		// replaces within argSpecs the default value (possibly null) by its value as it it given by the user
		for (String arg : argSpecs.keySet())
		{
			String value = argValues.get(arg);
			String defaultValue = argSpecs.get(arg);

			if (value != null)
			{
				args.put(arg, value);
			}
			else if (defaultValue == null)
			{
				throw new ADLException("no value was given for argument " + arg + " and no default value is available");
			}
		}

		getSemanticAnalyzer().resolveVariablesInNode(node, args);

		// semantic analysis
		ComponentDescription componentDescription = getSemanticAnalyzer().createComponentDescription(node);
		componentDescription.getArguments().putAll(argSpecs);
		componentDescription.setFile(adlFile);

		// intantiation and initializion out of the description
		return getBackend().createComponent(componentDescription, null);
	}

	
	public Backend getBackend()
	{
		return backend;
	}

	public void setBackend(Backend backend)
	{
		if (backend == null) throw new NullPointerException();

		this.backend = backend;
	}

	public Parser getLexicalAnalyzer()
	{
		return lexicalAnalyzer;
	}

	public void setLexicalAnalyzer(Parser lexicalAnalyzer)
	{
		if (lexicalAnalyzer == null) throw new NullPointerException();

		this.lexicalAnalyzer = lexicalAnalyzer;
	}

	public SemanticAnalyzer getSemanticAnalyzer()
	{
		return semanticAnalyzer;
	}

	public void setSemanticAnalyzer(SemanticAnalyzer semanticAnalyzer)
	{
		if (semanticAnalyzer == null) throw new NullPointerException();

		this.semanticAnalyzer = semanticAnalyzer;
	}

}
