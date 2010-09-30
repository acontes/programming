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
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.adl.luc.demo.ExampleComponent;
import org.objectweb.proactive.core.component.adl.luc.description.BindingDescription;
import org.objectweb.proactive.core.component.adl.luc.description.ComponentDescription;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Contingency;
import org.objectweb.proactive.core.component.adl.luc.description.InterfaceDescription.Role;
import org.xml.sax.SAXException;

public class SimplestGCMFactory
{
	public static void main(String... args) throws IOException, ParserConfigurationException, SAXException, ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		SimplestGCMFactory f = new SimplestGCMFactory();
		String xml = new String(new JavaResource(SimplestGCMFactory.class, "ExampleComponent.fractal").getByteArray());
		ExampleComponent component = (ExampleComponent) f.createComponent(xml);
		component.printOk();
	}

	public Component createComponent(JavaResource resource) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, ADLException, ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		return createComponent(new String(resource.getByteArray()));
	}

	public Component createComponent(File xmlFile) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, ADLException, ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		return createComponent(new String(FileUtilities.getFileContent(xmlFile)));
	}

	public Component createComponent(String xml) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, ADLException, ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		return createComponent(XMLUtilities.xml2node(xml, false));
	}

	public Component createComponent(XMLNode node) throws ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		return createComponent(ComponentDescription.createComponentDescription(node));
	}

	public Component createComponent(ComponentDescription componentDescription) throws ADLException, InstantiationException, NoSuchInterfaceException, IllegalContentException, IllegalLifeCycleException, IllegalBindingException
	{
		componentDescription.check();
		ComponentType type = createType(componentDescription);
		Component component = instantiateComponent(componentDescription, type);
		GCM.getNameController(component).setFcName(componentDescription.getName());
		Map<String, Component> comp_child = setSubComponents(componentDescription, component);
		comp_child.put("this", component);
		bindAllInterfaces(componentDescription, comp_child);
		return component;
	}

	private void bindAllInterfaces(ComponentDescription componentDescription, Map<String, Component> comp_child) throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException
	{
		for (BindingDescription bd : componentDescription.getBindingDescriptions())
		{
			InterfaceDescription clientInterfaceDescription = componentDescription.findInterfaceDescription(bd.getClient());
			InterfaceDescription serverInterfaceDescription = componentDescription.findInterfaceDescription(bd.getServer());
			Component clientComp = comp_child.get(clientInterfaceDescription.getParentComponentDescription().getName());
			Component serverComp = comp_child.get(serverInterfaceDescription.getParentComponentDescription().getName());
			Object serverIterface = serverComp.getFcInterface(serverInterfaceDescription.getName());
			GCM.getBindingController(clientComp).bindFc(clientInterfaceDescription.getName(), serverIterface);
		}
	}

	private Map<String, Component> setSubComponents(ComponentDescription componentDescription, Component component) throws ADLException, IllegalContentException, IllegalLifeCycleException, NoSuchInterfaceException, InstantiationException, IllegalBindingException
	{
		Map<String, Component> comp_child = new HashMap<String, Component>();

		for (ComponentDescription subD : componentDescription.getSubcomponentDescriptions())
		{
			Component child = createComponent(subD);
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
			String s = componentDescription.getSubcomponentDescriptions().isEmpty() ? Constants.PRIMITIVE : Constants.COMPOSITE;
			return gf.newFcInstance(type, s, componentDescription.getContent().getName());
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
				ControllerDescription cd = new ControllerDescription("parametricPrimitive", componentDescription.getSubcomponentDescriptions().isEmpty() ? Constants.PRIMITIVE : Constants.COMPOSITE, desc, false);
				return gf.newFcInstance(type, cd, componentDescription.getContent().getName());
			}
		}
	}

	private ComponentType createType(ComponentDescription componentDescription) throws InstantiationException, NoSuchInterfaceException
	{
		GCMTypeFactory typeFactory = GCM.getGCMTypeFactory(Utils.getBootstrapComponent());
		List<InterfaceType> interfaceTypes = new ArrayList<InterfaceType>();

		for (InterfaceDescription id : componentDescription.getInterfaceDescriptions())
		{
			interfaceTypes.add(typeFactory.createGCMItfType(id.getName(), id.getSignature().getName(), id.getRole() == Role.CLIENT, id.getContingency() == Contingency.OPTIONAL, id.getCardinality().name()));
		}

		return typeFactory.createFcType(interfaceTypes.toArray(new InterfaceType[0]));
	}
	
}
