package org.objectweb.proactive.core.component.adl.luc;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import lucci.io.FileUtilities;
import lucci.text.xml.XMLNode;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.adl.luc.description.ComponentDescription;
import org.xml.sax.SAXException;

public class NewFactory
{
	public static void main(String... args) throws IOException, ParserConfigurationException, SAXException, ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException, org.objectweb.proactive.core.component.adl.luc.ADLException
	{
		File adlFile = new File("src/Core/org/objectweb/proactive/core/component/adl/luc/demo/demo.fractal");
		File argFile = new File("src/Core/org/objectweb/proactive/core/component/adl/luc/demo/demo.fractal.args");
		Component component = new NewFactory().createComponent(adlFile, argFile, null);
		GCM.getLifeCycleController(component).startFc();
		System.out.println(((List<?>) component.getFcInterface("r")).size());
	}

	private SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
	private LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
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
		String adlDescription = new String(FileUtilities.getFileContent(adlFile));
		
		// parse XML
		XMLNode node = getLexicalAnalyzer().parse(adlDescription, false);

		System.out.println(node);
		// semantic analysis
		ComponentDescription componentDescription = getSemanticAnalyzer().createComponentDescription(node);
		componentDescription.setFile(adlFile);

		// read the arguments file
		Map<String, String> args = getLexicalAnalyzer().parseArgumentsValueFile(argumentFile);

		// intantiation and initializion out of the description
		return getBackend().createComponent(componentDescription, args, null);
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



	public LexicalAnalyzer getLexicalAnalyzer()
	{
		return lexicalAnalyzer;
	}

	public void setLexicalAnalyzer(LexicalAnalyzer lexicalAnalyzer)
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
