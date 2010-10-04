package org.objectweb.proactive.core.component.adl.luc;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import lucci.io.FileUtilities;
import lucci.io.JavaResource;
import lucci.io.Utilities;
import lucci.text.TextUtilities;
import lucci.text.xml.XMLNode;
import lucci.text.xml.XMLUtilities;

import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.adl.luc.description.BindingDescription;
import org.objectweb.proactive.core.component.adl.luc.description.ComponentDescription;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Contingency;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Role;
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

		// inserts the DTD declaration at the top of the XML text
		 adlDescription = new String(new JavaResource(NewFactory.class,
		 "xmlheader.txt").getByteArray()) + "\n\n" + adlDescription;

		// parse XML
		XMLNode node = XMLUtilities.xml2node(adlDescription, false);

		// resolve variables ${variable_name} -> value
		resolveVariablesInNode(node, parseArgumentsValueFile(argumentFile));
		System.out.println(node);
		// semantic analysis
		ComponentDescription componentDescription = ComponentDescription.createComponentDescription(node);
		componentDescription.setFile(adlFile);

		// intantiation and initializion out of the description
		return createComponent(componentDescription, deploymentFile);
	}

	public Component createComponent(ComponentDescription componentDescription, File deploymentFile) throws ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		// verifies that everything in the description and all sub-description
		componentDescription.check();

		// create the Fractal "type" for the component, necessary to instantiate
		// the component
		ComponentType type = createType(componentDescription);
		Component component = instantiateComponent(componentDescription, type);
		GCM.getNameController(component).setFcName(componentDescription.getName());
		Map<String, Component> name_comp = setSubComponents(componentDescription, component, deploymentFile);
		name_comp.put(componentDescription.getName(), component);
		bindAllInterfaces(componentDescription, name_comp);
		return component;
	}

	private void bindAllInterfaces(ComponentDescription componentDescription, Map<String, Component> comp_child) throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException
	{
		for (BindingDescription bd : componentDescription.getAllBindingDescriptions())
		{
			InterfaceDescription clientInterfaceDescription = componentDescription.findInterfaceDescription(bd.getClient());
			Component clientComp = comp_child.get(clientInterfaceDescription.getParentComponentDescription().getName());

			InterfaceDescription serverInterfaceDescription = componentDescription.findInterfaceDescription(bd.getServer());
			Component serverComp = comp_child.get(serverInterfaceDescription.getParentComponentDescription().getName());
			Object serverIterface = serverComp.getFcInterface(serverInterfaceDescription.getName());

			GCM.getBindingController(clientComp).bindFc(clientInterfaceDescription.getName(), serverIterface);
		}
	}

	private Map<String, Component> setSubComponents(ComponentDescription componentDescription, Component component, File deploymentFile) throws ADLException, IllegalContentException, IllegalLifeCycleException, NoSuchInterfaceException, InstantiationException, IllegalBindingException
	{
		Map<String, Component> comp_child = new HashMap<String, Component>();

		for (ComponentDescription subD : componentDescription.getAllSubcomponentDescriptions())
		{
			Component child = createComponent(subD, deploymentFile);
			GCM.getContentController(component).addFcSubComponent(child);
			comp_child.put(GCM.getNameController(child).getFcName(), child);
		}

		return comp_child;
	}

	private Component instantiateComponent(ComponentDescription componentDescription, ComponentType type) throws InstantiationException, NoSuchInterfaceException
	{
		GenericFactory gf = GCM.getGenericFactory(Utils.getBootstrapComponent());

		if (componentDescription.getMembraneDescription() == null)
		{
			// if the component is primitive
			String s = componentDescription.getAllSubcomponentDescriptions().isEmpty() ? Constants.PRIMITIVE : Constants.COMPOSITE;
			return gf.newFcInstance(type, s, componentDescription.getContent() == null ? null : componentDescription.getContent().getName());
		}
		else
		{
			String desc = componentDescription.getMembraneDescription().getDesc();

			if (desc.equals(Constants.PRIMITIVE) || desc.equals(Constants.COMPOSITE))
			{
				return gf.newFcInstance(type, desc, componentDescription.getContent().getName());
			}
			else
			{
				// this refers to an external controller config
				ControllerDescription cd = new ControllerDescription("parametricPrimitive", componentDescription.getAllSubcomponentDescriptions().isEmpty() ? Constants.PRIMITIVE : Constants.COMPOSITE, desc, false);
				return gf.newFcInstance(type, cd, componentDescription.getContent().getName());
			}
		}
	}

	private ComponentType createType(ComponentDescription componentDescription) throws InstantiationException, NoSuchInterfaceException
	{
		GCMTypeFactory typeFactory = GCM.getGCMTypeFactory(Utils.getBootstrapComponent());
		List<InterfaceType> interfaceTypes = new ArrayList<InterfaceType>();

		for (InterfaceDescription id : componentDescription.getAllInterfaceDescriptions())
		{
			interfaceTypes.add(typeFactory.createGCMItfType(id.getName(), id.getSignature().getName(), id.getRole() == Role.CLIENT, id.getContingency() == Contingency.OPTIONAL, id.getCardinality().name()));
		}

		return typeFactory.createFcType(interfaceTypes.toArray(new InterfaceType[0]));
	}

	public void resolveVariablesInNode(XMLNode node, Map<String, String> argumentValues) throws ADLException
	{
		// for every attribute of the node
		for (String attrName : node.getAttributes().keySet())
		{
			// get the raw value for the attribute
			String attrValue = node.getAttributes().get(attrName);

			// replace the raw value by the evaluated one
			node.getAttributes().put(attrName, TextUtilities.replaceVariableValues(attrValue, argumentValues));
		}

		for (XMLNode c : node.getChildren())
		{
			resolveVariablesInNode(c, argumentValues);
		}
	}



	public Map<String, String> parseArgumentsValueFile(File argFile) throws IOException
	{
		if (argFile == null || !argFile.exists())
		{
			return new HashMap<String, String>();
		}
		else
		{
			return Utilities.loadPropertiesToMap(new String(FileUtilities.getFileContent(argFile)));
		}
	}
}
